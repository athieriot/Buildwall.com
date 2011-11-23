package jobs;

import models.User;
import org.joda.time.DateTime;
import play.Logger;
import play.jobs.On;
import play.jobs.Job;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: aurelien
 * Date: 09/11/11
 * Time: 22:19
 */

// Fire a cleanup every first day of a month at noon
@On("0 0 12 1 * ?")
public class CleanLongInactives extends Job {

    public void doJob() {
        DateTime oneMonthSooner = new DateTime();
        oneMonthSooner = oneMonthSooner.minusMonths(1);

        List<User> longInactives = User.find("activated = false and creationDate < ?", oneMonthSooner.toDate()).fetch();
        for(User inactive : longInactives) {
            inactive.delete();
            Logger.info("Monthly maintenance. Inactive user : " + inactive.toString() + " has been exterminate.");
        }
    }
}