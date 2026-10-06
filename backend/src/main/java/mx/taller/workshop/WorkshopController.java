package mx.taller.workshop;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workshops")
public class WorkshopController {
  private final WorkshopService service;
  public WorkshopController(WorkshopService service){ this.service=service; }
  @GetMapping public List<WorkshopDtos.Summary> list(@RequestHeader(value="Authorization",required=false) String auth){ return service.list(auth); }
  @PostMapping @ResponseStatus(HttpStatus.CREATED) public WorkshopDtos.Result create(@Valid @RequestBody WorkshopDtos.Upsert request,@RequestHeader(value="Authorization",required=false) String auth){ return service.create(request,auth); }
  @PutMapping("/{id}") public WorkshopDtos.Result update(@PathVariable Long id,@Valid @RequestBody WorkshopDtos.Upsert request,@RequestHeader(value="Authorization",required=false) String auth){ return service.update(id,request,auth); }
}
