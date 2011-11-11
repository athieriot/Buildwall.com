package controllers;

import org.joda.time.DateTime;
import org.junit.Test;
import play.test.UnitTest;

import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

/**
 * Created by IntelliJ IDEA.
 * User: aurelien
 * Date: 11/11/11
 * Time: 13:46
 */
public class SecurityTest extends UnitTest {

    @Test
    public void testGenerateEmailToken() {
        // Given
        Long id = new Long(1);
        Long creationDateTimeStamp = (new DateTime("2011-11-11T11:11:11")).getMillis();
        String email = "test@test.com";

        // When
        String code = Security.generateEmailToken(id, creationDateTimeStamp, email);

        // Then
        assertThat(code, is(equalTo("56141207c6ed5a23a8108e9944201ad6d6f59d9d8063874af774c3a62222fc16")));
    }
}
