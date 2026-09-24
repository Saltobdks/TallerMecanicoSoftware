package mx.taller.auth;

import jakarta.validation.Valid;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import mx.taller.security.JwtService;
import mx.taller.user.User;
import mx.taller.user.UserRepository;

@RestController @RequestMapping("/api/auth")
public class AuthController {
  private final UserRepository users; private final PasswordEncoder encoder; private final JwtService jwt;
  public AuthController(UserRepository u, PasswordEncoder e, JwtService j) { users=u; encoder=e; jwt=j; }
  @PostMapping("/register") public ResponseEntity<?> register(@Valid @RequestBody AuthDtos.Register r) {
    if(users.findByEmail(r.email().toLowerCase()).isPresent()) return ResponseEntity.status(409).body(Map.of("message","El correo ya está registrado."));
    User u=new User(); u.setNombre(r.nombre()); u.setEmail(r.email()); u.setPassword(encoder.encode(r.password())); users.save(u); return ResponseEntity.status(201).body(response(u));
  }
  @PostMapping("/login") public ResponseEntity<?> login(@Valid @RequestBody AuthDtos.Login r) {
    User u=users.findByEmail(r.email().toLowerCase()).orElse(null);
    if(u==null || !u.isActivo() || !encoder.matches(r.password(),u.getPassword())) return ResponseEntity.status(401).body(Map.of("message","Correo o contraseña incorrectos."));
    return ResponseEntity.ok(response(u));
  }
  @PostMapping("/password/forgot") public Map<String,String> forgot(@Valid @RequestBody AuthDtos.ResetRequest r) {
    users.findByEmail(r.email().toLowerCase()).ifPresent(u->{ u.setResetToken(UUID.randomUUID().toString()); u.setResetExpiresAt(Instant.now().plusSeconds(900)); users.save(u); });
    return Map.of("message","Si existe una cuenta, recibirá instrucciones para restablecer la contraseña.");
  }
  private AuthDtos.AuthResponse response(User u) { return new AuthDtos.AuthResponse(jwt.create(u),u.getNombre(),u.getEmail(),u.getRole()); }
}
