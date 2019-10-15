## Jaeger tracer

Run Jaeger tracer:
```
docker run -it --rm -p 6831:6831/udp -p 16686:16686 jaegertracing/all-in-one
```

Open [Jaeger UI](http://localhost:16686/search).
