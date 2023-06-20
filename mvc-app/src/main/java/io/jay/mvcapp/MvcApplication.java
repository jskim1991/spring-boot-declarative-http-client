package io.jay.mvcapp;

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

import java.util.List;

interface ProductClient {

    @GetExchange("/products")
    ProductClientResponse fetchAll();
}

@SpringBootApplication
public class MvcApplication {

    public static void main(String[] args) {
        SpringApplication.run(MvcApplication.class, args);
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
    public List<Product> getAll() {
        return productClient.fetchAll().products();
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