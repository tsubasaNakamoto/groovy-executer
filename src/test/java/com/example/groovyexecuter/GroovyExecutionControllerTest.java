package com.example.groovyexecuter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GroovyExecutionController.class)
class GroovyExecutionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GroovyExecutionService groovyExecutionService;

    @Test
    void executeShouldReturnResult() throws Exception {
        when(groovyExecutionService.execute(eq("println 1"), eq(List.of("A")), eq(10)))
                .thenReturn(new ExecuteResponse(0, "1\n", "", false));

        mockMvc.perform(post("/api/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"script":"println 1","args":["A"],"timeoutSeconds":10}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exitCode").value(0))
                .andExpect(jsonPath("$.stdout").value("1\n"))
                .andExpect(jsonPath("$.timedOut").value(false));
    }

    @Test
    void executeShouldValidateInput() throws Exception {
        mockMvc.perform(post("/api/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"script":"","args":[],"timeoutSeconds":0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").exists());
    }
}
