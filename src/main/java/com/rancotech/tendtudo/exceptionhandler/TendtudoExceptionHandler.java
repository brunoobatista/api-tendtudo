package com.rancotech.tendtudo.exceptionhandler;

import com.rancotech.tendtudo.service.exception.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@ControllerAdvice
public class TendtudoExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {

        String mensagemUsuario = messageSource.getMessage("mensagem.invalida", null, LocaleContextHolder.getLocale());
        String mensagemDesenvolvedor = ex.getCause() != null ? ex.getCause().toString() : ex.toString();
        List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
        return handleExceptionInternal(ex, erros, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {

        List<Erro> erros = criarListaErros(ex.getBindingResult());
        return handleExceptionInternal(ex, erros, headers, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({ EmptyResultDataAccessException.class })
    public ResponseEntity<Object> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex, WebRequest request) {
        String mensagemUsuario = messageSource.getMessage("recurso.nao-encontrado", null, LocaleContextHolder.getLocale());
        String mensagemDesenvolvedor = ex.toString();
        List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
        return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({ NoSuchElementException.class })
    public ResponseEntity<Object> handleNoSuchElementException(NoSuchElementException ex, WebRequest request) {
        String mensagemUsuario = messageSource.getMessage("recurso.vazio", null, LocaleContextHolder.getLocale());
        String mensagemDesenvolvedor = ex.toString();
        List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
        return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({ DataIntegrityViolationException.class })
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
        String mensagemUsuario = messageSource.getMessage("recurso.opecarao-nao-permitida", null, LocaleContextHolder.getLocale());
        return this.montaMensagem(ex, request, mensagemUsuario);
    }

    @ExceptionHandler({ InvalidTokenException.class })
    public ResponseEntity<Object> handleInvalidTokenException(Exception ex, WebRequest request) {
        String mensagemUsuario = messageSource.getMessage("token.invalido", null, LocaleContextHolder.getLocale());
        return this.montaMensagem(ex, request, mensagemUsuario);
    }

    @ExceptionHandler({ NullPointerException.class })
    public ResponseEntity<Object> handleNullPointerException(Exception ex, WebRequest request) {
        String mensagemUsuario = messageSource.getMessage("valor.invalido", null, LocaleContextHolder.getLocale());
        return this.montaMensagem(ex, request, mensagemUsuario);
    }

    @ExceptionHandler({ BuscaValorNullException.class })
    public ResponseEntity<Object> handleBuscaValorNullException(Exception ex, WebRequest request) {
        String mensagemUsuario = messageSource.getMessage("busca.null", null, LocaleContextHolder.getLocale());
        return this.montaMensagem(ex, request, mensagemUsuario);
    }

    @ExceptionHandler({ AtualizarVendaException.class })
    public ResponseEntity<Object> handleAtualizarVendaException(Exception ex, WebRequest request) {
        String mensagemUsuario = messageSource.getMessage("busca.status-invalido", null, LocaleContextHolder.getLocale());
        return this.montaMensagem(ex, request, mensagemUsuario);
    }

    @ExceptionHandler({ SenhaConfirmacaoException.class })
    public ResponseEntity<Object> handleSenhaConfirmacaoException(Exception ex, WebRequest request) {
        String mensagemUsuario = messageSource.getMessage("senha.incorreta", null, LocaleContextHolder.getLocale());
        return this.montaMensagem(ex, request, mensagemUsuario);
    }

    @ExceptionHandler({ SenhaObrigatoriaException.class })
    public ResponseEntity<Object> handleSSenhaObrigatoriaException(Exception ex, WebRequest request) {
        String mensagemUsuario = messageSource.getMessage("senha.obrigatoria", null, LocaleContextHolder.getLocale());
        return this.montaMensagem(ex, request, mensagemUsuario);
    }

    @ExceptionHandler({ UsernameObrigatorioException.class })
    public ResponseEntity<Object> handleUsernameObrigatorioException(Exception ex, WebRequest request) {
        String mensagemUsuario = messageSource.getMessage("username.obrigatoria", null, LocaleContextHolder.getLocale());
        return this.montaMensagem(ex, request, mensagemUsuario);
    }

    @ExceptionHandler({ InvalidGrantException.class })
    public ResponseEntity<Object> handleInvalidGrantException(Exception ex, WebRequest request) {
        String mensagemUsuario = messageSource.getMessage("invalid.grant", null, LocaleContextHolder.getLocale());
        return this.montaMensagem(ex, request, mensagemUsuario);
    }

    private ResponseEntity<Object> montaMensagem(Exception ex, WebRequest request, String mensagemUsuario) {
        String mensagemDesenvolvedor = ExceptionUtils.getRootCauseMessage(ex);
        List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
        return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    private List<Erro> criarListaErros(BindingResult bindingResult) {
        List<Erro> erros = new ArrayList<>();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            String mensagemUsuario = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
            String mensagemDesenvolvedor = fieldError.toString();
            erros.add(new Erro(mensagemUsuario, mensagemDesenvolvedor));
        }

        return erros;
    }

    public static class Erro {

        private String mensagemUsuario;
        private String mensagemDesenvolvedor;

        public Erro(String mensagemUsuario, String mensagemDesenvolvedor) {
            this.mensagemUsuario = mensagemUsuario;
            this.mensagemDesenvolvedor = mensagemDesenvolvedor;
        }

        public String getMensagemUsuario() {
            return mensagemUsuario;
        }

        public String getMensagemDesenvolvedor() {
            return mensagemDesenvolvedor;
        }

    }

}
