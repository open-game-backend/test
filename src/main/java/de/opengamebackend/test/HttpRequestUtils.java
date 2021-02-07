package de.opengamebackend.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HttpRequestUtils {
    public <T> T assertGetOk(MockMvc mvc, String url, Class<T> responseClass) throws Exception {
       return assertGetOk(mvc, url, responseClass, null);
    }

    public <T> T assertGetOk(MockMvc mvc, String url, Class<T> responseClass, String playerId) throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();

        if (playerId != null) {
            httpHeaders.put("Player-Id", Collections.singletonList(playerId));
        }

        String responseJson = mvc.perform(get(url)
                .headers(httpHeaders)
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

    public <T> T assertPostOk(MockMvc mvc, String url, Object request) throws Exception {
        return assertPostOk(mvc, url, request, null, null);
    }

    public <T> T assertPostOk(MockMvc mvc, String url, Object request, Class<T> responseClass, String playerId) throws Exception {
        ObjectMapper objectMapper = createObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        HttpHeaders httpHeaders = new HttpHeaders();

        if (playerId != null) {
            httpHeaders.put("Player-Id", Collections.singletonList(playerId));
        }

        ResultActions resultActions = mvc.perform(post(url)
                .content(requestJson)
                .headers(httpHeaders)
                .contentType(MediaType.APPLICATION_JSON));

        if (responseClass == null) {
            resultActions.andExpect(status().isNoContent()).andReturn();
            return null;
        }

        MvcResult result = resultActions.andExpect(status().isOk()).andReturn();
        String responseJson = result.getResponse().getContentAsString();
        T response = objectMapper.readValue(responseJson, responseClass);
        assertThat(response).isNotNull();

        return response;
    }

    public void assertPutOk(MockMvc mvc, String url, Object request) throws Exception {
        ObjectMapper objectMapper = createObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        HttpHeaders httpHeaders = new HttpHeaders();

        mvc.perform(put(url)
                .content(requestJson)
                .headers(httpHeaders)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    public void assertDeleteOk(MockMvc mvc, String url) throws Exception {
        mvc.perform(delete(url)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // Required for proper JSON (de-)serialization of LocalDateTime.
        objectMapper.registerModule(new JavaTimeModule());

        return objectMapper;
    }
}
