package mx.taller.workshop;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkshopRepository extends JpaRepository<Workshop, Long> {
  boolean existsByRfcOrEmailOrTelefono(String rfc, String email, String telefono);
  boolean existsByRfcAndIdNot(String rfc, Long id);
  boolean existsByEmailAndIdNot(String email, Long id);
  boolean existsByTelefonoAndIdNot(String telefono, Long id);
  List<Workshop> findAllByOrderByNombreAsc();
}
