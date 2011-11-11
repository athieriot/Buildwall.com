package controllers;

import models.User;
import play.libs.Crypto;
import play.mvc.Controller;

public class Application extends Controller {

    public static void index() {
        render();
    }

    public static void home() {
        render();
    }

    public static void pricing() {
        render();
    }

    public static void signup() {
        render();
    }

    public static void terms() {
        render();
    }

    public static void termsFullScreen() {
        render();
    }

    public static void about() {
        render();
    }

    public static void help() {
        render();
    }

    public static void tour() {
        render();
    }

    public static void legals() {
        render();
    }

    public static void handleSignup(User user) {
        if (!validation.valid(user).ok) {
            boolean userAlreadyExists = userAlreadyExists(user);
            render("@signupWithErrors", user, userAlreadyExists);
        } else {
            createUser(user);
        }
    }

    static void createUser(User user) {
        boolean userAlreadyExists = userAlreadyExists(user);
        if (userAlreadyExists) {
            render("@signupWithErrors", user, userAlreadyExists);
        }
        user.password = Crypto.passwordHash(user.password);
        user.passwordConfirm = Crypto.passwordHash(user.passwordConfirm);
        user.save();

        Mails.userCreated(user);
        render(user);
    }

    protected static boolean userAlreadyExists(User user) {
        return (null != Security.findUser(user.username));
    }

}