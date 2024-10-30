package recipex.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication(scanBasePackages = "recipex")
@EnableReactiveMongoRepositories(basePackages = "recipex.mongo")
public class RecipexApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecipexApplication.class, args);
	}

}
