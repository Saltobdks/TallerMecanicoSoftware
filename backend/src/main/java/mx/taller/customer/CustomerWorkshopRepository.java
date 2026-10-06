package mx.taller.customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
/** Persiste asociaciones cliente-sucursal sin duplicarlas. */
public interface CustomerWorkshopRepository extends JpaRepository<CustomerWorkshop, Long> {
  boolean existsByCustomerIdAndWorkshopId(Long customerId, Long workshopId);
  @Modifying @Query("delete from CustomerWorkshop link where link.customer.id=:customerId")
  void deleteByCustomerId(@Param("customerId") Long customerId);
}
