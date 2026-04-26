package com.mecanix.controller;
import com.mecanix.dto.*;
import com.mecanix.service.ClienteService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/clientes")
public class ClienteController {
    private final ClienteService service;
    public ClienteController(ClienteService service){this.service=service;}
    @GetMapping public List<ClienteResponse> listar(){return service.listar();}
    @GetMapping("/{id}") public ClienteResponse buscar(@PathVariable Long id){return service.buscarPorId(id);}
    @PostMapping public ResponseEntity<ClienteResponse> criar(@RequestBody ClienteRequest req){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(req));
    }
    @PutMapping("/{id}") public ClienteResponse atualizar(@PathVariable Long id,@RequestBody ClienteRequest req){return service.atualizar(id,req);}
    @DeleteMapping("/{id}") public ResponseEntity<Void> deletar(@PathVariable Long id){service.deletar(id);return ResponseEntity.noContent().build();}
}
