package com.web.milhas.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Valida que a senha atende aos requisitos mínimos de complexidade:
 * - mínimo 8 caracteres
 * - pelo menos 1 letra maiúscula
 * - pelo menos 1 letra minúscula
 * - pelo menos 1 dígito
 * - pelo menos 1 caractere especial
 */
@Documented
@Constraint(validatedBy = SenhaForteValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface SenhaForte {

    String message() default "A senha deve ter no mínimo 8 caracteres, incluindo maiúscula, minúscula, número e caractere especial.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
