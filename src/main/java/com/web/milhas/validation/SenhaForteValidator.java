package com.web.milhas.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.passay.*;

import java.util.List;

/**
 * Implementação do validador de força de senha usando Passay.
 * Regras: min 8 chars, 1 maiúscula, 1 minúscula, 1 dígito, 1 especial.
 */
public class SenhaForteValidator implements ConstraintValidator<SenhaForte, String> {

    @Override
    public boolean isValid(String senha, ConstraintValidatorContext context) {
        // Se a senha for nula, assume-se que o utilizador não quer alterá-la.
        if (senha == null || senha.isBlank()) {
            return true;
        }

        PasswordValidator validator = new PasswordValidator(List.of(
                new LengthRule(8, 128),
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.Special, 1),
                new WhitespaceRule()));

        return validator.validate(new PasswordData(senha)).isValid();
    }
}
