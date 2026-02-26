package com.example.groovyexecuter;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.concurrent.*;

@Service
public class GroovyExecutionService {
    private final ObjectMapper objectMapper;

    public GroovyExecutionService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ExecuteResponse execute(String script, List<String> args, int timeoutSeconds) {
        return execute(script, args, timeoutSeconds, null);
    }

    public ExecuteResponse execute(String script, List<String> args, int timeoutSeconds, Object argsContext) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<ExecuteResponse> future = executor.submit(() -> runScript(script, args, argsContext));

        try {
            return future.get(timeoutSeconds, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            return new ExecuteResponse(-1, "", "\n执行超时（>" + timeoutSeconds + "s）", true);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof GroovyExecutionException ge) {
                throw ge;
            }
            throw new GroovyExecutionException("执行 Groovy 脚本失败。", cause);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new GroovyExecutionException("执行被中断。", e);
        } finally {
            executor.shutdownNow();
        }
    }

    private ExecuteResponse runScript(String script, List<String> args, Object argsContext) {
        StringWriter stdoutWriter = new StringWriter();
        StringWriter stderrWriter = new StringWriter();

        Binding binding = new Binding();
        Object context = normalizeContext(argsContext == null ? args : argsContext);
        binding.setVariable("args", args.toArray(String[]::new));
        binding.setVariable("argsList", args);
        binding.setVariable("context", context);
        binding.setVariable("params", context);
        if (context instanceof List<?> list) {
            binding.setVariable("list", list);
        }
        if (context instanceof JsonMapNode jsonMapNode) {
            binding.setVariable("json", jsonMapNode);
        }
        binding.setVariable("out", new PrintWriter(stdoutWriter, true));
        binding.setVariable("err", new PrintWriter(stderrWriter, true));

        CompilerConfiguration config = new CompilerConfiguration();
        GroovyShell shell = new GroovyShell(binding, config);

        try {
            shell.evaluate(script);
            return new ExecuteResponse(0, stdoutWriter.toString(), stderrWriter.toString(), false);
        } catch (Exception e) {
            String message = e.getMessage() == null ? e.getClass().getName() : e.getMessage();
            if (!stderrWriter.toString().isBlank()) {
                message = stderrWriter.toString() + "\n" + message;
            }
            return new ExecuteResponse(1, stdoutWriter.toString(), message, false);
        }
    }

    private Object normalizeContext(Object rawContext) {
        if (rawContext instanceof List<?> list) {
            return list.stream().map(this::normalizeContext).collect(Collectors.toList());
        }
        if (rawContext instanceof Map<?, ?> map) {
            return wrapMapNode(map);
        }
        return rawContext;
    }

    private JsonMapNode wrapMapNode(Map<?, ?> source) {
        Map<Object, Object> wrapped = new LinkedHashMap<>();

        for (Map.Entry<?, ?> entry : source.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            wrapped.put(key, normalizeContext(value));
        }
        return new JsonMapNode(wrapped);
    }

    private final class JsonMapNode {
        private final Map<Object, Object> delegate;

        private JsonMapNode(Map<Object, Object> delegate) {
            this.delegate = delegate;
        }

        public Object get(Object key) {
            return delegate.get(key);
        }

        public boolean containsKey(Object key) {
            return delegate.containsKey(key);
        }

        public Map<Object, Object> asMap() {
            return delegate;
        }

        @Override
        public String toString() {
            try {
                return objectMapper.writeValueAsString(delegate);
            } catch (JsonProcessingException e) {
                return delegate.toString();
            }
        }
    }
}
