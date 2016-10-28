import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by tomco on 25/10/2016.
 */
public class SolutionTest {

    @Before
    public void init() {
        Solution.init();
    }

    @Test
    public void alreadyCorrectString() {
        String s = "[]{}<>{}";
        assertThat("Not Valid", Solution.isValid(s), is(true));
        assertThat(Solution.flippable(s), is(true));
    }

    @Test
    public void missingBracketIsInvalid() {
        String s = "[{}<>{}";
        assertThat("Valid",Solution.isValid(s), is(false));
    }

    @Test
    public void missingBracketIsNotFlippable() {
        String s = "[{}<>{}";
        assertThat("Flippable",Solution.flippable(s), is(false));
    }

    @Test
    public void invert1() {
        String s = "[[{}<>{}";
        assertThat(Solution.flippable(s), is(true));
    }

    @Test
    public void correctStringWithLettersInBetween() {
        String s = "[234{}<99>{00}]";
        assertThat(Solution.flippable(s), is(true));
    }

    @Test
    public void multipleFlippingNeeded() {
        String s = "[[}}";
        assertThat(Solution.flippable(s), is(true));
    }

    @Test
    public void tooSlow() {
        Solution.flippable("<{[(abc(]}>");
        Solution.flippable("<{[(abc>}])");
    }

}