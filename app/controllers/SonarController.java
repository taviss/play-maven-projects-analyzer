package controllers;

import models.Sonar;
import models.dao.SonarDAO;
import models.dao.UserDAO;
import play.data.Form;
import play.data.FormFactory;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.sonar;
import views.html.sonarlist;

import javax.inject.Inject;

import static play.mvc.Results.ok;

/**
 * Created by octavian.salcianu on 8/25/2016.
 */
public class SonarController extends Controller {

    @Inject
    private FormFactory formFactory;

    @Inject
    private SonarDAO sonarDAO;

    @Transactional
    @Security.Authenticated(Secured.class)
    public Result getAll() {
        return ok(Json.toJson(sonarDAO.getAll()));
    }

    @Transactional
    @Security.Authenticated(Secured.class)
    public Result deleteSonar(Long id) {
        Sonar sonar = sonarDAO.get(id);
        sonarDAO.delete(sonar);
        return ok();
    }

    @Security.Authenticated(Secured.class)
    @Transactional
    public Result listSonars() {
        return(ok(sonarlist.render()));
    }

    @Security.Authenticated(Secured.class)
    @Transactional
    public Result addSonar() {
        return(ok(sonar.render()));
    }

    @Transactional
    @Security.Authenticated(Secured.class)
    public Result createSonar() {
        Form<Sonar> sonarForm = formFactory.form(Sonar.class).bindFromRequest();
        if (!sonarForm.hasErrors()) {
            Sonar sonar = sonarForm.get();
            sonarDAO.create(sonar);
            return ok("Sonar host added");
        } else {
            return badRequest(sonarForm.errorsAsJson());
        }
    }
}
