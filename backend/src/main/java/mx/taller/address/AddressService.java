package mx.taller.address;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipInputStream;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/** Catálogo local SEPOMEX; no depende de un token, DNS ni servicio de terceros en tiempo de ejecución. */
@Service
public class AddressService {
  private static final Pattern ZIP=Pattern.compile("^\\d{5}$");
  private final Map<String, AddressDtos.PostalCode> catalog;

  public AddressService() { catalog=loadCatalog(); }

  public AddressDtos.PostalCode byPostalCode(String postalCode) {
    if (!ZIP.matcher(postalCode).matches()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"El código postal debe tener 5 dígitos.");
    AddressDtos.PostalCode result=catalog.get(postalCode);
    if (result==null) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No se encontró el código postal.");
    return result;
  }

  private Map<String, AddressDtos.PostalCode> loadCatalog() {
    Map<String, MutablePostalCode> rows=new HashMap<>();
    try (ZipInputStream zip=new ZipInputStream(new ClassPathResource("sepomex/20241116.txt.zip").getInputStream());) {
      if (zip.getNextEntry()==null) throw new IllegalStateException("El archivo SEPOMEX está vacío.");
      try (BufferedReader reader=new BufferedReader(new InputStreamReader(zip, StandardCharsets.ISO_8859_1))) {
        reader.readLine(); // Aviso de uso de Correos de México.
        reader.readLine(); // Encabezados.
        String line;
        while ((line=reader.readLine())!=null) {
          String[] value=line.split("\\|",-1);
          if (value.length<5 || !ZIP.matcher(value[0]).matches()) continue;
          rows.computeIfAbsent(value[0], ignored -> new MutablePostalCode(value[0],value[4],value[3])).colonias.add(value[1]);
        }
      }
    } catch (Exception error) { throw new IllegalStateException("No fue posible cargar el catálogo postal local.",error); }
    Map<String, AddressDtos.PostalCode> result=new HashMap<>();
    rows.forEach((cp,row)->result.put(cp,new AddressDtos.PostalCode(cp,row.estado,row.municipio,List.copyOf(row.colonias))));
    return Map.copyOf(result);
  }

  private static final class MutablePostalCode {
    final String codigoPostal,estado,municipio; final SortedSet<String> colonias=new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    MutablePostalCode(String cp,String state,String municipality){codigoPostal=cp;estado=state;municipio=municipality;}
  }
}
