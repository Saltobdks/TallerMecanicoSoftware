package mx.taller.customer;

import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.Authentication;

/** Fachada de caso de uso: coordina autorización, validación, duplicados y persistencia del cliente. */
@Service
public class CustomerFacade {
  private final AuthGuardService guard; private final ValidationService validation;
  private final CustomerRepository customers; private final CustomerWorkshopRepository workshops;
  public CustomerFacade(AuthGuardService g, ValidationService v, CustomerRepository c, CustomerWorkshopRepository w){ guard=g; validation=v; customers=c; workshops=w; }

  /** Registra un cliente y lo asocia a la sucursal indicada dentro de una sola transacción. */
  @Transactional public CustomerDtos.Result registerCustomer(CustomerDtos.Register input, String authorization) {
    guard.authorizeBearer(authorization);
    List<String> errors = new ArrayList<>(validation.validateCustomerFormat(input));
    errors.addAll(validation.validateStructuredAddress(input.address()));
    if (!errors.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.join(" ", errors));
    if (customers.existsByEmailOrTelefonoPersonal(input.email().trim().toLowerCase(), input.telefonoPersonal()))
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un cliente con ese correo o teléfono.");
    Customer customer = map(input); customer = customers.save(customer); associateToWorkshop(customer.getId(), input.workshopId());
    return new CustomerDtos.Result(customer.getId(), "Cliente registrado correctamente.");
  }

  /** Asocia un cliente existente a una sucursal; es idempotente para evitar filas duplicadas. */
  @Transactional public void associateToWorkshop(Long customerId, Long workshopId) {
    if (!workshops.existsByCustomerIdAndWorkshopId(customerId, workshopId)) {
      Customer customer = customers.findById(customerId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado."));
      workshops.save(new CustomerWorkshop(customer, workshopId));
    }
  }
  private Customer map(CustomerDtos.Register i) { Customer c=new Customer(); c.setNombreCompleto(i.nombreCompleto()); c.setContactoAlternativo(i.contactoAlternativo()); c.setEdad(i.edad()); c.setFechaNacimiento(i.fechaNacimiento()); c.setTelefonoPersonal(i.telefonoPersonal()); c.setTelefonoTrabajo(i.telefonoTrabajo()); c.setEmail(i.email()); c.setEmailTrabajo(i.emailTrabajo()); c.setFotografia(i.fotografia()); c.setCalle(i.address().calle()); c.setNumero(i.address().numero()); c.setColonia(i.address().colonia()); c.setMunicipio(i.address().municipio()); c.setEstado(i.address().estado()); c.setCodigoPostal(i.address().codigoPostal()); return c; }
}
