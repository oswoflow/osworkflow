package com.opensymphony.workflow.util.javascript;


import com.opensymphony.workflow.Condition;
import com.opensymphony.workflow.WorkflowException;

import com.opensymphony.module.propertyset.PropertySet;

import com.opensymphony.util.TextUtils;

import com.opensymphony.workflow.*;
import com.opensymphony.workflow.spi.WorkflowEntry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Map;

public class JavaScriptCondition implements Condition {
    // ~ Static fields/initializers /////////////////////////////////////////////

    private static final Log log = LogFactory.getLog(JavaScriptCondition.class);

    // ~ Methods ////////////////////////////////////////////////////////////////

    public boolean passesCondition(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
        String script = (String) args.get(AbstractWorkflow.JS_SCRIPT);

        WorkflowContext context = (WorkflowContext) transientVars.get("context");
        WorkflowEntry entry = (WorkflowEntry) transientVars.get("entry");

        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

        try {

            engine.put("entry", entry);
            engine.put("context", context);
            engine.put("transientVars", transientVars);
            engine.put("propertySet", ps);
            engine.put("jn", transientVars.get("jn"));

            Object o = engine.eval(script);

            if (o == null) {
                return false;
            } else {
                return TextUtils.parseBoolean(o.toString());
            }
        } catch (ScriptException e) {
            String message = "Could not execute JavaScript script";
            log.error(message, e);
            throw new WorkflowException(message, e);
        }
    }
}