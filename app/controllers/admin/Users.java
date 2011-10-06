package controllers.admin;

import controllers.CRUD;
import controllers.Check;
import controllers.Secure;
import play.mvc.With;

@Check("jblemee")
@With(Secure.class)
public class Users extends CRUD {

    }
