package mx.taller.address;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {
  private final AddressService service;
  public AddressController(AddressService service){ this.service=service; }
  @GetMapping("/postal-codes/{postalCode}")
  public AddressDtos.PostalCode byPostalCode(@PathVariable String postalCode){ return service.byPostalCode(postalCode); }
}
