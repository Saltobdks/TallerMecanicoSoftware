package mx.taller.customer;

import java.net.URI;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

/** Centraliza reglas de formato y consistencia para que la vista no sea la fuente de seguridad. */
@Service
public class ValidationService {
  private static final Pattern PHONE = Pattern.compile("^\\d{10}$");
  private static final Pattern ZIP = Pattern.compile("^\\d{5}$");
  private static final Pattern BASE64_IMAGE = Pattern.compile("^data:image/(png|jpe?g|webp);base64,[A-Za-z0-9+/=\\s]+$");

  /** Valida campos personales, contacto y que edad coincida con fecha de nacimiento. */
  public List<String> validateCustomerFormat(CustomerDtos.Register data) {
    List<String> errors = new ArrayList<>();
    if (!PHONE.matcher(data.telefonoPersonal()).matches()) errors.add("El teléfono personal debe tener 10 dígitos.");
    if (hasText(data.telefonoTrabajo()) && !PHONE.matcher(data.telefonoTrabajo()).matches()) errors.add("El teléfono de trabajo debe tener 10 dígitos.");
    if (data.fechaNacimiento().isAfter(LocalDate.now())) errors.add("La fecha de nacimiento no puede ser futura.");
    else if (Period.between(data.fechaNacimiento(), LocalDate.now()).getYears() != data.edad()) errors.add("La edad no coincide con la fecha de nacimiento.");
    if (hasText(data.fotografia()) && !isImageSource(data.fotografia())) errors.add("La fotografía debe ser una URL http(s) o una imagen Base64 válida.");
    return errors;
  }

  /** Comprueba que la dirección sea atómica y que el código postal mexicano tenga cinco dígitos. */
  public List<String> validateStructuredAddress(CustomerDtos.Address address) {
    List<String> errors = new ArrayList<>();
    if (!ZIP.matcher(address.codigoPostal()).matches()) errors.add("El código postal debe tener 5 dígitos.");
    return errors;
  }

  private boolean hasText(String value) { return value != null && !value.isBlank(); }
  private boolean isImageSource(String source) {
    if (BASE64_IMAGE.matcher(source).matches()) return true;
    try { URI uri = URI.create(source); return "http".equals(uri.getScheme()) || "https".equals(uri.getScheme()); }
    catch (IllegalArgumentException ignored) { return false; }
  }
}
