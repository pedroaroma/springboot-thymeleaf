package curso.springboot;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CryptoPassword {

	public static void main(String[] args) {
		
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		
		String reString = encoder.encode("123");
		
		System.out.println(reString);

	}

}
