package com.example.groovyexecuter;

public class ExecuteResponse {
    private int exitCode;
    private String stdout;
    private String stderr;
    private boolean timedOut;

    public ExecuteResponse(int exitCode, String stdout, String stderr, boolean timedOut) {
        this.exitCode = exitCode;
        this.stdout = stdout;
        this.stderr = stderr;
        this.timedOut = timedOut;
    }

    public int getExitCode() {
        return exitCode;
    }

    public String getStdout() {
        return stdout;
    }

    public String getStderr() {
        return stderr;
    }

    public boolean isTimedOut() {
        return timedOut;
    }
}
