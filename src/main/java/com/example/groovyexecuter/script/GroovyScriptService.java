package com.example.groovyexecuter.script;

import com.example.groovyexecuter.GroovyExecutionException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroovyScriptService {
    private final GroovyScriptMapper mapper;

    public GroovyScriptService(GroovyScriptMapper mapper) {
        this.mapper = mapper;
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
        mapper.insert(script);
        return get(script.getId());
    }

    public GroovyScript update(Long id, ScriptUpsertRequest request) {
        GroovyScript existing = get(id);
        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setContent(request.getContent());
        mapper.update(existing);
        return get(id);
    }

    public void delete(Long id) {
        int affected = mapper.deleteById(id);
        if (affected == 0) {
            throw new GroovyExecutionException("脚本不存在: id=" + id);
        }
    }
}
