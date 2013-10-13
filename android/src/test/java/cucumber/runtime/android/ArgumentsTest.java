package cucumber.runtime.android;

import android.os.Bundle;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.spy;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class ArgumentsTest {

    @Test
    public void isDebugEnabled_should_return_true_when_bundle_contains_true() {
        // given
        final Bundle bundle = spy(new Bundle());
        bundle.putString(Arguments.KEY.DEBUG_ENABLED, "true");

        // when
        final Arguments arguments = new Arguments(bundle);

        // then
        assertThat(arguments.isDebugEnabled(), is(true));
    }

    @Test
    public void isDebugEnabled_should_return_false_when_bundle_contains_false() {
        // given
        final Bundle bundle = spy(new Bundle());
        bundle.putString(Arguments.KEY.DEBUG_ENABLED, "false");

        // when
        final Arguments arguments = new Arguments(bundle);

        // then
        assertThat(arguments.isDebugEnabled(), is(false));
    }

    @Test
    public void isDebugEnabled_should_return_false_when_bundle_contains_no_value() {
        // given
        final Bundle bundle = spy(new Bundle());

        // when
        final Arguments arguments = new Arguments(bundle);

        // then
        assertThat(arguments.isDebugEnabled(), is(false));
    }

    @Test
    public void coverageDataFilePath_should_return_value_when_bundle_contains_value() {
        // given
        final String fileName = "some_custome_file.name";
        final Bundle bundle = spy(new Bundle());
        bundle.putString(Arguments.KEY.COVERAGE_DATA_FILE_PATH, fileName);

        // when
        final Arguments arguments = new Arguments(bundle);

        // then
        assertThat(arguments.coverageDataFilePath(), is(fileName));
    }

    @Test
    public void coverageDataFilePath_should_return_default_value_when_bundle_contains_no_value() {
        // given
        final Bundle bundle = spy(new Bundle());

        // when
        final Arguments arguments = new Arguments(bundle);

        // then
        assertThat(arguments.coverageDataFilePath(), is(Arguments.DEFAULT.COVERAGE_DATA_FILE_PATH));
    }

    @Test
    public void isCoverageEnabled_should_return_true_when_bundle_contains_true() {
        // given
        final Bundle bundle = spy(new Bundle());
        bundle.putString(Arguments.KEY.COVERAGE_ENABLED, "true");

        // when
        final Arguments arguments = new Arguments(bundle);

        // then
        assertThat(arguments.isCoverageEnabled(), is(true));
    }

    @Test
    public void isCoverageEnabled_should_return_false_when_bundle_contains_false() {
        // given
        final Bundle bundle = spy(new Bundle());
        bundle.putString(Arguments.KEY.COVERAGE_ENABLED, "false");

        // when
        final Arguments arguments = new Arguments(bundle);

        // then
        assertThat(arguments.isCoverageEnabled(), is(false));
    }

    @Test
    public void isCoverageEnabled_should_return_false_when_bundle_contains_no_value() {
        // given
        final Bundle bundle = spy(new Bundle());

        // when
        final Arguments arguments = new Arguments(bundle);

        // then
        assertThat(arguments.isCoverageEnabled(), is(false));
    }
}
