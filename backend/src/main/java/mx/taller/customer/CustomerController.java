package mx.taller.customer;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/** Expone el alta de clientes. La autorización real se verifica nuevamente en CustomerFacade. */
@RestController
@RequestMapping("/api/auth/customers")
public class CustomerController {
  private final CustomerFacade facade;
  public CustomerController(CustomerFacade facade){ this.facade=facade; }
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CustomerDtos.Result register(@Valid @RequestBody CustomerDtos.Register request, @RequestHeader(value = "Authorization", required = false) String authorization) {
    return facade.registerCustomer(request, authorization);
  }
}
