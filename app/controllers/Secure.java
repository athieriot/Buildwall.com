package controllers;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

import models.User;
import play.Play;
import play.data.validation.Required;
import play.libs.Crypto;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;
import play.utils.Java;
import utils.CypherUtil;

public class Secure extends Controller {

   public final static String LOGIN_KEY = "username";

   @Before(unless = {"login", "activate", "authenticate", "logout"})
   static void checkAccess() throws Throwable {
      // Authent
      if (!session.contains(LOGIN_KEY) || Security.invoke("connectedUser") == null) {
         session.clear();
         response.removeCookie("rememberme");
         flash.put("url", request.method == "GET" ? request.url : "/"); // seems a good default
         login();
      }
      // Checks
      Check check = getActionAnnotation(Check.class);
      if (check != null) {
         check(check);
      }
      check = getControllerInheritedAnnotation(Check.class);
      if (check != null) {
         check(check);
      }
   }

   private static void check(Check check) throws Throwable {
      for (String profile : check.value()) {
         boolean hasProfile = (Boolean) Security.invoke("check", profile);
         if (!hasProfile) {
            Security.invoke("onCheckFailed", profile);
         }
      }
   }
   // ~~~ Activation

   public static String generateEmailToken(Long id, Long timestamp, String email) {
      if(id == null || timestamp == null || email == null)
        return null;

      String digestive = id.toString() + timestamp + email + controllers.Security.CYPHER_INTERNAL_SUGAR;
      return CypherUtil.sha256Base64(digestive);
   }

   public static void activate(@Required Long id,
                               @Required String token) throws Throwable {
      User attendee = User.findById(id);

      if(isAllowedToActivation(token, attendee)) {
          attendee.activate();
          attendee.save();
          // Mark user as connected
            session.put("username", attendee.username);
          //TODO: Redirection vers la page de build
          redirect("/buildwall");
      }

      forbidden();
   }

    private static boolean isAllowedToActivation(String token, User attendee) {
        return attendee != null && token != null
                && token.equals(generateEmailToken(attendee.id, attendee.creationDate, attendee.email));
    }

    // ~~~ Login

   public static void login() throws Throwable {
      autoConnect();
      if (isConnected()) {
         redirectToOriginalURL();
      } else {
         flash.keep("url");
         render();
      }
   }

   static void authenticate(User user) throws Throwable {
       if(user != null)
           authenticate(user.username, user.password, true);
   }

   public static void authenticate(@Required String username, String password, boolean remember) throws Throwable {
      // Check tokens
      Boolean allowed = (Boolean) Security.invoke("authenticate", username, password);
      if (validation.hasErrors() || !allowed) {
         flash.keep("url");
         flash.error("secure.error");
         params.flash();
         login();
      }
      // Mark user as connected
      session.put("username", username);
      // Remember if needed
      if (remember) {
         response.setCookie("rememberme", Crypto.sign(username) + "-" + username, "30d");
      }
      // Redirect to the original URL (or /)
      redirectToOriginalURL();
   }

   public static void logout() throws Throwable {
      session.clear();
      response.removeCookie("rememberme");
      Security.invoke("onDisconnected");
      flash.success("secure.logout");
      login();
   }

   // ~~~ utils

   static void redirectToOriginalURL() throws Throwable {
      Security.invoke("onAuthenticated");
      String url = flash.get("url");
      if (url == null) {
         url = "/";
      }
      redirect(url);
   }

   /**
    * This method returns the current connected username
    * 
    * @return
    */
   static String connected() {
      Secure.autoConnect();
      return session.get(LOGIN_KEY);
   }

   /**
    * Indicate if a user is currently connected
    * 
    * @return true if the user is connected
    */
   static boolean isConnected() {
      return session.contains(LOGIN_KEY);
   }

   static void autoConnect() {
      if (!isConnected()) {
         Http.Cookie remember = request.cookies.get("rememberme");
         if (remember != null && remember.value.indexOf("-") > 0) {
            String sign = remember.value.substring(0, remember.value.indexOf("-"));
            String username = remember.value.substring(remember.value.indexOf("-") + 1);
            if (Crypto.sign(username).equals(sign)) {
               session.put(LOGIN_KEY, username);
            }
         }
      }
   }

    public static class Security extends Controller {

      /**
       * @Deprecated
       * @param username
       * @param password
       * @return
       */
      static boolean authentify(String username, String password) {
         throw new UnsupportedOperationException();
      }

      /**
       * This method is called during the authentication process. This is where you check if the user is allowed to log in into the system. This is the actual
       * authentication process against a third party system (most of the time a DB).
       * 
       * @param username
       * @param password
       * @return true if the authentication process succeeded
       */
      static boolean authenticate(String username, String password) {
         return true;
      }

      /**
       * This method checks that a profile is allowed to view this page/method. This method is called prior to the method's controller annotated with the @Check
       * method.
       * 
       * @param profile
       * @return true if you are allowed to execute this controller method.
       */
      static boolean check(String profile) {
         return true;
      }

      /**
       * This method is called after a successful authentication. You need to override this method if you with to perform specific actions (eg. Record the time
       * the user signed in)
       */
      static void onAuthenticated() {
      }

      /**
       * This method is called after a successful sign off. You need to override this method if you with to perform specific actions (eg. Record the time the
       * user signed off)
       */
      static void onDisconnected() {
      }

      /**
       * This method is called if a check does not succeed. By default it shows the not allowed page (the controller forbidden method).
       * 
       * @param profile
       */
      static void onCheckFailed(String profile) {
         forbidden();
      }

      public static User connectedUser() {
         return null;
      }

      private static Object invoke(String m, Object... args) throws Throwable {
         Class security = null;
         List<Class> classes = Play.classloader.getAssignableClasses(Security.class);
         if (classes.size() == 0) {
            security = Security.class;
         } else {
            security = classes.get(0);
         }
         try {
            return Java.invokeStaticOrParent(security, m, args);
         } catch (InvocationTargetException e) {
            throw e.getTargetException();
         }
      }

   }

}