package mx.taller.customer;

import org.springframework.security.access.AccessDeniedException;
import mx.taller.security.JwtService;
import mx.taller.user.User;
import mx.taller.user.UserRepository;
import org.springframework.stereotype.Service;

/** Aplica en el dominio la precondición de que sólo Administrador o Secretaria den de alta clientes. */
@Service
public class AuthGuardService {
  private final JwtService jwt; private final UserRepository users;
  public AuthGuardService(JwtService jwt, UserRepository users){ this.jwt=jwt; this.users=users; }
  public void authorizeBearer(String authorization) {
    if (authorization == null || !authorization.startsWith("Bearer ")) throw new AccessDeniedException("Debes iniciar sesión.");
    try {
      User user = users.findByEmail(jwt.subject(authorization.substring(7))).orElseThrow();
      boolean authorized = user.isActivo() && (user.getRole().name().equals("SUPERADMIN") || user.getRole().name().equals("ADMINISTRADOR") || user.getRole().name().equals("SECRETARIA"));
      if (!authorized) throw new AccessDeniedException("No tienes permiso para registrar clientes.");
    } catch (AccessDeniedException error) { throw error; } catch (Exception error) { throw new AccessDeniedException("Token inválido."); }
  }
}
