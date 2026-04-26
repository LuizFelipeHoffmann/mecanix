package com.mecanix.controller;
import com.mecanix.dto.*;
import com.mecanix.service.EstoqueService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/estoque")
public class EstoqueController {
    private final EstoqueService service;
    public EstoqueController(EstoqueService service){this.service=service;}
    @GetMapping public List<EstoqueResponse> listar(){return service.listar();}
    @GetMapping("/alertas") public List<EstoqueResponse> alertas(){return service.listarAlertas();}
    @GetMapping(params="tipo") public List<EstoqueResponse> listarPorTipo(@RequestParam String tipo){return service.listarPorTipo(tipo);}
    @GetMapping("/{id}") public EstoqueResponse buscar(@PathVariable Long id){return service.buscarPorId(id);}
    @PostMapping public ResponseEntity<EstoqueResponse> criar(@RequestBody EstoqueRequest req){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(req));
    }
    @PutMapping("/{id}") public EstoqueResponse atualizar(@PathVariable Long id,@RequestBody EstoqueRequest req){return service.atualizar(id,req);}
    @DeleteMapping("/{id}") public ResponseEntity<Void> deletar(@PathVariable Long id){service.deletar(id);return ResponseEntity.noContent().build();}
}
