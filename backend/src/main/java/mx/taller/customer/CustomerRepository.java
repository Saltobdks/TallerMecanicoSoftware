package mx.taller.customer;

import org.springframework.data.jpa.repository.JpaRepository;

/** Acceso a datos de clientes; las restricciones únicas complementan la validación de duplicados. */
public interface CustomerRepository extends JpaRepository<Customer, Long> {
  boolean existsByEmailOrTelefonoPersonal(String email, String telefonoPersonal);
}
