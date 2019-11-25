# Tracing einer Java-Servicelandschaft

*Mikroservices haben in den letzten Jahren einen Aufschwung verteilter Systeme eingeleitet.
Eine der Herausforderungen bei der Entwicklung eines verteilten Systems ist sicherzustellen,
dass es zu debuggen ist.*

Ein verteiltes System zu debuggen kann ganz schön mühsam sein.
Integrierte Systeme haben einen Stacktrace. Tritt ein Fehler auf, ist diese Aufrufhierarchie ein wichtiger Baustein, um die Fehlerursache einzugrenzen. In verteilten Systemen geht der Stacktrace aber zwischen den Systemaufrufen verloren.
Integrierte Systeme schreiben in ein Log. Der Threadname ist eine einfache Möglichkeit, die einzelnen Lognachrichten einer Arbeitseinheit zuzuordnen. Die Logs der Services in einer Systemlandschaft müssen dagegen erst einmal zusammengeführt werden. Und die Möglichkeit die einzelnen Logeinträge über die Systemgrenzen hinweg einer Arbeitseinheit zuzuordnen, muss erst einmal geschaffen werden

Letztlich sind Mikroservices verteilte Systeme, und die Entwicklung verteilter Systeme ist nicht leicht. Microservices wurden in den letzten Jahren romantisiert, und das hat diese Tatsache in den Hintergrund treten lassen.
Was in integrierten Systemen (ich vermeide den abwertenden Begriff Monolith) relativ leicht zu erreichen ist, ist in verteilten Systemen nicht so trivial. 
Die Entwicklung muss also eine zusätzliche Anstrengung unternehmen, um die Vorgänge in ihrem System sichtbar zu machen. Tracing-Systeme wie [Jaeger](https://www.jaegertracing.io) oder [Zipkin](https://zipkin.io) können dabei unterstützen.

![Bild Ein Trace](screenshots/EinTrace.png)

Hier macht eine Jaeger-Oberfläche sichtbar, dass die Greet-Resource zur Abarbeitung der Anfrage einen weiteren Dienst, die Time-Of-Day-Resource, abgefragt hat. Bevor sie die Anfrage beantwortet, veröffentlicht sie noch eine Nachricht auf einem Topic. Erst in einem nachgelagerten Prozess wird die Nachricht verarbeitet.

Im Fehlerfall ist in der Oberfläche nicht nur sichtbar, wo der Fehler entstanden ist, sondern auch welcher Geschäftsvorgang beeinträchtigt wurde:
![Bild Ein Trace mit Fehler](screenshots/EinTraceMitFehler.png)

Um diese Informationen zu erhalten, müssen die Microservices ein Tracing-Backend mit Daten über die einzelnen Arbeitsschritte versorgen:

    -----------------------+      span: A (trace-id: 1)    +-----------------+
    | Greet-Resource       |    ------------------------>  |                 |
    -----------------------+                               |                 |
              |                                            |                 |
              | /time-of-day                               |                 |
              | (trace-id: 1)                              | Tracing-Backend |
              |                                            |                 |
             \|/                                           |                 |
    +----------------------+     span: B (trace-id: 1)     |                 |
    | Time-Of-Day-Resource |   ------------------------->  |                 |
    +----------------------+                               +-----------------+

Jeder der Dienste informiert das Tracingsystem über die Abarbeitung einer Arbeitseinheit, den sogenannten Span. Im Beispiel gibt es zwei Spans: die Abfrage der Greet-Resource, und die daraus folgende Anfrage an die Time-Of-Day-Resource. Um die einzelnen Spans derselben Arbeitseinheit zuzuordnen, dem sogenannten Trace, dient die Trace-ID. Die Abfrage der Greet-Resource ist der erste Span im Geschäftsvorgang. Diese Einheit legt also die ID fest, und übermittelt sie nicht nur an das Tracingsystem, sondern auch, wenn sie die Time-Of-Day-Resource aufruft, damit der zweite beteiligte Dienst seine Spans unter denselben Trace einordnen kann.

Um die Aufrufe zwischen den Systemen mit der Trace-ID anzureichern und die Spans an das Tracingsystem zu melden, gibt es im wesentlichen drei Ansätze:
1. Einarbeitung des Tracings in den Produktivcode
2. Einsatz eines transparent arbeitenden Tracing-Agenten
3. Anwendung des Sidecar-Patterns

Ich möchte das Tracing anhand einer Beispielanwendung ausprobieren.
Dabei beschränke ich mich auf Microservices, die in einer Java-Systemlandschaft implementiert sind. Die Schnittstellen zwischen den Services nutzen REST für synchrone Aufrufe und JMS (Java Messaging System) für asynchrone.
In der Wahl der Mittel beschränke ich mich auf die Einarbeitung des Tracings in den Quellcode der Anwendung. Die Nutzung eines Tracing-Agenten werde ich streifen, aber nicht praktisch erproben. Die Anwendung des Sidecar-Patterns und der Einsatz eines Service-Mesh, wäre für diese kleine Studie zu umfangreich.

#### Zielbild

Im Tracing der Beispielapplikation soll sichtbar werden, dass die Greet-Resource die Time-Of-Day-Resource aufruft. Dieser Aufruf soll in einem Eltern-Kind-Verhältnis stehen. Die Abfrage der Time-Of-Day-Resource ist ein Teil der Abarbeitung der Anfrage an die Greet-Resource (Elternprozess).

    |------------+-----/greet----------+----------|
      (child-of) |                     |  
                 |----/time-of-day-----|  

Eine Eltern-Kind-Beziehung bildet synchrone Aufrufe treffend ab. Für asynchrone Nachverarbeitungen passt eine Folgt-Aus-Beziehung besser. Die Verarbeitung der Nachricht auf dem Greet-Topic soll aus aus dem Aufruf der Greet-Resource folgen:

       |--+-----+-- /greet --+-------------+--|
       |     |            |             |    (follows-from)
         ...              |-- publish --|        >>>>>      |-- consume --|

## Einarbeitung des Tracings in den Produktivcode

In der Studie wird die [hier](app/) entworfene Applikation unter zwei verschiedenen Technologiestacks einem Tracing unterworfen:
- Als JEE-Applikation im [Microprofile-Opentracing](https://github.com/eclipse/microprofile-opentracing/blob/master/spec/src/main/asciidoc/microprofile-opentracing.asciidoc)-konformen Applikationsserver [Thorntail](https://thorntail.io) und [Jaeger](https://www.jaegertracing.io) als Tracingsystem
- Als Spring-Boot-Applikation mit [Spring Cloud Sleuth](https://cloud.spring.io/spring-cloud-sleuth/reference/html/) und [Zipkin](https://zipkin.io) als Tracingbackend

##### Voraussetzungen

Um die Beispiele nachzuvollziehen, benötigt man:
- Java 8 und  [Maven 3](https://maven.apache.org) für die Beispielanwendung
- [Docker](https://www.docker.com) für die Tracing-Backends

### Microprofile-Opentracing

Das [Microprofile-Opentracing](https://github.com/eclipse/microprofile-opentracing/blob/master/spec/src/main/asciidoc/microprofile-opentracing.asciidoc) setzt Opentracing für den JEE-Stack um. [Opentracing](https://opentracing.io) definiert einen Standard, wie Microservices mit einem Tracingsystem kommunizieren.

#### Wie wird das Tracing aktiviert?

Um das Microprofile in Thorntail zu aktivieren, müssen die folgenden Abhängigkeiten eingebunden werden. Hier der Ausschnitt aus dem [pom.xml](opentracing/pom.xml):

    <dependency>
      <groupId>io.thorntail</groupId>
      <artifactId>microprofile-opentracing</artifactId>
    </dependency>
    <dependency>
      <groupId>io.thorntail</groupId>
      <artifactId>jaeger</artifactId>
    </dependency>

In der [Konfiguration](opentracing/src/main/resources/project-defaults.yml) der Anwendung muss das Tracing mit Jaeger noch aktiviert werden:

    thorntail:  
      jaeger:  
        service-name: greeter  
        sampler-type: const  
        sampler-parameter: 1


#### Wie lassen sich synchrone REST-Aufrufe verfolgen?

Das Microprofile legt fest, dass JAX-RS-Applikationen von der Laufzeitumgebung instrumentiert werden müssen. Deswegen muss kein Code geschrieben werden, um sichtbar zu machen, dass die Time-Of-Day-Resource von der Greet-Resource genutzt wird. Ruft eine JAX-RS-Resource ein weitere Resource synchron auf, dann wird der Aufruf als Kind des Aufruferprozesses dargestellt.
Damit das aber funktioniert, muss ein spezieller JAX-RS-Client verwendet werden, wenn andere Microservices angefragt werden. Nur so ist sichergestellt, dass der aktuelle Kontext an den aufgerufenen Dienst übertragen wird. Im Beispiel erledigt das der  [TracingJaxRSClientProducer](opentracing/src/main/java/de/kieseltaucher/studies/tracing/tracing/TracingJaxRSClientProducer.java).

#### Wie lassen sich asynchrone Aufrufe über JMS verfolgen?

Das Microprofile macht keine Vorgaben, wie Aufrufe über JMS (Java Messaging Service) instrumentiert werden sollen. Es bietet aber eine [Registratur](https://opentracing.io/registry/), in der man nach ergänzenden Bibliotheken suchen kann. Dort finden sich beispielsweise Instrumentierungsbibliotheken für JDBC, oder eben auch [java-jms](https://opentracing.io/registry/java-jms/). Man sollte bei der Auswahl aber Vorsicht walten lassen, da eine Bibliothek nicht ausgereift sein muss, um aufgenommen zu werden.
In dieser Studie habe ich mich entschieden, die Instrumentierung selbst zu implementieren. Die Klasse [JMSContextMediator](opentracing/src/main/java/de/kieseltaucher/studies/tracing/tracing/JMSContextMediator.java) kann den aktuellen Tracingkontext in eine zu sendende Nachricht einbetten. Auf der Empfängerseite stellt sie eine Folgt-Aus-Beziehung her.

#### Wie kann das Tracing mit Debuginformationen angereichert werden?

Das Microprofile stellt eine Tracerkomponente bereit. Diese kann man nutzen, um den aktuellen Span zu [annotieren](https://javadoc.io/static/io.opentracing/opentracing-api/0.31.0/io/opentracing/Span.html#log-java.util.Map-). Im [TraceLoggerImpl](opentracing/src/main/java/de/kieseltaucher/studies/tracing/tracing/TraceLoggerImpl.java) ist das beispielhaft implementiert.

#### Wie kann ich das ausprobieren?

Die Beispielanwendung im Modul opentracing läuft auf dem Applikationsserver Thorntail.
Sie wird folgendermaßen gestartet:

    mvn install
    cd opentracing
    mvn thorntail:run

Anschließend muss noch der Jaeger-Tracer zum Laufen gebracht werden:

    docker run -it --rm -p 14268:14268 -p 6831:6831/udp -p 16686:16686 jaegertracing/all-in-one

Nun kann man die [Greet-Resource](http://localhost:8080/hello) abfragen und in der [Jaeger-UI](http://localhost:16686/search) nach Traces suchen:

![Trace in Jaeger](screenshots/EinTrace.png)

### Spring Cloud - Sleuth

[Spring Cloud Sleuth](https://cloud.spring.io/spring-cloud-sleuth/reference/html/) instrumentiert Aufrufe über JMS und REST von Haus aus (und noch sehr viel [mehr](https://cloud.spring.io/spring-cloud-sleuth/reference/html/#integrations)). Als Dreingabe reichert es das Logging mit der Tracing-ID an. Das erleichert es, die Traces mit den Logausgaben zu korrelieren.

#### Wie wird das Tracing aktiviert?

Um das Tracing zu aktivieren, muss die Bibliothek spring-cloud-starter-zipkin in die Applikation eingebunden werden. Hier der Ausschnitt aus dem [pom.xml](sleuth/pom.xml):

    <dependency>  
      <groupId>org.springframework.cloud</groupId>  
      <artifactId>spring-cloud-starter-zipkin</artifactId>
    </dependency>

In der Standardeinstellung meldet Sleuth lediglich jeden zehnten Aufruf an das Tracing-Backend. Diese Stichproben sind sinnvoll, um etwa durchschnittliche Antwortzeiten zu ermitteln, ohne die Anwendung mit dem Tracing über die Gebühr zu belasten. Sie eignet sich aber nicht zum Debuggen. Um vollumfänglich zu protokollieren, kann dieser Wert in der [Applikationskonfiguration](sleuth/src/main/resources/application.yml) überschrieben werden:

    sleuth.sampler.probability = 1

#### Wie lassen sich synchrone REST-Aufrufe verfolgen?

Es ist nichts weiter zu tun.

#### Wie lassen sich asynchrone Aufrufe über JMS verfolgen?

Es ist nichts weiter zu tun.

#### Wie kann das Tracing mit Debuginformationen angereichert werden?

Ähnlich wie unter Opentracing lassen sich die aktuellen Spans annotieren. Auch Sleuth stellt eine Tracerkomponente bereit, die [Schnittstelle](https://www.javadoc.io/static/io.zipkin.brave/brave/5.9.0/brave/SpanCustomizer.html#tag\(java.lang.String,java.lang.String) ist nur geringfügig anders.
Die Klasse[TraceLoggerImpl](sleuth/src/main/java/de/kieseltaucher/studies/tracing/tracing/TraceLoggerImpl.java) zeigt ihre Nutzung.

Da Sleuth die IDs der Traces zum Mapped Diagnostic Context (MDC) von SLF4J hinzufügt, lassen sich die Traces noch dazu leicht den Logausgaben der Applikation zuordnen:
![Logging](screenshots/EinLoggingMitMDC.png)

#### Wie kann ich das ausprobieren?

Die Beispielanwendung im Modul sleuth läuft mit Spring-Boot und wird folgendermaßen gestartet:

    mvn package
    cd sleuth
    mvn spring-boot:run

Der Tracer Zipkin steht als Docker-Image zur Verfügung:

    docker run -p 9411:9411 openzipkin/zipkin

Anschließend kann auf der [Zipkin_UI](http://localhost:9411) nach Traces der [Greet-Resource](http://localhost:8080/hello) gesucht werden:

![Trace in Zipkin](screenshots/EinTraceMitZipkin.png)


## Tracing-Agenten

Man kann eine Java-Anwendung auch überwachen, ohne den Quellcode anzupassen. Denn sie lässt sich über einen sogenannten Java-Agent [instrumentieren](https://docs.oracle.com/en/java/javase/11/docs/api/java.instrument/java/lang/instrument/package-summary.html). Die JVM erlaubt dem Java-Agent den Bytecode einer laufenden Anwendung zu modifizieren. Dadurch wird es möglich, das Tracing zur Laufzeit in die Anwendung einzuschleusen. Die Applikation selbst bleibt unverändert.

Diese Technologie wird von einigen kommerziellen Anbietern angeboten. Darunter sind  [Dynatrace ](https://www.dynatrace.com/support/doc/appmon/installation/set-up-agents/java-agent-configuration/)
und [Datadog](https://docs.datadoghq.com/tracing/setup/java/). Diese Angebote sind umfangreich und dementsprechend kostenpflichtig. Sie umfassen sehr viel mehr als ein Distributed-Tracing, und bieten eine vollumfängliche Überwachung der Servicelandschaft über die technische Infrastruktur, die Middleware, die Logdateien bis hin zu den Applikationen selbst.

Um das Prinzip zu verdeutlichen, nutze ich in der Studie den Opentracing-kompatiblen  Java-Agent [inspectIt Ocelot](https://github.com/inspectIT/inspectit-ocelot).

#### Wie wird das Tracing aktiviert?

Die Konfiguration, die Ocelot verwenden soll, liegt in [diesem](agents/ocelot) Verzeichnis.
Die Konfigurationsdatei [config.properties](agents/ocelot/config.properties) teilt Ocelot mit, wo das Jaeger-Backend lauscht.
Was an das Backend gemeldet werden soll, wird in der Datei [instrumentation.yaml](agents/ocelot/instrumentation.yaml) festgelegt. Das Tracing beschränkt sich auf die synchronen Aufrufe über HTTP (ob auch die asynchronen Aufrufe mit Ocelot abgebildet werden können, habe ich nicht untersucht).

Um die Anwendung zur Laufzeit zu instrumentieren, muss sie mit dem Java-Agent gestartet werden:

    java -Dinspectit.config.file-based.path=ocelot -javaagent:"inspectit-ocelot-agent-0.4.jar" -jar target/kieselt...

Das Argument ``-javaagent`` teilt der JVM mit, dass der Ocelot-Agent gebootet werden soll. Die Umgebungsvariable ``inspectit.config.file-based.path=ocelot`` wiederum dem Ocelot-Agent, wo die Konfiguration für die Instrumentierung zu finden ist.
Man kann die Instrumentierung übrigens zur Laufzeit verändern. Der Agent überwacht das Verzeichnis, und aktualisiert gegebenenfalls seine Konfiguration.

#### Wie kann ich das ausprobieren?

Die Beispielanwendung im Modul agents ist eine Spring-Boot-Applikation und muss folgendermaßen gestartet werden, um das Tracing mit Ocelot zu aktivieren:

    mvn package
    cd agents
    java -Dinspectit.config.file-based.path=ocelot \
         -javaagent:"inspectit-ocelot-agent-0.4.jar" \
         -jar target/kieseltaucher-tracing-agents-1.0.0-SNAPSHOT.jar
    
Anschließend muss noch der Jaeger-Tracer gestartet werden:

    docker run -it --rm -p 14268:14268 -p 6831:6831/udp -p 16686:16686 jaegertracing/all-in-one

Die Traces der  [Greet-Resource](http://localhost:8080/hello) erscheinen als Service "greeter-ocelot" in der [Jaeger-UI](http://localhost:16686/search).
Wie gewünscht sind die Aufrufe der Time-Of-Day-Resource als Kinder der Greet-Resource-Anfragen dargestellt.

