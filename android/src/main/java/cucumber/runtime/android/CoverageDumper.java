package cucumber.runtime.android;

import android.app.Instrumentation;
import android.os.Bundle;
import android.util.Log;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * TODO refactor this so that it is not harcoded to emma by introducing an abstraction
 * <p/>
 * Dumps coverage data into a file.
 */
public class CoverageDumper {

    private static final String REPORT_KEY_COVERAGE_PATH = "coverageFilePath";
    private static final String STREAM_OUTPUT = "%s\nGenerated code coverage data to %s";

    /**
     * The implementation of the code coverage tool.
     * Currently known implementations are emma and jacoco.
     */
    public static final String IMPLEMENTATION_CLASS = "com.vladium.emma.rt.RT";

    /**
     * The method to call for dumping the coverage data.
     */
    private static final String IMPLEMENTATION_METHOD = "dumpCoverageData";

    /**
     * The arguments to work with.
     */
    private final Arguments arguments;

    /**
     * Creates a new instance for the given arguments.
     *
     * @param arguments the arguments to work with
     */
    public CoverageDumper(final Arguments arguments) {
        this.arguments = arguments;
    }

    /**
     * Dumps the coverage data into the given file.
     */
    public void requestDump(final Bundle results) {

        if (!arguments.isCoverageEnabled()) {
            return;
        }

        final String coverageDateFilePath = arguments.coverageDataFilePath();
        final File coverageFile = new File(coverageDateFilePath);

        try {
            final Class dumperClass = Class.forName(IMPLEMENTATION_CLASS);
            final Method dumperMethod = dumperClass.getMethod(IMPLEMENTATION_METHOD, coverageFile.getClass(), boolean.class, boolean.class);
            dumperMethod.invoke(null, coverageFile, false, false);

            results.putString(REPORT_KEY_COVERAGE_PATH, coverageDateFilePath);
            final String currentStream = results.getString(Instrumentation.REPORT_KEY_STREAMRESULT);
            results.putString(Instrumentation.REPORT_KEY_STREAMRESULT, String.format(STREAM_OUTPUT, currentStream, coverageDateFilePath));
        } catch (final ClassNotFoundException e) {
            reportError(results, e);
        } catch (final SecurityException e) {
            reportError(results, e);
        } catch (final NoSuchMethodException e) {
            reportError(results, e);
        } catch (final IllegalAccessException e) {
            reportError(results, e);
        } catch (final InvocationTargetException e) {
            reportError(results, e);
        }
    }

    private void reportError(final Bundle results, final Exception e) {
        final String msg = "Failed to generate coverage.";
        Log.e(CucumberExecutor.TAG, msg, e);
        results.putString(Instrumentation.REPORT_KEY_STREAMRESULT, "\nError: " + msg);
    }
}
