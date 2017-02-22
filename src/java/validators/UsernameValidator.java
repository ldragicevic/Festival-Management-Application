/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package validators;

import entities.Users;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import utils.UsersHelper;

/**
 *
 * @author korisnik
 */
@FacesValidator("usernameValidator")
public class UsernameValidator implements Validator {

    private String username = "";

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        username = value.toString();
        Users user = UsersHelper.getUserByUsername(username);
        if (!validateUsernameLength() || user != null) {
            FacesMessage message = new FacesMessage("");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

    private boolean validateUsernameLength() {
        return username.length() >= 1 && username.length() <= 10;
    }

}
