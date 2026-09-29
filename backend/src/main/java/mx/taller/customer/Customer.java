package mx.taller.customer;

import jakarta.persistence.*;
import java.time.LocalDate;

/** Cliente del taller. La dirección se conserva en campos atómicos para búsquedas y facturación. */
@Entity
@Table(name = "clientes", indexes = {
  @Index(name = "idx_cliente_email", columnList = "email", unique = true),
  @Index(name = "idx_cliente_telefono", columnList = "telefonoPersonal", unique = true)
})
public class Customer {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @Column(nullable = false, length = 120) private String nombreCompleto;
  @Column(nullable = false, length = 120) private String contactoAlternativo;
  @Column(nullable = false) private Integer edad;
  @Column(nullable = false) private LocalDate fechaNacimiento;
  @Column(nullable = false, length = 10) private String telefonoPersonal;
  @Column(length = 10) private String telefonoTrabajo;
  @Column(nullable = false, length = 180) private String email;
  @Column(length = 180) private String emailTrabajo;
  @Column(length = 4000) private String fotografia;
  @Column(nullable = false, length = 120) private String calle;
  @Column(nullable = false, length = 20) private String numero;
  @Column(nullable = false, length = 120) private String colonia;
  @Column(nullable = false, length = 120) private String municipio;
  @Column(nullable = false, length = 120) private String estado;
  @Column(nullable = false, length = 5) private String codigoPostal;
  public Long getId(){ return id; }
  public String getEmail(){ return email; } public void setEmail(String v){ email=v.trim().toLowerCase(); }
  public String getTelefonoPersonal(){ return telefonoPersonal; } public void setTelefonoPersonal(String v){ telefonoPersonal=v; }
  public void setNombreCompleto(String v){ nombreCompleto=v.trim(); } public void setContactoAlternativo(String v){ contactoAlternativo=v.trim(); }
  public void setEdad(Integer v){ edad=v; } public void setFechaNacimiento(LocalDate v){ fechaNacimiento=v; }
  public void setTelefonoTrabajo(String v){ telefonoTrabajo=blankToNull(v); } public void setEmailTrabajo(String v){ emailTrabajo=blankToNull(v); }
  public void setFotografia(String v){ fotografia=blankToNull(v); } public void setCalle(String v){ calle=v.trim(); } public void setNumero(String v){ numero=v.trim(); }
  public void setColonia(String v){ colonia=v.trim(); } public void setMunicipio(String v){ municipio=v.trim(); } public void setEstado(String v){ estado=v.trim(); } public void setCodigoPostal(String v){ codigoPostal=v; }
  private String blankToNull(String v){ return v == null || v.isBlank() ? null : v.trim(); }
}
