package forms;

import models.User;
import models.dao.UserDAO;
import play.Logger;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.db.jpa.JPA;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import static utils.PasswordHashing.validatePassword;

/**
 * Created by octavian.salcianu on 8/29/2016.
 */
public class LoginForm {

    @Constraints.Required()
    @Constraints.MinLength(3)
    @Constraints.MaxLength(64)
    public String userName;

    @Constraints.Required()
    @Constraints.MinLength(6)
    @Constraints.MaxLength(256)
    public String userPass;
}
