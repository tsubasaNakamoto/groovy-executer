package com.example.groovyexecuter;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class GroovyExecutionService {
    public ExecuteResponse execute(String script, List<String> args, int timeoutSeconds) {
        ensureGroovyAvailable();

        Path tempScript = null;
        try {
            tempScript = Files.createTempFile("groovy-script-", ".groovy");
            Files.writeString(tempScript, script, StandardCharsets.UTF_8);

            List<String> command = new ArrayList<>();
            command.add("groovy");
            command.add(tempScript.toString());
            command.addAll(args);

            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();

            boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                String partialOut = readStream(process.getInputStream());
                String partialErr = readStream(process.getErrorStream());
                return new ExecuteResponse(-1, partialOut, partialErr + "\n执行超时（>" + timeoutSeconds + "s）", true);
            }

            String stdout = readStream(process.getInputStream());
            String stderr = readStream(process.getErrorStream());
            int exitCode = process.exitValue();
            return new ExecuteResponse(exitCode, stdout, stderr, false);
        } catch (IOException e) {
            throw new GroovyExecutionException("执行 Groovy 脚本失败。", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new GroovyExecutionException("执行被中断。", e);
        } finally {
            if (tempScript != null) {
                try {
                    Files.deleteIfExists(tempScript);
                } catch (IOException ignored) {
                }
            }
        }
    }

    private void ensureGroovyAvailable() {
        try {
            Process process = new ProcessBuilder("groovy", "--version").start();
            if (!process.waitFor(3, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                throw new GroovyExecutionException("未检测到可用的 groovy 命令，请安装 Groovy 并配置 PATH。");
            }
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new GroovyExecutionException("未检测到可用的 groovy 命令，请安装 Groovy 并配置 PATH。", e);
        }
    }

    private String readStream(java.io.InputStream inputStream) throws IOException {
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }
}
