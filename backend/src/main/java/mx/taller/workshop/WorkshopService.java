package mx.taller.workshop;

import java.util.List;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import mx.taller.customer.AuthGuardService;
import mx.taller.customer.ValidationService;

@Service
public class WorkshopService {
  private static final Pattern RFC = Pattern.compile("^(?:[A-ZÑ&]{3}|[A-ZÑ&]{4})\\d{6}[A-Z0-9]{3}$");
  private static final Pattern PHONE = Pattern.compile("^\\d{10}$");
  private final WorkshopRepository repository; private final AuthGuardService guard; private final ValidationService validation;
  public WorkshopService(WorkshopRepository r, AuthGuardService g, ValidationService v){ repository=r; guard=g; validation=v; }
  @Transactional public WorkshopDtos.Result create(WorkshopDtos.Upsert input, String authorization) {
    guard.authorizeAdministrator(authorization); validate(input);
    if (repository.existsByRfcOrEmailOrTelefono(normalizeRfc(input.rfc()), input.email().trim().toLowerCase(), input.telefono().trim())) conflict();
    Workshop workshop=new Workshop(); workshop.apply(input); repository.save(workshop);
    return new WorkshopDtos.Result(workshop.getId(), "Taller registrado correctamente.");
  }
  @Transactional public WorkshopDtos.Result update(Long id, WorkshopDtos.Upsert input, String authorization) {
    guard.authorizeAdministrator(authorization); validate(input);
    if (repository.existsByRfcAndIdNot(normalizeRfc(input.rfc()), id)
        || repository.existsByEmailAndIdNot(input.email().trim().toLowerCase(), id)
        || repository.existsByTelefonoAndIdNot(input.telefono().trim(), id)) conflict();
    Workshop workshop=repository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Taller no encontrado.")); workshop.apply(input);
    return new WorkshopDtos.Result(id, "Taller actualizado correctamente.");
  }
  @Transactional(readOnly=true) public List<WorkshopDtos.Summary> list(String authorization) {
    guard.authorizeCustomerManager(authorization);
    return repository.findAllByOrderByNombreAsc().stream().map(w -> new WorkshopDtos.Summary(w.getId(), w.getNombre())).toList();
  }
  private void validate(WorkshopDtos.Upsert input) {
    if (!RFC.matcher(normalizeRfc(input.rfc())).matches()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"El RFC debe tener el formato oficial mexicano, con homoclave.");
    if (!PHONE.matcher(input.telefono().trim()).matches()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"El teléfono debe tener 10 dígitos.");
    List<String> errors=validation.validateStructuredAddress(input.address()); if (!errors.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,String.join(" ",errors));
  }
  private String normalizeRfc(String rfc){ return rfc == null ? "" : rfc.trim().toUpperCase(); }
  private void conflict(){ throw new ResponseStatusException(HttpStatus.CONFLICT,"Ya existe un taller con ese RFC, correo o teléfono."); }
}
