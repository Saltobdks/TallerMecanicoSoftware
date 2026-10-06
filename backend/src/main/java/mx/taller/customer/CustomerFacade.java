package mx.taller.customer;

import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.*;
import mx.taller.workshop.WorkshopRepository;

/** Fachada de caso de uso: coordina autorización, validación, duplicados y persistencia del cliente. */
@Service
public class CustomerFacade {
  private final AuthGuardService guard; private final ValidationService validation;
  private final CustomerRepository customers; private final CustomerWorkshopRepository workshops;
  private final WorkshopRepository workshopCatalog;
  public CustomerFacade(AuthGuardService g, ValidationService v, CustomerRepository c, CustomerWorkshopRepository w, WorkshopRepository catalog){ guard=g; validation=v; customers=c; workshops=w; workshopCatalog=catalog; }

  /** Registra un cliente y lo asocia a la sucursal indicada dentro de una sola transacción. */
  @Transactional public CustomerDtos.Result registerCustomer(CustomerDtos.Register input, String authorization) {
    guard.authorizeBearer(authorization);
    List<String> errors = new ArrayList<>(validation.validateCustomerFormat(input));
    errors.addAll(validation.validateStructuredAddress(input.address()));
    if (!errors.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.join(" ", errors));
    if (!workshopCatalog.existsById(input.workshopId())) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El taller seleccionado no existe.");
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
  @Transactional public CustomerDtos.Result updateCustomer(Long id, CustomerDtos.Update input, String authorization) {
    guard.authorizeAdministrator(authorization); validate(input);
    if (!workshopCatalog.existsById(input.workshopId())) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"El taller seleccionado no existe.");
    Customer customer=customers.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Cliente no encontrado."));
    if (customers.existsByEmailAndIdNot(input.email().trim().toLowerCase(),id) || customers.existsByTelefonoPersonalAndIdNot(input.telefonoPersonal(),id)) throw new ResponseStatusException(HttpStatus.CONFLICT,"Ya existe un cliente con ese correo o teléfono.");
    apply(customer,input); workshops.deleteByCustomerId(id); associateToWorkshop(id,input.workshopId());
    return new CustomerDtos.Result(id,"Cliente actualizado correctamente.");
  }
  @Transactional public CustomerDtos.Result suspend(Long id, String authorization) {
    guard.authorizeAdministrator(authorization); Customer customer=customers.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Cliente no encontrado.")); customer.suspender();
    return new CustomerDtos.Result(id,"Cliente suspendido correctamente.");
  }
  @Transactional(readOnly=true) public CustomerDtos.PageResult list(Long workshopId,int page,int size,String direction,String authorization) {
    guard.authorizeAdministrator(authorization);
    if (!workshopCatalog.existsById(workshopId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"El taller seleccionado no existe.");
    if(page<0 || size<1 || size>10) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"La página debe ser válida y el máximo es 10 registros.");
    Sort.Direction sort="DESC".equalsIgnoreCase(direction)?Sort.Direction.DESC:Sort.Direction.ASC;
    Page<Customer> result=customers.findPageByWorkshopId(workshopId,PageRequest.of(page,size,Sort.by(sort,"nombreCompleto")));
    return new CustomerDtos.PageResult(result.getContent().stream().map(c->item(c,workshopId)).toList(),result.getTotalElements(),result.getTotalPages(),result.getNumber(),result.getSize());
  }
  private void validate(CustomerDtos.Update i) {
    CustomerDtos.Register asRegister=new CustomerDtos.Register(i.nombreCompleto(),i.contactoAlternativo(),i.edad(),i.fechaNacimiento(),i.telefonoPersonal(),i.telefonoTrabajo(),i.email(),i.emailTrabajo(),i.fotografia(),i.address(),i.workshopId());
    List<String> errors=new ArrayList<>(validation.validateCustomerFormat(asRegister)); errors.addAll(validation.validateStructuredAddress(i.address())); if(!errors.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,String.join(" ",errors));
  }
  private CustomerDtos.Item item(Customer c,Long workshopId){return new CustomerDtos.Item(c.getId(),c.getNombreCompleto(),c.getEmail(),c.getTelefonoPersonal(),c.getEstatus(),workshopId,new CustomerDtos.Address(c.getCalle(),c.getNumero(),c.getColonia(),c.getMunicipio(),c.getEstado(),c.getCodigoPostal()));}
  private Customer map(CustomerDtos.Register i) { Customer c=new Customer(); apply(c,new CustomerDtos.Update(i.nombreCompleto(),i.contactoAlternativo(),i.edad(),i.fechaNacimiento(),i.telefonoPersonal(),i.telefonoTrabajo(),i.email(),i.emailTrabajo(),i.fotografia(),i.address(),i.workshopId())); return c; }
  private void apply(Customer c,CustomerDtos.Update i) { c.setNombreCompleto(i.nombreCompleto()); c.setContactoAlternativo(i.contactoAlternativo()); c.setEdad(i.edad()); c.setFechaNacimiento(i.fechaNacimiento()); c.setTelefonoPersonal(i.telefonoPersonal()); c.setTelefonoTrabajo(i.telefonoTrabajo()); c.setEmail(i.email()); c.setEmailTrabajo(i.emailTrabajo()); c.setFotografia(i.fotografia()); c.setPais("México"); c.setCalle(i.address().calle()); c.setNumero(i.address().numero()); c.setColonia(i.address().colonia()); c.setMunicipio(i.address().municipio()); c.setEstado(i.address().estado()); c.setCodigoPostal(i.address().codigoPostal()); }
}
