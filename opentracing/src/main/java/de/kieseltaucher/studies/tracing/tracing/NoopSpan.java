package de.kieseltaucher.studies.tracing.tracing;

import java.util.Map;

import io.opentracing.Span;
import io.opentracing.SpanContext;

class NoopSpan implements Span {

    @Override
    public SpanContext context() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Span setTag(String s, String s1) {
        return this;
    }

    @Override
    public Span setTag(String s, boolean b) {
        return this;
    }

    @Override
    public Span setTag(String s, Number number) {
        return this;
    }

    @Override
    public Span log(Map<String, ?> map) {
        return this;
    }

    @Override
    public Span log(long l, Map<String, ?> map) {
        return this;
    }

    @Override
    public Span log(String s) {
        return this;
    }

    @Override
    public Span log(long l, String s) {
        return this;
    }

    @Override
    public Span setBaggageItem(String s, String s1) {
        return this;
    }

    @Override
    public String getBaggageItem(String s) {
        return null;
    }

    @Override
    public Span setOperationName(String s) {
        return this;
    }

    @Override
    public void finish() {
        // A noop has nothing to do
    }

    @Override
    public void finish(long l) {
        // A noop has nothing to do
    }
}
