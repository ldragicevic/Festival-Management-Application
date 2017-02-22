/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package validators;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("passwordValidator")
public class PasswordValidator implements Validator {

    private static final String formatRequired = "Please enter password with: 6-12 chars. 1+ uppercase. 3+ lowercase. 1+ numeric/special. First char letter. Max 2 near same characters.";
    private String password = "";

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        password = value.toString();
        if (!(hasCorrentLength() && hasUppercase() && hasLowercases() && hasSpecialChar() && hasFirstCharLetter() && hasNearCharsDifference())) {
            FacesMessage message = new FacesMessage(formatRequired);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

    private boolean hasCorrentLength() {
        return password.length() >= 6 && password.length() <= 12;
    }

    private boolean hasUppercase() {
        for (int i = 0; i < password.length(); i++) {
            if (Character.isUpperCase(password.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private boolean hasLowercases() {
        int lowercaseNumber = 0;
        for (int i = 0; i < password.length(); i++) {
            if (Character.isLowerCase(password.charAt(i))) {
                lowercaseNumber++;
            }
        }
        return lowercaseNumber >= 3;
    }

    private boolean hasSpecialChar() {
        int lowercaseNumber = 0;
        for (int i = 0; i < password.length(); i++) {
            if (!Character.isAlphabetic(password.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private boolean hasFirstCharLetter() {
        return Character.isLowerCase(password.charAt(0)) || Character.isUpperCase(password.charAt(0));
    }

    private boolean hasNearCharsDifference() {
        for (int i = 0; i < password.length() - 2; i++) {
            if (password.charAt(i) == password.charAt(i + 1) && password.charAt(i + 1) == password.charAt(i + 2) && password.charAt(i) == password.charAt(i + 2)) {
                return false;
            }
        }
        return true;
    }

}
