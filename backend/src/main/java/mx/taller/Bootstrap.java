package mx.taller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import mx.taller.user.*;
@org.springframework.context.annotation.Configuration public class Bootstrap {
 @Bean CommandLineRunner seed(UserRepository users, PasswordEncoder encoder, @Value("${app.superadmin-email}") String email, @Value("${app.superadmin-password}") String password){ return args->{ if(users.findByEmail(email.toLowerCase()).isEmpty()){ User u=new User();u.setNombre("Superadministrador");u.setEmail(email);u.setPassword(encoder.encode(password));u.setRole(Role.SUPERADMIN);users.save(u); } }; }
}
