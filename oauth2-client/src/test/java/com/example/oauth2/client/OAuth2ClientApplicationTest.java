package com.example.oauth2.client;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OAuth2ClientApplicationTest {
    private static final Logger logger = LoggerFactory.getLogger(OAuth2ClientApplicationTest.class);

    @LocalServerPort
    private int port;

    private static MockWebServer mockOAuth2ResourceServer;

    private static MockWebServer mockOAuth2AuthorizationServer;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() throws Exception {
        // Mock Resource Server
        mockOAuth2ResourceServer = new MockWebServer();
        Dispatcher dispatcherOfResourceServer = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                if (request.getPath().equals("/")){
                    return new MockResponse().setBody("this is a message from the MOCKED resource server.").setResponseCode(200);
                } else {
                    return new MockResponse().setBody("not found!").setResponseCode(404);
                }
            }
        };
        mockOAuth2ResourceServer.setDispatcher(dispatcherOfResourceServer);
        mockOAuth2ResourceServer.start(8090);


        // Mock Authorization Server
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("access_token", JWKSUtil.getSignedJWT());
        jsonObject.put("scope", "openid read");
        jsonObject.put("token_type", "Bearer");
        jsonObject.put("expires_in", 3599);

        mockOAuth2AuthorizationServer = new MockWebServer();
        Dispatcher dispatcherOfAuthorizationServer = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                logger.info("Mock Auth Server: Request URL = {}", request.getRequestUrl());

                if (request.getPath().equals("/oauth2/token")) {
                    return new MockResponse().setBody(jsonObject.toString())
                            .setHeader("Content-Type", "application/json")
                            .setResponseCode(200);

                } else if (request.getPath().contains("/oauth2/jwks")) {
                    return new MockResponse().setBody(JWKSUtil.getJWKSet())
                            .setHeader("Content-Type", "application/json")
                            .setResponseCode(200);

                } else {
                    return new MockResponse().setBody("not found!").setResponseCode(404);
                }
            }
        };
        mockOAuth2AuthorizationServer.setDispatcher(dispatcherOfAuthorizationServer);
        mockOAuth2AuthorizationServer.start(8080);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "test", authorities = "SCOPE_test")
    public void test_oauth2Client() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("this is a message from the MOCKED resource server."));
    }





}
