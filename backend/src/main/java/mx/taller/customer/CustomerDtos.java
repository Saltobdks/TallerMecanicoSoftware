package mx.taller.customer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

/** Contratos HTTP del alta de clientes. La validación de reglas cruzadas está en ValidationService. */
public final class CustomerDtos {
  public record Address(@NotBlank @Size(max=120) String calle, @NotBlank @Size(max=20) String numero,
                        @NotBlank @Size(max=120) String colonia, @NotBlank @Size(max=120) String municipio,
                        @NotBlank @Size(max=120) String estado, @NotBlank String codigoPostal) {}
  public record Register(@NotBlank @Size(max=120) String nombreCompleto, @NotBlank @Size(max=120) String contactoAlternativo,
                         @NotNull @Min(0) @Max(130) Integer edad, @NotNull LocalDate fechaNacimiento,
                         @NotBlank String telefonoPersonal, String telefonoTrabajo, @NotBlank @Email String email,
                         @Email String emailTrabajo, String fotografia, @NotNull @Valid Address address, @NotNull @Positive Long workshopId) {}
  public record Result(Long customerId, String message) {}
  private CustomerDtos() {}
}
