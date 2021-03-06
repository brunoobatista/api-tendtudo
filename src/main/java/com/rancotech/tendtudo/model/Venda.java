package com.rancotech.tendtudo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rancotech.tendtudo.model.enumerated.StatusAtivo;
import com.rancotech.tendtudo.model.enumerated.StatusVenda;
import com.rancotech.tendtudo.repository.ClienteRepository;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Entity
@Table(name = "vendas")
@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(name = "getTotalVendas",
                procedureName = "total_vendas"
        )
})
public class Venda implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="data_venda")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataVenda;

    @Column(name="valor")
    private BigDecimal valor;

    @OneToMany(
            mappedBy = "venda",
            fetch = FetchType.EAGER
    )
    private List<VendaProduto> produtos = new ArrayList<>();

    /* RELACIONAMENTO ONE TO ONE*/
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "observacao")
    private String observacao;

    @Column(name="desconto")
    private BigDecimal desconto;

    @Column(name = "cliente_id")
    private Long clienteId;

    @Column(name = "status")
    private String status;

    @JsonIgnore
    @Transient
    private Cliente cliente;

    @Column(name = "ativo")
    @Enumerated
    private StatusAtivo ativo;

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void addProduto(Produto produto, Integer quantidade) {
        VendaProduto vendaProduto = new VendaProduto(this, produto, quantidade);

        produtos.add(vendaProduto);
        //produto.getVendas().add(vendaProduto);
    }

    public void removeProduto(Produto produto) {
        for (Iterator<VendaProduto> iterator = produtos.iterator(); iterator.hasNext();) {
            VendaProduto vendaProduto = iterator.next();
            if (vendaProduto.getVenda().equals(this) &&
                    vendaProduto.getProduto().equals(produto)) {
                iterator.remove();
                //vendaProduto.getProduto().getVendas().remove(vendaProduto);
                vendaProduto.setVenda(null);
                vendaProduto.setProduto(null);
            }
        }
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public List<VendaProduto> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<VendaProduto> produtos) {
        this.produtos = produtos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(LocalDateTime dataVenda) {
        this.dataVenda = dataVenda;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public StatusAtivo getAtivo() {
        return ativo;
    }

    public void setAtivo(StatusAtivo ativo) {
        this.ativo = ativo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Venda venda = (Venda) o;
        return id.equals(venda.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}

