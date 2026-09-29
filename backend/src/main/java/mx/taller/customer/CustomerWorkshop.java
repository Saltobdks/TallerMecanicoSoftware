package mx.taller.customer;

import jakarta.persistence.*;

/** Relación extensible que permite asociar el mismo cliente a varias sucursales. */
@Entity
@Table(name = "cliente_taller", uniqueConstraints = @UniqueConstraint(columnNames = {"customer_id", "workshopId"}))
public class CustomerWorkshop {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "customer_id") private Customer customer;
  @Column(nullable = false) private Long workshopId;
  protected CustomerWorkshop() {}
  public CustomerWorkshop(Customer customer, Long workshopId){ this.customer=customer; this.workshopId=workshopId; }
}
