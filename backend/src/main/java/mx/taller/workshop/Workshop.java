package mx.taller.workshop;

import jakarta.persistence.*;

/** Sucursal/taller: sus datos y dirección no se comparten con otros talleres. */
@Entity
@Table(name = "talleres", indexes = {
  @Index(name = "uk_taller_rfc", columnList = "rfc", unique = true),
  @Index(name = "uk_taller_email", columnList = "email", unique = true),
  @Index(name = "uk_taller_telefono", columnList = "telefono", unique = true)
})
public class Workshop {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @Column(nullable=false, length=120) private String nombre;
  @Column(nullable=false, length=180) private String razonSocial;
  @Column(nullable=false, length=13) private String rfc;
  @Column(nullable=false, length=10) private String telefono;
  @Column(nullable=false, length=180) private String email;
  @Column(length=4000) private String fotografia;
  @Column(nullable=false, length=60) private String pais = "México";
  @Column(nullable=false, length=120) private String calle;
  @Column(nullable=false, length=20) private String numero;
  @Column(nullable=false, length=120) private String colonia;
  @Column(nullable=false, length=120) private String municipio;
  @Column(nullable=false, length=120) private String estado;
  @Column(nullable=false, length=5) private String codigoPostal;
  protected Workshop() {}
  public Long getId(){ return id; } public String getNombre(){ return nombre; }
  public void apply(WorkshopDtos.Upsert v) {
    nombre=v.nombre().trim(); razonSocial=v.razonSocial().trim(); rfc=v.rfc().trim().toUpperCase(); telefono=v.telefono().trim(); email=v.email().trim().toLowerCase(); fotografia=blank(v.fotografia());
    pais="México"; calle=v.address().calle().trim(); numero=v.address().numero().trim(); colonia=v.address().colonia().trim(); municipio=v.address().municipio().trim(); estado=v.address().estado().trim(); codigoPostal=v.address().codigoPostal().trim();
  }
  private String blank(String value){ return value == null || value.isBlank() ? null : value.trim(); }
}
