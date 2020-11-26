package curso.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
//configura o pacote o qual irá ser escaneado para gerar as tabelas no bd
@EntityScan(basePackages = "curso.springboot.model")
@ComponentScan(basePackages = { "curso.*" })
@EnableJpaRepositories(basePackages = "curso.springboot.repository")
@EnableTransactionManagement
@EnableWebMvc
public class SpringbootApplication implements WebMvcConfigurer{

	public static void main(String[] args) {
		SpringApplication.run(SpringbootApplication.class, args);
	}
	
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		//reconfigura a página padrão do spring de login, para a nossa propria página de login
		registry.addViewController("/login").setViewName("/login");
		registry.setOrder(Ordered.LOWEST_PRECEDENCE);
		
	}

}
