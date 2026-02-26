package com.example.groovyexecuter;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

public class ExecuteRequest {
    @NotBlank
    private String script;
    private List<String> args = new ArrayList<>();
    private Object argsContext;
    @Min(1)
    @Max(120)
    private Integer timeoutSeconds = 10;

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args = args == null ? new ArrayList<>() : args;
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
