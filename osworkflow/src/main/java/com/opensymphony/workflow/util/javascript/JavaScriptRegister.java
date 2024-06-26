package com.opensymphony.workflow.util.javascript;

import com.opensymphony.workflow.Register;
import com.opensymphony.workflow.WorkflowException;

import com.opensymphony.module.propertyset.PropertySet;

import com.opensymphony.workflow.*;
        import com.opensymphony.workflow.spi.WorkflowEntry;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Map;

public class JavaScriptRegister implements Register {

    public Object registerVariable(WorkflowContext context, WorkflowEntry entry, Map args, PropertySet ps) throws WorkflowException {
        String script = (String) args.get(AbstractWorkflow.JS_SCRIPT);

        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

        try {

            engine.put("entry", entry);
            engine.put("context", context);
            engine.put("propertySet", ps);

            return engine.eval(script);
        } catch (ScriptException e) {
            String message = "Could not get object registered in to variable map";
            throw new WorkflowException(message, e);
        }
    }
}