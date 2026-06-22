package webstack;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("webstack.module.**.mapper")
public class WebstackServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebstackServerApplication.class, args);
    }
}
