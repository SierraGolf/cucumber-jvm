package cucumber.runtime.android;

import android.app.Instrumentation;
import android.content.Context;
import android.util.Log;
import cucumber.api.CucumberOptions;
import cucumber.runtime.Backend;
import cucumber.runtime.ClassFinder;
import cucumber.runtime.CucumberException;
import cucumber.runtime.Runtime;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.RuntimeOptionsFactory;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.java.JavaBackend;
import cucumber.runtime.java.ObjectFactory;
import cucumber.runtime.model.CucumberFeature;
import dalvik.system.DexFile;
import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Executes the cucumber tests.
 */
public class CucumberExecutor {

    public static final String TAG = "cucumber-android";

    private final Instrumentation instrumentation;
    private final ClassLoader classLoader;
    private final ClassFinder classFinder;
    private final ResourceLoader resourceLoader;
    private final RuntimeOptions runtimeOptions;
    private final cucumber.runtime.Runtime runtime;
    private final List<CucumberFeature> cucumberFeatures;

    public CucumberExecutor(final Arguments arguments, final Instrumentation instrumentation) {

        trySetCucumberOptionsToSystemProperties(arguments);

        final Context context = instrumentation.getContext();
        this.instrumentation = instrumentation;
        this.classLoader = context.getClassLoader();
        this.classFinder = createDexClassFinder(context);
        this.resourceLoader = new AndroidResourceLoader(context);
        this.runtimeOptions = createRuntimeOptions(context);
        this.runtime = new Runtime(resourceLoader, classLoader, createBackends(), runtimeOptions);
        this.cucumberFeatures = runtimeOptions.cucumberFeatures(resourceLoader);
    }

    private void trySetCucumberOptionsToSystemProperties(Arguments arguments) {
        final String cucumberOptions = arguments.getCucumberOptions();
        if (!cucumberOptions.isEmpty()) {
            Log.d(TAG, "Setting cucumber.options from arguments: '" + cucumberOptions + "'");
            System.setProperty("cucumber.options", cucumberOptions);
        }
    }

    private ClassFinder createDexClassFinder(final Context context) {
        final String apkPath = context.getPackageCodePath();
        return new DexClassFinder(newDexFile(apkPath));
    }

    private DexFile newDexFile(final String apkPath) {
        try {
            return new DexFile(apkPath);
        } catch (final IOException e) {
            throw new CucumberException("Failed to open " + apkPath);
        }
    }

    private RuntimeOptions createRuntimeOptions(final Context context) {
        for (final Class<?> clazz : classFinder.getDescendants(Object.class, context.getPackageName())) {
            if (clazz.isAnnotationPresent(CucumberOptions.class)) {
                Log.d(TAG, "Found CucumberOptions in class " + clazz.getName());
                final Class<?> optionsAnnotatedClass = clazz;
                final RuntimeOptionsFactory factory = new RuntimeOptionsFactory(optionsAnnotatedClass, CucumberOptions.class);
                return factory.create();
            }
        }

        throw new CucumberException("No CucumberOptions annotation");
    }

    private Collection<? extends Backend> createBackends() {
        final ObjectFactory delegateObjectFactory = JavaBackend.loadObjectFactory(classFinder);
        final AndroidObjectFactory objectFactory = new AndroidObjectFactory(delegateObjectFactory, instrumentation);
        final List<Backend> backends = new ArrayList<Backend>();
        backends.add(new JavaBackend(objectFactory, classFinder));
        return backends;
    }

    public void execute() {

        runtimeOptions.getFormatters().add(new AndroidInstrumentationReporter(runtime, instrumentation, getNumberOfTests()));
        runtimeOptions.getFormatters().add(new AndroidLogcatReporter(runtime, TAG));

        final Reporter reporter = runtimeOptions.reporter(classLoader);
        final Formatter formatter = runtimeOptions.formatter(classLoader);

        for (final CucumberFeature cucumberFeature : cucumberFeatures) {
            cucumberFeature.run(formatter, reporter, runtime);
        }

        formatter.done();
        formatter.close();
    }

    public int getNumberOfTests() {
        return TestCaseCounter.countTestCasesOf(cucumberFeatures);
    }
}
