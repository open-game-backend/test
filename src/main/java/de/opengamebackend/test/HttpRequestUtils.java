package de.opengamebackend.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HttpRequestUtils {
    public <T> T assertGetOk(MockMvc mvc, String url, Class<T> responseClass) throws Exception {
        String responseJson = mvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        T response = createObjectMapper().readValue(responseJson, responseClass);
        assertThat(response).isNotNull();

        return response;
    }

    public <T> T assertPostOk(MockMvc mvc, String url, Object request, Class<T> responseClass) throws Exception {
        return assertPostOk(mvc, url, request, responseClass, null);
    }

    public <T> T assertPostOk(MockMvc mvc, String url, Object request, Class<T> responseClass, String playerId) throws Exception {
        ObjectMapper objectMapper = createObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        HttpHeaders httpHeaders = new HttpHeaders();

        if (playerId != null) {
            httpHeaders.put("Player-Id", Collections.singletonList(playerId));
        }

        String responseJson = mvc.perform(post(url)
                .content(requestJson)
                .headers(httpHeaders)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        T response = objectMapper.readValue(responseJson, responseClass);
        assertThat(response).isNotNull();

        return response;
    }

    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // Required for proper JSON (de-)serialization of LocalDateTime.
        objectMapper.registerModule(new JavaTimeModule());

        return objectMapper;
    }
}
