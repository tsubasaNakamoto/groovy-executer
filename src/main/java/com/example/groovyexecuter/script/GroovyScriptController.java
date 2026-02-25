package com.example.groovyexecuter.script;

import com.example.groovyexecuter.ExecuteRequest;
import com.example.groovyexecuter.ExecuteResponse;
import com.example.groovyexecuter.GroovyExecutionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scripts")
public class GroovyScriptController {
    private final GroovyScriptService groovyScriptService;
    private final GroovyExecutionService groovyExecutionService;

    public GroovyScriptController(GroovyScriptService groovyScriptService,
                                  GroovyExecutionService groovyExecutionService) {
        this.groovyScriptService = groovyScriptService;
        this.groovyExecutionService = groovyExecutionService;
    }

    @GetMapping
    public List<GroovyScript> list() {
        return groovyScriptService.list();
    }

    @GetMapping("/{id}")
    public GroovyScript get(@PathVariable Long id) {
        return groovyScriptService.get(id);
    }

    @PostMapping
    public GroovyScript create(@Valid @RequestBody ScriptUpsertRequest request) {
        return groovyScriptService.create(request);
    }

    @PutMapping("/{id}")
    public GroovyScript update(@PathVariable Long id, @Valid @RequestBody ScriptUpsertRequest request) {
        return groovyScriptService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        groovyScriptService.delete(id);
    }

    @PostMapping("/{id}/execute")
    public ExecuteResponse executeById(@PathVariable Long id, @RequestBody(required = false) ExecuteRequest request) {
        GroovyScript script = groovyScriptService.get(id);
        List<String> args = request == null || request.getArgs() == null ? List.of() : request.getArgs();
        int timeoutSeconds = request == null || request.getTimeoutSeconds() == null ? 10 : request.getTimeoutSeconds();
        return groovyExecutionService.execute(script.getContent(), args, timeoutSeconds);
    }
}
