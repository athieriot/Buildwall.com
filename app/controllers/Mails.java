package controllers;

import models.User;
import play.mvc.Mailer;

import static play.Play.*;

/**
 * Created by IntelliJ IDEA.
 * User: aurelien
 * Date: 03/11/11
 * Time: 23:42
 */


public class Mails extends Mailer {

    private static final String MAIL_FROM = "Buildwall.com <" + configuration.getProperty("mail.noreply") + ">";

    private static final String USER_CREATED_SUBJECT = "Welcome to Builwall.com";

    public static void userCreated(User user) {
        setFrom(MAIL_FROM);
        setSubject(USER_CREATED_SUBJECT);
        addRecipient(user.email);

        String token = Secure.generateEmailToken(user.id, user.creationDate.getTime(), user.email);
        send(user, token);
    }

}
