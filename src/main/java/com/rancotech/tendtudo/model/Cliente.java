package com.rancotech.tendtudo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rancotech.tendtudo.model.enumerated.StatusAtivo;
import com.rancotech.tendtudo.model.enumerated.TipoPessoa;
import com.rancotech.tendtudo.model.validation.ClienteGroupSequenceProvider;
import com.rancotech.tendtudo.validation.CpfCnpjUnique;
import org.hibernate.validator.group.GroupSequenceProvider;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.Objects;

@CpfCnpjUnique(cpfCnpj = "cpfCnpj", id = "id", message = "CPF/CNPJ já existentes")
@Entity
@Table(name = "clientes")
@GroupSequenceProvider(ClienteGroupSequenceProvider.class)
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    private String nome;

    @Email
    @NotEmpty
    private String email;

    private String password;

    @Transient
    private String confirmPassword;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pessoa")
    private TipoPessoa tipoPessoa;

    @Column(name = "cpf_cnpj")
    private String cpfCnpj;

    @Column(name = "confirmado")
    private boolean confirmado;

    @Column(name = "ativo")
    @Enumerated
    private StatusAtivo ativo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.setCpfCnpjDoc();
    }

    @PreUpdate
    private void preUpdate() {
        this.setCpfCnpjDoc();
    }

    private void setCpfCnpjDoc() {
        if (this.cpfCnpj == null || this.cpfCnpj.isEmpty()) {
            this.cpfCnpj = null;
        } else {
            this.cpfCnpj = TipoPessoa.removerFormatacao(this.cpfCnpj);
        }
    }

    @PostLoad
    @PostUpdate
    private void postLoad() {
        if (this.cpfCnpj != null && !this.cpfCnpj.isEmpty())
            this.cpfCnpj = this.tipoPessoa.formatar(this.cpfCnpj);
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public TipoPessoa getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(TipoPessoa tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public void setConfirmado(boolean confirmado) {
        this.confirmado = confirmado;
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
        Cliente cliente = (Cliente) o;
        return Objects.equals(id, cliente.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
