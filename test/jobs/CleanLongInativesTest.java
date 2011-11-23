package jobs;

import models.User;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import play.test.Fixtures;
import play.test.UnitTest;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

/**
 * Created by IntelliJ IDEA.
 * User: aurelien
 * Date: 09/11/11
 * Time: 23:48
 */
public class CleanLongInativesTest extends UnitTest {

    @Before
    public void setUp() {
        Fixtures.deleteAllModels();
        Fixtures.loadModels("users.yml");
    }

    @Test
    public void testRunOneActiveAndOneOld() {
        // Given
        DateTime twoMonthsSooner = new DateTime();
        twoMonthsSooner = twoMonthsSooner.minusMonths(2);

        User inactive = User.find("username = 'Unbob'").first();
        inactive.creationDate = twoMonthsSooner.toDate();
        inactive.save();

        // When
        CleanLongInactives cli = new CleanLongInactives();
        cli.doJob();

        // Then
        assertThat(User.find("username = 'Bob'").first(), notNullValue());
        assertThat(User.find("username = 'Unbob'").first(), nullValue());
    }

    @Test
    public void testRunOneActiveAndOneYoung() {
        // Given
        DateTime twoWeeksSooner = new DateTime();
        twoWeeksSooner = twoWeeksSooner.minusWeeks(2);

        User inactive = User.find("username = 'Unbob'").first();
        inactive.creationDate = twoWeeksSooner.toDate();
        inactive.save();

        // When
        CleanLongInactives cli = new CleanLongInactives();
        cli.doJob();

        // Then
        assertThat(User.find("username = 'Bob'").first(), notNullValue());
        assertThat(User.find("username = 'Unbob'").first(), notNullValue());
    }
}
