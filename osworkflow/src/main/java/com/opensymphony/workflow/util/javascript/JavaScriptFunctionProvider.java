package com.opensymphony.workflow.util.javascript;

import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.workflow.WorkflowException;

import com.opensymphony.module.propertyset.PropertySet;

import com.opensymphony.workflow.*;
        import com.opensymphony.workflow.spi.WorkflowEntry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Map;

public class JavaScriptFunctionProvider implements FunctionProvider {
    private static final Log log = LogFactory.getLog(JavaScriptFunctionProvider.class);

    public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
        String script = (String) args.get(AbstractWorkflow.JS_SCRIPT);

        WorkflowContext context = (WorkflowContext) transientVars.get("context");
        WorkflowEntry entry = (WorkflowEntry) transientVars.get("entry");
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

        try {
            if (loader != null) {
                Thread.currentThread().setContextClassLoader(loader);
            }

            engine.put("entry", entry);
            engine.put("context", context);
            engine.put("transientVars", transientVars);
            engine.put("propertySet", ps);

            engine.eval(script);
        } catch (ScriptException e) {
            String message = "Evaluation error while running JavaScript function script";
            log.error(message, e);
            throw new WorkflowException(message, e);
        } finally {
            if (loader != null) {
                Thread.currentThread().setContextClassLoader(null);
            }
        }
    }
}