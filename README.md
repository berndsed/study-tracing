## Purpose

This is a sample project to investigate tracing.

## Requirements

* Maven 3
* Java 8
* Docker

## How to build

```
mvn install
```

## How to run

This project contains 3 separate tracing apps.

### Open Tracing (Thorntail) with Jaeger

1. Start the app:

```
cd opentracing
mvn thorntail:run
```

2. Start the Jaeger UI

```
docker run -it --rm -p 14268:14268 -p 6831:6831/udp -p 16686:16686 jaegertracing/all-in-one
```

### Sleuth (Spring) with Zipkin

1. Start the app

```
cd sleuth
mvn spring-boot:run
```

2. Start Zipkin

```
docker run -p 9411:9411 openzipkin/zipkin
```

### Tracing with a Java-Agent (Ocelot)

1. Start the app

```
cd agents
java -Dinspectit.config.file-based.path=ocelot \
     -javaagent:"inspectit-ocelot-agent-0.4.jar" \
     -jar target/kieseltaucher-tracing-agents-1.0.0-SNAPSHOT.jar
```

2. Start the Jaeger UI

```
docker run -it --rm -p 14268:14268 -p 6831:6831/udp -p 16686:16686 jaegertracing/all-in-one
```





