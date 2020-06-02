package ml.socshared.auth.util.password;

import org.passay.*;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

// Валидатор паролей
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(ValidPassword constraintAnnotation) {

    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        PasswordValidator validator = new PasswordValidator(Arrays.asList(
                // at least 8 characters
                new LengthRule(8, 30),

                // at least one upper-case character
                new CharacterRule(EnglishCharacterData.UpperCase, 1),

                // at least one lower-case character
                new CharacterRule(EnglishCharacterData.LowerCase, 1),

                // at least one digit character
                new CharacterRule(EnglishCharacterData.Digit, 1),

//                // at least one symbol (special character)
//                new CharacterRule(EnglishCharacterData.Special, 1),

                // no whitespace
                new WhitespaceRule()

        ));
        RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }
        List<String> messages = validator.getMessages(result);

        String messageTemplate = String.join(",", messages);
        context.buildConstraintViolationWithTemplate(messageTemplate
                .replaceAll("Password must be", "Пароль должен состоять из")
                .replaceAll("or more characters in length", "или более символов")
                .replaceAll("Password must contain", "Пароль должен содержать ")
                .replaceAll("or more uppercase characters", "или более заглавных букв")
                .replaceAll("Password must contain", "Пароль должен содержать")
                .replaceAll("or more lowercase characters", "или более строчных букв")
                .replaceAll("Password must contain", "Пароль должен содержать")
                .replaceAll("or more digit characters", "или более цифр")
        ).addConstraintViolation().disableDefaultConstraintViolation();
        return false;
    }
}
