import java.util.Random;

import org.junit.Test;

import play.test.UnitTest;

public class RandomTest extends UnitTest {

    @Test
   public void randomTest() {
      int i = new Random().nextInt(2);
      if (i < 1) {
         fail();
      }
    }

}
