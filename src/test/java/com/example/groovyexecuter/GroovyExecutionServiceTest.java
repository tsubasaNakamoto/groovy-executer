package com.example.groovyexecuter;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GroovyExecutionServiceTest {

    private final GroovyExecutionService service = new GroovyExecutionService(new com.fasterxml.jackson.databind.ObjectMapper());

    @Test
    void shouldExecuteScriptInJvm() {
        ExecuteResponse response = service.execute("println \"Hello, ${args.join('-')}\"", List.of("A", "B"), 5);
        assertEquals(0, response.getExitCode());
        assertTrue(response.getStdout().contains("Hello, A-B"));
        assertFalse(response.isTimedOut());
    }

    @Test
    void shouldReturnScriptError() {
        ExecuteResponse response = service.execute("throw new RuntimeException('boom')", List.of(), 5);
        assertEquals(1, response.getExitCode());
        assertTrue(response.getStderr().contains("boom"));
        assertFalse(response.isTimedOut());
    }

    @Test
    void shouldTimeoutLongRunningScript() {
        ExecuteResponse response = service.execute("while(true){}", List.of(), 1);
        assertEquals(-1, response.getExitCode());
        assertTrue(response.isTimedOut());
    }

    @Test
    void shouldPrintJsonStringWhenContextListContainsObject() {
        ExecuteResponse response = service.execute(
                "println context.get(0)",
                List.of("{\"Alice\":12}"),
                5,
                List.of(java.util.Map.of("Alice", 12))
        );
        assertEquals(0, response.getExitCode());
        assertTrue(response.getStdout().contains("{\"Alice\":12}"));
    }

    @Test
    void shouldSupportChainedGetForContextListMap() {
        ExecuteResponse response = service.execute(
                "println context.get(0).get(\"Alice\")",
                List.of("{\"Alice\":12}"),
                5,
                List.of(java.util.Map.of("Alice", 12))
        );
        assertEquals(0, response.getExitCode());
        assertTrue(response.getStdout().contains("12"));
    }
}
