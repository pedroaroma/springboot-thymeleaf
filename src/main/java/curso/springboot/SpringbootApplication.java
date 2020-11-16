package curso.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
//configura o pacote o qual irá ser escaneado para gerar as tabelas no bd
@EntityScan(basePackages = "curso.springboot.model")
@ComponentScan(basePackages = { "curso.*" })
@EnableJpaRepositories(basePackages = "curso.springboot.repository")
@EnableTransactionManagement
public class SpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootApplication.class, args);
	}

}
