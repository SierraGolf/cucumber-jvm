package cucumber.runtime.android;

import android.os.Debug;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Debug.class)
public class WaiterTest {


    @Test
    public void should_wait_for_debugger_when_flag_is_set() {
        // given
        final Arguments arguments = mock(Arguments.class);
        when(arguments.isDebugEnabled()).thenReturn(true);

        mockStatic(Debug.class);

        final Waiter waiter = new Waiter(arguments);

        // when
        waiter.requestWaitForDebugger();

        // then
        verifyStatic();
        Debug.waitForDebugger();
    }

    @Test
    public void should_not_wait_for_debugger_when_flat_is_not_set() {
        // given
        final Arguments arguments = mock(Arguments.class);
        when(arguments.isDebugEnabled()).thenReturn(false);

        mockStatic(Debug.class);

        final Waiter waiter = new Waiter(arguments);

        // when
        waiter.requestWaitForDebugger();

        // then
        verifyStatic(never());
        Debug.waitForDebugger();
    }
}
