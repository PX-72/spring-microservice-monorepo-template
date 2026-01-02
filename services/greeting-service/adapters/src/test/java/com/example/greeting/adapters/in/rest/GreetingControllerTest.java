package com.example.greeting.adapters.in.rest;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.greeting.domain.Greeting;
import com.example.greeting.domain.services.GreetingService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = GreetingController.class)
class GreetingControllerTest {
  private final MockMvc mockMvc;

  @MockitoBean private GreetingService greetingService;

  @Autowired
  GreetingControllerTest(MockMvc mockMvc) {
    this.mockMvc = mockMvc;
  }

  @Test
  void CreateReturnsCreated() throws Exception {
    var id = UUID.fromString("00000000-0000-0000-0000-000000000001");
    when(this.greetingService.createGreeting(anyString()))
        .thenReturn(new Greeting(id, "Hello, User!"));

    this.mockMvc
        .perform(
            post("/api/v1/greetings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"User\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.message").value("Hello, User!"));
  }
}
