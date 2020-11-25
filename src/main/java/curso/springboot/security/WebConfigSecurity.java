package curso.springboot.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter {
	
	@Override //configura as solicitações de acesso por http
	protected void configure(HttpSecurity http) throws Exception {
		
		http.csrf()
		.disable() //desativas as configurações padrões de memória
		.authorizeRequests() // permite a restrição de acessos
		.antMatchers(HttpMethod.GET, "/").permitAll() //qualquer usuário pode acessar a página inicial
		.anyRequest().authenticated()
		.and().formLogin().permitAll() //permite qualquer usuário
		.and().logout() // mapeia URL de Logout e invalida usuário autenticado
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
		
		
	}
	
	@Override //cria autenticação do usuário com banco de dados ou em memória
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		auth.inMemoryAuthentication().passwordEncoder(NoOpPasswordEncoder.getInstance())
		.withUser("pedro")
		.password("123")
		.roles("ADMIN");
	
	}
	
	@Override //ignora URL especificas
	public void configure(WebSecurity web) throws Exception {
		
		web.ignoring().antMatchers("/materialize/**");
	
	}
}
