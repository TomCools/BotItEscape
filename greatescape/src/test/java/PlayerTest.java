import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class PlayerTest {
    private static int TEST_WIDTH = 20;
    private static int TEST_HEIGHT = 20;


    @Test
    public void givenPositionLeft_canCorrectlyCalculateTargetPath() {
        int[] target = Player.calculateTarget(0, 2, TEST_WIDTH, TEST_HEIGHT);

        assertThat(target[0],is(19));
        assertThat(target[1],is(2));
    }

}