package mx.taller.customer;
import org.springframework.data.jpa.repository.JpaRepository;
/** Persiste asociaciones cliente-sucursal sin duplicarlas. */
public interface CustomerWorkshopRepository extends JpaRepository<CustomerWorkshop, Long> {
  boolean existsByCustomerIdAndWorkshopId(Long customerId, Long workshopId);
}
