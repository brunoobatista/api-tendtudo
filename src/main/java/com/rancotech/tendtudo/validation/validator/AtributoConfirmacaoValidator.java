package com.rancotech.tendtudo.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;

import com.rancotech.tendtudo.validation.AtributosConfirmacao;
import org.apache.commons.beanutils.BeanUtils;

public class AtributoConfirmacaoValidator implements ConstraintValidator<AtributosConfirmacao, Object> {

    private String atributo;
    private String atributoConfirmacao;
    private String id;

    @Override
    public void initialize(AtributosConfirmacao constraintAnnotation) {
        this.atributo = constraintAnnotation.atributo();
        this.atributoConfirmacao = constraintAnnotation.atributoConfirmacao();
        this.id = constraintAnnotation.id();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        boolean valido = false;

        try {
            Object valorAtributo = BeanUtils.getProperty(object, this.atributo);
            Object valorAtributoConfirmacao = BeanUtils.getProperty(object, this.atributoConfirmacao);
            Object valorId = BeanUtils.getProperty(object, this.id);

            if (valorId != null) {
                valido = true;
            } else if (valorId == null && valorAtributo == null) {
                context.disableDefaultConstraintViolation();
                String mensagem = context.getDefaultConstraintMessageTemplate();
                ConstraintViolationBuilder violationBuilder = context.buildConstraintViolationWithTemplate("Senha é obrigatória");
                violationBuilder.addPropertyNode(atributo).addConstraintViolation();
                return true;
            } else {
                valido = ambosSaoNull(valorAtributo, valorAtributoConfirmacao) || ambosSaoIguais(valorAtributo, valorAtributoConfirmacao);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro recuperando valores dos atributos", e);
        }

        if (!valido) {
            context.disableDefaultConstraintViolation();
            String mensagem = context.getDefaultConstraintMessageTemplate();
            ConstraintViolationBuilder violationBuilder = context.buildConstraintViolationWithTemplate(mensagem);
            violationBuilder.addPropertyNode(atributoConfirmacao).addConstraintViolation();
        }

        return valido;
    }

    private boolean ambosSaoIguais(Object valorAtributo, Object valorAtributoConfirmacao) {
        return valorAtributo != null && valorAtributo.equals(valorAtributoConfirmacao);
    }

    private boolean ambosSaoNull(Object valorAtributo, Object valorAtributoConfirmacao) {
        return valorAtributo == null && valorAtributoConfirmacao == null;
    }

}