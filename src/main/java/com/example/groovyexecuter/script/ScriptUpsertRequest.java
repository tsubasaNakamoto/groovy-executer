package com.example.groovyexecuter.script;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ScriptUpsertRequest {
    @NotBlank
    @Size(max = 100)
    private String name;
    @Size(max = 255)
    private String description;
    @NotBlank
    private String content;
    private String executeArgsJson;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExecuteArgsJson() {
        return executeArgsJson;
    }

    public void setExecuteArgsJson(String executeArgsJson) {
        this.executeArgsJson = executeArgsJson;
    }
}
