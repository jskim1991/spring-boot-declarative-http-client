package io.jay.reactiveapp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReactiveApplicationTests {

    @Autowired
    WebTestClient webTestClient;

    @SpyBean
    ProductClient productClient;

    @Test
    void test_getAllProducts_invokesProductClient() {
        webTestClient.get()
                .uri("/v1/products")
                .exchange()
                .expectBodyList(Product.class)
                .returnResult();


        verify(productClient, times(1)).fetchAll();
    }

    @Test
    void test_getAllProducts_returnsProducts() {
        var response = webTestClient.get()
                .uri("/v1/products")
                .exchange()
                .expectBodyList(Product.class)
                .returnResult()
                .getResponseBody();


        assertEquals(30, response.size());
    }
}
