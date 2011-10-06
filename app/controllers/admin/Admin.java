package controllers.admin;

import controllers.Check;
import controllers.Secure;
import models.Build;
import models.User;
import models.Wall;
import org.apache.commons.lang.StringUtils;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.With;

import java.util.Collection;
import java.util.List;

@With(Secure.class)
public class Admin extends Controller {

    @Check("jblemee")
    public static void allWalls(String wallId, String wallKey) {
        renderJSON(Wall.findAll());
    }

    @Check("jblemee")
    public static void allUsers(String wallId, String wallKey) {
        renderJSON(User.findAll());
    }

      public static void openWall(String wallId, String wallKey) {
        renderJSON(User.findAll());
    }

}