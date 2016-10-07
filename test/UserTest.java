import models.User;
import models.dao.UserDAO;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.Logger;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static play.inject.Bindings.bind;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.SEE_OTHER;
import static play.test.Helpers.route;
import static utils.PasswordHashing.hashPassword;

/**
 * Created by octavian.salcianu on 9/13/2016.
 */
public class UserTest extends WithApplication{


    private UserDAO ud = mock(UserDAO.class);

    @Before
    public void setUp() throws Exception {
        Http.Context context = mock(Http.Context.class);
        Http.Context.current.set(context);
    }

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder()
                .configure("play.http.router", "router.Routes")
                .overrides(bind(UserDAO.class).toInstance(ud))
                .build();
    }

    @Test
    public void testLoginNoSuchUser() {
        Map form = new HashMap<String, String>();
        form.put("userName", "Abcd");
        form.put("userPass", "testtest");

        when(ud.getUserByName(anyString())).thenReturn(null);

        Result res = route(Helpers.fakeRequest(controllers.routes.UserController.tryLogin()).bodyForm(form));
        assertEquals(BAD_REQUEST, res.status());
        assertEquals(null, res.session().get("user"));
    }

    @Test
    public void testLoginSuccess() {
        Map form = new HashMap<String, String>();
        form.put("userName", "Test");
        form.put("userPass", "testtest");

        User u = new User();
        u.setUserName("Test");
        u.setUserPass(hashPassword("testtest".toCharArray()));
        u.setUserMail("test@test.com");
        when(ud.getUserByName(anyString())).thenReturn(u);

        Result res = route(Helpers.fakeRequest(controllers.routes.UserController.tryLogin()).bodyForm(form));
        assertEquals(SEE_OTHER, res.status());
        assertEquals("Test", res.session().get("user"));
    }

    @Test
    public void testLoginBadPassword() {
        Map form = new HashMap<String, String>();
        form.put("userName", "Test");
        form.put("userPass", "abcabc");

        User u = new User();
        u.setUserName("Test");
        u.setUserPass(hashPassword("testtest".toCharArray()));
        u.setUserMail("test@test.com");
        when(ud.getUserByName(anyString())).thenReturn(u);

        Result res = route(Helpers.fakeRequest(controllers.routes.UserController.tryLogin()).bodyForm(form));
        assertEquals(BAD_REQUEST, res.status());
        assertEquals(null, res.session().get("user"));
    }

    @Test
    public void testLoginPasswordTooShort() {
        Map form = new HashMap<String, String>();
        form.put("userName", "Test");
        form.put("userPass", "abc");

        Result res = route(Helpers.fakeRequest(controllers.routes.UserController.tryLogin()).bodyForm(form));
        assertEquals(BAD_REQUEST, res.status());
        assertEquals(null, res.session().get("user"));
    }

    @Test
    public void testLoginUserTooShort() {
        Map form = new HashMap<String, String>();
        form.put("userName", "te");
        form.put("userPass", "testtest");

        Result res = route(Helpers.fakeRequest(controllers.routes.UserController.tryLogin()).bodyForm(form));
        assertEquals(BAD_REQUEST, res.status());
        assertEquals(null, res.session().get("user"));
    }
}
