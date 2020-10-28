package p1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class Swagger3Config {

    @Bean
    public Docket createRestApi(Environment environment) {

        Profiles profiles = Profiles.of("dev");
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .enable(environment.acceptsProfiles(profiles))
                .select()
                .apis(RequestHandlerSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Swagger3API Document")
                .description("Api Documentation")
                .contact(new Contact("Caleb","https://github.com/shangguanxiaomei/shangguanxiaomei",""))
                .version("1.0")
                .license("Apache 2.0")
                .build();
    }
}