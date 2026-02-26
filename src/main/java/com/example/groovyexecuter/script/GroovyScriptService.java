package com.example.groovyexecuter.script;

import com.example.groovyexecuter.GroovyExecutionException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class GroovyScriptService {
    private final GroovyScriptMapper mapper;
    private final ObjectMapper objectMapper;

    public GroovyScriptService(GroovyScriptMapper mapper, ObjectMapper objectMapper) {
        this.mapper = mapper;
        this.objectMapper = objectMapper;
    }

    public List<GroovyScript> list() {
        return mapper.findAll();
    }

    public GroovyScript get(Long id) {
        GroovyScript script = mapper.findById(id);
        if (script == null) {
            throw new GroovyExecutionException("脚本不存在: id=" + id);
        }
        return script;
    }

    public GroovyScript create(ScriptUpsertRequest request) {
        GroovyScript script = new GroovyScript();
        script.setName(request.getName());
        script.setDescription(request.getDescription());
        script.setContent(request.getContent());
        script.setExecuteArgsJson(normalizeArgsText(request.getExecuteArgsJson()));
        mapper.insert(script);
        return get(script.getId());
    }

    public GroovyScript update(Long id, ScriptUpsertRequest request) {
        GroovyScript existing = get(id);
        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setContent(request.getContent());
        existing.setExecuteArgsJson(normalizeArgsText(request.getExecuteArgsJson()));
        mapper.update(existing);
        return get(id);
    }

    public void delete(Long id) {
        int affected = mapper.deleteById(id);
        if (affected == 0) {
            throw new GroovyExecutionException("脚本不存在: id=" + id);
        }
    }

    public ParsedArgs parseSavedArgs(GroovyScript script) {
        return parseArgsText(script.getExecuteArgsJson());
    }

    public List<String> toArgsFromContext(Object argsContext) {
        if (argsContext == null) {
            return List.of();
        }
        if (argsContext instanceof List<?> list) {
            return list.stream().map(this::stringifyValue).toList();
        }
        return List.of(stringifyValue(argsContext));
    }

    private String normalizeArgsText(String argsText) {
        if (argsText == null || argsText.isBlank()) {
            return "[]";
        }
        return argsText;
    }

    private ParsedArgs parseArgsText(String argsText) {
        String raw = argsText == null ? "" : argsText.trim();
        if (raw.isBlank()) {
            return new ParsedArgs(List.of(), List.of());
        }

        try {
            Object parsed = objectMapper.readValue(raw, Object.class);
            return new ParsedArgs(toArgsFromContext(parsed), parsed);
        } catch (IOException e) {
            return new ParsedArgs(List.of(raw), raw);
        }
    }

    private String stringifyValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String text) {
            return text;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (IOException e) {
            throw new GroovyExecutionException("执行参数转换失败。", e);
        }
    }

    public record ParsedArgs(List<String> args, Object context) {}
}
