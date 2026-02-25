package com.example.groovyexecuter.script;

import com.example.groovyexecuter.ExecuteResponse;
import com.example.groovyexecuter.GroovyExecutionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GroovyScriptController.class)
class GroovyScriptControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GroovyScriptService groovyScriptService;

    @MockBean
    private GroovyExecutionService groovyExecutionService;

    @Test
    void shouldListScripts() throws Exception {
        GroovyScript script = new GroovyScript();
        script.setId(1L);
        script.setName("demo");
        script.setContent("println 1");
        when(groovyScriptService.list()).thenReturn(List.of(script));

        mockMvc.perform(get("/api/scripts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("demo"));
    }

    @Test
    void shouldCreateScript() throws Exception {
        GroovyScript script = new GroovyScript();
        script.setId(2L);
        script.setName("n1");
        script.setContent("println 1");
        when(groovyScriptService.create(org.mockito.ArgumentMatchers.any())).thenReturn(script);

        mockMvc.perform(post("/api/scripts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"n1","description":"d","content":"println 1"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    void shouldExecuteScriptById() throws Exception {
        GroovyScript script = new GroovyScript();
        script.setId(1L);
        script.setContent("println 'ok'");
        when(groovyScriptService.get(1L)).thenReturn(script);
        when(groovyExecutionService.execute(eq("println 'ok'"), eq(List.of("A")), eq(8)))
                .thenReturn(new ExecuteResponse(0, "ok\n", "", false));

        mockMvc.perform(post("/api/scripts/1/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"args":["A"],"timeoutSeconds":8}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exitCode").value(0))
                .andExpect(jsonPath("$.stdout").value("ok\n"));
    }
}
