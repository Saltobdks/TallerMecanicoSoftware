package mx.taller.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Acceso a datos de clientes; las restricciones únicas complementan la validación de duplicados. */
public interface CustomerRepository extends JpaRepository<Customer, Long> {
  boolean existsByEmailOrTelefonoPersonal(String email, String telefonoPersonal);
  boolean existsByEmailAndIdNot(String email, Long id);
  boolean existsByTelefonoPersonalAndIdNot(String telefonoPersonal, Long id);
  @Query("select c from Customer c join CustomerWorkshop link on link.customer=c where link.workshopId=:workshopId")
  Page<Customer> findPageByWorkshopId(@Param("workshopId") Long workshopId, Pageable pageable);
}
