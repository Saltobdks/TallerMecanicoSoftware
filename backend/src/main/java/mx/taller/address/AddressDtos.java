package mx.taller.address;

import java.util.List;

public final class AddressDtos {
  public record PostalCode(String codigoPostal, String estado, String municipio, List<String> colonias) {}
  public record State(String nombre, List<String> municipios) {}
  private AddressDtos() {}
}
