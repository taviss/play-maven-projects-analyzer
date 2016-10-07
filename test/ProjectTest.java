import models.Project;
import models.dao.ProjectDAO;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static play.inject.Bindings.bind;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.NOT_FOUND;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.route;

/**
 * Created by octavian.salcianu on 8/31/2016.
 */
public class ProjectTest extends WithApplication{
    private ProjectDAO pd = mock(ProjectDAO.class);

    @Before
    public void setUp() throws Exception {
        Http.Context context = mock(Http.Context.class);
        Http.Context.current.set(context);

		/* Neutralize ProjectDAO methods that interact with DB */
        when(pd.create(any(Project.class))).thenReturn(null);
        when(pd.update(any())).thenReturn(null);
        doNothing().when(pd).delete(any());
    }

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder()
                .configure("play.http.router", "router.Routes")
                .overrides(bind(ProjectDAO.class).toInstance(pd))
                .build();
    }

    @Test
    public void testUploadNameTooShort() {
        Map form = new HashMap<String, String>();
        form.put("name", "Ab");

        Result r = route(Helpers.fakeRequest(controllers.routes.ProjectController.uploadProject()).bodyForm(form));

        assertEquals(BAD_REQUEST, r.status());
    }

    @Test
    public void testGetProjectNoSuchId() {
        Result r = route(Helpers.fakeRequest(controllers.routes.ProjectController.getProject(Long.MAX_VALUE)));

        assertEquals(BAD_REQUEST, r.status());
    }

    /*

    @Test
    public void testGetProjectsPageNumberTooBig() {
        Result r = route(Helpers.fakeRequest(controllers.routes.ProjectController.getProjects(10000)));

        assertEquals(OK, r.status());
    }

    */

    @Test
    public void testDeleteProjectNoSuchId() {
        Result r = route(Helpers.fakeRequest(controllers.routes.ProjectController.deleteProject(Long.MAX_VALUE)).session("user", "admin"));

        assertEquals(NOT_FOUND, r.status());
    }

    @Test
    public void testAnalyzeProjectNoSuchId() {
        Result r = route(Helpers.fakeRequest(controllers.routes.ProjectController.analyzeProject(Long.MAX_VALUE)).session("user", "admin"));

        assertEquals(NOT_FOUND, r.status());
    }

}
