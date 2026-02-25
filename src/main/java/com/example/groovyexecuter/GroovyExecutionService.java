package com.example.groovyexecuter;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.concurrent.*;

@Service
public class GroovyExecutionService {
    public ExecuteResponse execute(String script, List<String> args, int timeoutSeconds) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<ExecuteResponse> future = executor.submit(() -> runScript(script, args));

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

    private ExecuteResponse runScript(String script, List<String> args) {
        StringWriter stdoutWriter = new StringWriter();
        StringWriter stderrWriter = new StringWriter();

        Binding binding = new Binding();
        binding.setVariable("args", args.toArray(String[]::new));
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
}
