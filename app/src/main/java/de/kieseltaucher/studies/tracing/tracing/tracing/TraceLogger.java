package de.kieseltaucher.studies.tracing.tracing.tracing;

public interface TraceLogger {
    void log(String msg);

    void error(Exception e);
}
