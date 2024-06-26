package com.opensymphony.workflow.util.javascript;

import com.opensymphony.workflow.InvalidInputException;
import com.opensymphony.workflow.Validator;
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

public class JavaScriptValidator implements Validator {
    private static final Log log = LogFactory.getLog(JavaScriptValidator.class);

    public void validate(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

        try {
            String contents = (String) args.get(AbstractWorkflow.JS_SCRIPT);

            WorkflowContext context = (WorkflowContext) transientVars.get("context");
            WorkflowEntry entry = (WorkflowEntry) transientVars.get("entry");

            engine.put("entry", entry);
            engine.put("context", context);
            engine.put("transientVars", transientVars);
            engine.put("propertySet", ps);

            Object o = engine.eval(contents);

            if (o != null) {
                throw new InvalidInputException(o);
            }
        } catch (ScriptException e) {
            Throwable cause = e.getCause();
            if (cause instanceof WorkflowException) {
                throw (WorkflowException) cause;
            } else {
                String message = "Unexpected exception in JavaScript validator script:" + e.getMessage();
                throw new WorkflowException(message, e);
            }
        } catch (Exception e) {
            String message = "Error executing JavaScript validator";
            log.error(message, e);
            throw new WorkflowException(message, e);
        }
    }
}