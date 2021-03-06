package com.rancotech.tendtudo.resource;

import com.rancotech.tendtudo.event.RecursoCriadoEvent;
import com.rancotech.tendtudo.model.Cliente;
import com.rancotech.tendtudo.model.Role;
import com.rancotech.tendtudo.model.Usuario;
import com.rancotech.tendtudo.model.enumerated.StatusAtivo;
import com.rancotech.tendtudo.repository.RoleRepository;
import com.rancotech.tendtudo.repository.UsuarioRepository;
import com.rancotech.tendtudo.repository.filter.UsuarioFilter;
import com.rancotech.tendtudo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioResource {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('READ_USUARIO', 'FULL_USUARIO')")
    public Page<Usuario> listar(UsuarioFilter usuarioFilter, Pageable pageable) {
        return this.usuarioRepository.filtrar(usuarioFilter, pageable);
    }

    @GetMapping("/search/{valor}")
    @PreAuthorize("hasAnyAuthority('READ_USUARIO', 'FULL_USUARIO')")
    public List<Usuario> procurarUsuario(@PathVariable String valor) {

        return this.usuarioRepository.findByNomeContainsIgnoreCaseAndAtivoEquals(valor, Integer.parseInt(StatusAtivo.ATIVADO.toString()));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('WRITE_USUARIO', 'FULL_USUARIO')")
    public ResponseEntity<Usuario> salvar(@Valid @RequestBody Usuario usuario, HttpServletResponse response) {
        Usuario usuarioSalvo = this.usuarioService.salvar(usuario);
        this.publisher.publishEvent(new RecursoCriadoEvent(this, response, usuarioSalvo.getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioSalvo);
    }

    @PostMapping("/edit")
    @PreAuthorize("hasAnyAuthority('WRITE_USUARIO', 'FULL_USUARIO')")
    public ResponseEntity<Usuario> editar(@Valid @RequestBody Usuario usuario, HttpServletResponse response) {
        Usuario usuarioSalvo = this.usuarioService.editar(usuario);
        this.publisher.publishEvent(new RecursoCriadoEvent(this, response, usuarioSalvo.getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioSalvo);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('READ_USUARIO', 'FULL_USUARIO')")
    public ResponseEntity<Usuario> buscarPorCódigo(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioRepository.findByIdAndAtivoEquals(id, StatusAtivo.ATIVADO);
        return usuario.isPresent() ? ResponseEntity.ok(usuario.get()) : ResponseEntity.notFound().build();
    }

    @GetMapping("/permissoes")
    @PreAuthorize("hasAnyAuthority('READ_USUARIO', 'FULL_USUARIO')")
    public List<Role> listarPermissoes() {
        return this.roleRepository.findAllByNomeNotContains("ROOT");
    }

    @DeleteMapping("/only/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('WRITE_USUARIO', 'FULL_USUARIO')")
    public void removerById(@PathVariable Long id) {
        this.usuarioRepository.deleteById(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('WRITE_USUARIO', 'FULL_USUARIO')")
    public ResponseEntity<Usuario> remover(@PathVariable Long id, Pageable pageable) {
        Usuario usuario = usuarioService.remover(id, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(usuario);
    }


}
