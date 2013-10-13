package cucumber.runtime.android;

import android.util.Log;
import cucumber.runtime.Runtime;
import gherkin.formatter.model.Background;
import gherkin.formatter.model.Examples;
import gherkin.formatter.model.Feature;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.ScenarioOutline;
import gherkin.formatter.model.Step;
import java.util.List;

/**
 * TODO
 */
public class AndroidLogcatReporter extends NoOpFormattingReporter {
    private final Runtime runtime;
    private final String logTag;
    private String uri;

    public AndroidLogcatReporter(final Runtime runtime, final String logTag) {
        this.runtime = runtime;
        this.logTag = logTag;
    }

    @Override
    public void uri(String uri) {
        this.uri = uri;
    }

    @Override
    public void feature(Feature feature) {
        Log.d(logTag, String.format("%s: %s (%s)%n%s", feature.getKeyword(), feature.getName(), uri, feature.getDescription()));
    }

    @Override
    public void background(Background background) {
        Log.d(logTag, background.getName());
    }

    @Override
    public void scenario(Scenario scenario) {
        Log.d(logTag, String.format("%s: %s", scenario.getKeyword(), scenario.getName()));
    }

    @Override
    public void scenarioOutline(ScenarioOutline scenarioOutline) {
        Log.d(logTag, String.format("%s: %s", scenarioOutline.getKeyword(), scenarioOutline.getName()));
    }

    @Override
    public void examples(Examples examples) {
        Log.d(logTag, String.format("%s: %s", examples.getKeyword(), examples.getName()));
    }

    @Override
    public void step(Step step) {
        Log.d(logTag, String.format("%s%s", step.getKeyword(), step.getName()));
    }

    @Override
    public void syntaxError(String state, String event, List<String> legalEvents, String uri, Integer line) {
        Log.e(logTag, String.format("syntax error '%s' %s:%d", event, uri, line));
    }

    @Override
    public void done() {
        for (final Throwable throwable : runtime.getErrors()) {
            Log.e(logTag, throwable.toString());
        }

        for (final String snippet : runtime.getSnippets()) {
            Log.w(logTag, snippet);
        }
    }
}
