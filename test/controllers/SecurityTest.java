package controllers;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import play.test.Fixtures;
import play.test.UnitTest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

/**
 * Created by IntelliJ IDEA.
 * User: aurelien
 * Date: 11/11/11
 * Time: 13:46
 */
public class SecurityTest extends UnitTest {

    @Before
    public void setUp() {
        Fixtures.deleteAllModels();
        Fixtures.loadModels("users.yml");
    }

    @Test
    public void testGenerateEmailToken() {
        // Given
        Long id = new Long(1);
        Long creationDateTimeStamp = (new DateTime("2011-11-11T11:11:11")).getMillis();
        String email = "test@test.com";

        // When
        String code = Secure.generateEmailToken(id, creationDateTimeStamp, email);

        // Then
        assertThat(code, is(equalTo("VhQSB8btWiOoEI6ZRCAa1tb1nZ2AY4dK93TDpiIi/BY=")));
    }

    @Test
    public void testAuthenticationAgainstActivateStatusWrong() {
        // Given

        // When
        boolean code = Security.authenticate("Unbob", "secret");

        // Then
        assertThat(code, is(not(true)));
    }

    @Test
    public void testAuthenticationAgainstActivateStatusOk() {
        // Given

        // When
        boolean code = Security.authenticate("Bob", "secret");

        // Then
        assertThat(code, is(true));
    }
}
