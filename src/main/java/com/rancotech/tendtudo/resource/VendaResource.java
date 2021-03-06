package com.rancotech.tendtudo.resource;

import com.rancotech.tendtudo.event.RecursoCriadoEvent;
import com.rancotech.tendtudo.model.Venda;
import com.rancotech.tendtudo.model.enumerated.StatusAtivo;
import com.rancotech.tendtudo.model.enumerated.StatusVenda;
import com.rancotech.tendtudo.repository.VendaRepository;
import com.rancotech.tendtudo.repository.filter.VendaFilter;
import com.rancotech.tendtudo.service.VendaService;
import com.rancotech.tendtudo.service.exception.AtualizarVendaException;
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
@RequestMapping("/vendas")
public class VendaResource {

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private VendaService vendaService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @GetMapping("/listar")
    @PreAuthorize("hasAnyAuthority('READ_VENDA', 'FULL_VENDA')")
    public List<Venda> findAll() {
        return vendaRepository.findAllByAtivoEquals(StatusAtivo.ATIVADO);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('READ_VENDA', 'FULL_VENDA')")
    public Page<Venda> listar(VendaFilter vendaFilter, Pageable pageable) {
        return vendaRepository.filtrar(vendaFilter, pageable);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('WRITE_VENDA', 'FULL_VENDA')")
    public ResponseEntity<Venda> salvarEmAberto(@Valid @RequestBody Venda venda, HttpServletResponse response) {
        venda.setStatus(StatusVenda.ABERTA.toString());

        Venda vendaSalvo = vendaService.salvar(venda);

        return ResponseEntity.status(HttpStatus.CREATED).body(this.salvar(vendaSalvo, response));
    }

    @PostMapping("/finalizar")
    @PreAuthorize("hasAnyAuthority('WRITE_VENDA', 'FULL_VENDA')")
    public ResponseEntity<Void> finalizar(@Valid @RequestBody Venda venda, HttpServletResponse response) {
        if (venda.getStatus().equalsIgnoreCase(StatusVenda.ABERTA.toString())) {
            venda.setStatus(StatusVenda.FINALIZADA.toString());

            vendaService.finalizar(venda);

            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            throw new AtualizarVendaException();
        }
    }

    @PostMapping("/cancelar")
    @PreAuthorize("hasAnyAuthority('WRITE_VENDA', 'FULL_VENDA')")
    public ResponseEntity<Venda> cancelar(@Valid @RequestBody Venda venda, HttpServletResponse response) {
        if (venda.getStatus().equalsIgnoreCase(StatusVenda.ABERTA.toString())) {
            venda.setStatus(StatusVenda.CANCELADA.toString());
            vendaService.cancelar(venda);
            return ResponseEntity.status(HttpStatus.OK).body(this.salvar(venda, response));
        } else {
            throw new AtualizarVendaException();
        }
    }

    @PutMapping("/estornar")
    @PreAuthorize("hasAnyAuthority('WRITE_VENDA', 'FULL_VENDA')")
    public ResponseEntity<Venda> estornar(@Valid @RequestBody Venda venda, HttpServletResponse response) {
        if (venda.getStatus().equalsIgnoreCase(StatusVenda.FINALIZADA.toString())) {
            venda.setStatus(StatusVenda.ESTORNADA.toString());
            vendaService.estornar(venda);
            return ResponseEntity.status(HttpStatus.OK).body(this.salvar(venda, response));
        } else {
            throw new AtualizarVendaException();
        }

    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('READ_VENDA', 'FULL_VENDA')")
    public ResponseEntity<Venda> buscarPorCodigo(@PathVariable Long id) {
        Optional<Venda> venda = vendaService.findByIdAtivo(id);
        return venda.isPresent() ? ResponseEntity.ok(venda.get()) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('WRITE_VENDA', 'FULL_VENDA')")
    public ResponseEntity<Venda> remover(@PathVariable Long id, Pageable pageable) {
        Venda venda = this.vendaService.remover(id, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(venda);
    }

    @DeleteMapping("/{vendaId}/{produtoId}")
    @PreAuthorize("hasAnyAuthority('WRITE_VENDA', 'FULL_VENDA')")
    public ResponseEntity<Venda> removerProduto(@PathVariable(name = "vendaId") Long vendaId, @PathVariable(name = "produtoId") Long produtoId) {
        Venda venda = this.vendaService.removerProduto(vendaId, produtoId);

        return ResponseEntity.status(HttpStatus.OK).body(venda);
    }

    private Venda salvar(Venda venda, HttpServletResponse response) {
        publisher.publishEvent(new RecursoCriadoEvent(this, response, venda.getId()));
        return venda;
    }
}
