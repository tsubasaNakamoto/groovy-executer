package com.example.groovyexecuter.script;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.List;

public class ScriptExecuteRequest {
    private List<String> args;
    private Object argsContext;

    @Min(1)
    @Max(120)
    private Integer timeoutSeconds;

    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }

    public Object getArgsContext() {
        return argsContext;
    }

    public void setArgsContext(Object argsContext) {
        this.argsContext = argsContext;
    }

    public Integer getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(Integer timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }
}
