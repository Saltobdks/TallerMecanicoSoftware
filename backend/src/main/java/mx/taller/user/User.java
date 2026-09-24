package mx.taller.user;
import jakarta.persistence.*; import java.time.Instant;
@Entity @Table(name="usuarios") public class User {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true) private String email;
 @Column(nullable=false) private String password;
 @Column(nullable=false) private String nombre;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private Role role=Role.CLIENTE;
 private boolean activo=true; private String resetToken; private Instant resetExpiresAt;
 public Long getId(){return id;} public String getEmail(){return email;} public void setEmail(String v){email=v.toLowerCase().trim();} public String getPassword(){return password;} public void setPassword(String v){password=v;} public String getNombre(){return nombre;} public void setNombre(String v){nombre=v.trim();} public Role getRole(){return role;} public void setRole(Role v){role=v;} public boolean isActivo(){return activo;} public void setActivo(boolean v){activo=v;} public String getResetToken(){return resetToken;} public void setResetToken(String v){resetToken=v;} public Instant getResetExpiresAt(){return resetExpiresAt;} public void setResetExpiresAt(Instant v){resetExpiresAt=v;}
}
