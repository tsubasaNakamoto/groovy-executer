package com.example.groovyexecuter;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class GroovyExecutionController {
    private final GroovyExecutionService groovyExecutionService;

    public GroovyExecutionController(GroovyExecutionService groovyExecutionService) {
        this.groovyExecutionService = groovyExecutionService;
    }

    @PostMapping("/execute")
    public ExecuteResponse execute(@Valid @RequestBody ExecuteRequest request) {
        Object argsContext = request.getArgsContext() == null ? request.getArgs() : request.getArgsContext();
        return groovyExecutionService.execute(
                request.getScript(),
                request.getArgs(),
                request.getTimeoutSeconds(),
                argsContext
        );
    }
}
