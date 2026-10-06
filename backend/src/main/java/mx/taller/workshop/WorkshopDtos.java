package mx.taller.workshop;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import mx.taller.customer.CustomerDtos;

public final class WorkshopDtos {
  public record Upsert(@NotBlank @Size(max=120) String nombre, @NotBlank @Size(max=180) String razonSocial,
                       @NotBlank String rfc, @NotBlank String telefono, @NotBlank @Email String email,
                       String fotografia, @NotNull @Valid CustomerDtos.Address address) {}
  public record Summary(Long id, String nombre) {}
  public record Result(Long workshopId, String message) {}
  private WorkshopDtos() {}
}
