package io.jay.reactiveapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

interface ProductClient {

    @GetExchange("/products")
    Mono<ProductClientResponse> fetchAll();
}

@SpringBootApplication
public class ReactiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactiveApplication.class, args);
    }
}

@Controller
@ResponseBody
class ProductController {

    private final ProductClient productClient;

    public ProductController(ProductClient productClient) {
        this.productClient = productClient;
    }

    @GetMapping("/v1/products")
    public Flux<Product> getAll() {
        return productClient.fetchAll()
                .flatMapMany(response -> Flux.fromIterable(response.products()));
    }
}

record Product(int id, String title, String description, int price, double discountPercentage, double rating, int stock,
               String brand, String category) {
}

record ProductClientResponse(List<Product> products) {
}

@Configuration
class HttpClientConfiguration {
    @Bean
    ProductClient productClient(WebClient.Builder builder) {
        var wca = WebClientAdapter.forClient(builder.baseUrl("https://dummyjson.com").build());
        return HttpServiceProxyFactory.builder()
                .clientAdapter(wca)
                .build()
                .createClient(ProductClient.class);
    }
}