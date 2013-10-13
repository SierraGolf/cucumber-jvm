package cucumber.api.android;

import android.app.Activity;
import android.app.Instrumentation;
import android.os.Bundle;
import android.os.Looper;
import cucumber.runtime.android.Arguments;
import cucumber.runtime.android.CoverageDumper;
import cucumber.runtime.android.CucumberExecutor;
import cucumber.runtime.android.Waiter;


public class CucumberInstrumentation extends Instrumentation {
    public static final String REPORT_VALUE_ID = "CucumberInstrumentation";
    public static final String REPORT_KEY_NUM_TOTAL = "numtests";
    public static final String TAG = "cucumber-android";


    private Waiter waiter;
    private CoverageDumper coverageDumper;
    private CucumberExecutor cucumberExecutor;
    private Arguments arguments;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        arguments = new Arguments(bundle);
        cucumberExecutor = new CucumberExecutor(arguments, this);
        coverageDumper = new CoverageDumper(arguments);
        waiter = new Waiter(arguments);
        start();
    }

    @Override
    public void onStart() {
        Looper.prepare();

        final Bundle results = new Bundle();
        if (arguments.isCountEnabled()) {
            results.putString(Instrumentation.REPORT_KEY_IDENTIFIER, REPORT_VALUE_ID);
            results.putInt(REPORT_KEY_NUM_TOTAL, cucumberExecutor.getNumberOfTests());
            finish(Activity.RESULT_OK, results);
        } else {
            waiter.requestWaitForDebugger();
            cucumberExecutor.execute();
            coverageDumper.requestDump(results);
            finish(Activity.RESULT_OK, results);
        }
    }
}
