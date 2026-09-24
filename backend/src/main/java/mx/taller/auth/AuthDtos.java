package mx.taller.auth;
import jakarta.validation.constraints.*; import mx.taller.user.Role;
public final class AuthDtos {
 public record Register(@NotBlank @Size(max=100) String nombre,@Email @NotBlank String email,@NotBlank @Size(min=10,max=72) String password){}
 public record Login(@Email @NotBlank String email,@NotBlank String password){}
 public record ResetRequest(@Email @NotBlank String email){}
 public record ResetConfirm(@NotBlank String token,@NotBlank @Size(min=10,max=72) String password){}
 public record AuthResponse(String token,String nombre,String email,Role role){}
 private AuthDtos(){}
}
