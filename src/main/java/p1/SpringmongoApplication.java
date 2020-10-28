package p1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.oas.annotations.EnableOpenApi;

@EnableOpenApi
@SpringBootApplication
public class SpringmongoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringmongoApplication.class, args);
    }

}
