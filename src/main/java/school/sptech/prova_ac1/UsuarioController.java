package school.sptech.prova_ac1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository repository;

    @PostMapping
    public ResponseEntity<Usuario> criar(@RequestBody Usuario usuario) {
        if (repository.findByEmail(usuario.getEmail()).isPresent() ||
            repository.findByCpf(usuario.getCpf()).isPresent()) {
            return ResponseEntity.status(409).build();
        }
        Usuario salvo = repository.save(usuario);
        return ResponseEntity.status(201).body(salvo);
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> buscarTodos() {
        List<Usuario> lista = repository.findAll();
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Integer id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filtro-data")
    public ResponseEntity<List<Usuario>> buscarPorDataNascimento(
            @RequestParam LocalDate nascimento) {
        List<Usuario> lista = repository.findByDataNascimentoAfter(nascimento);
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(
            @PathVariable Integer id,
            @RequestBody Usuario usuario) {

        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        Optional<Usuario> emailExistente = repository.findByEmail(usuario.getEmail());
        if (emailExistente.isPresent() && !emailExistente.get().getId().equals(id)) {
            return ResponseEntity.status(409).build();
        }

        Optional<Usuario> cpfExistente = repository.findByCpf(usuario.getCpf());
        if (cpfExistente.isPresent() && !cpfExistente.get().getId().equals(id)) {
            return ResponseEntity.status(409).build();
        }

        usuario.setId(id);
        Usuario atualizado = repository.save(usuario);

        return ResponseEntity.ok(atualizado);
    }
}
