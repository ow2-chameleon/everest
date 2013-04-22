package org.apache.felix.ipojo.everest.impl;

import org.apache.felix.ipojo.everest.services.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Default request implementation.
 */
public class DefaultRequest implements Request {

    private final Action action;
    private final Path path;
    private final Map<String, Object> params;

    public DefaultRequest(Action action, Path path, Map<String, ? extends Object> params) {
        this.action = action;
        this.path = path;
        this.params = new HashMap<String, Object>();
        if (params != null) {
            this.params.putAll(params);
        }
    }

    public Path path() {
        return path;
    }

    public Action action() {
        return action;
    }

    public Map<String, ? extends Object> parameters() {
        return new HashMap<String, Object>(params);
    }

    public <T> T get(String key, Class<? extends T> clazz) {
        Object obj = params.get(key);

        if (obj == null) {
            return null;
        }

        if (clazz.isInstance(obj)) {
            return clazz.cast(obj);
        } else {
            throw new IllegalArgumentException("The parameter '" + key + "' is not a '" + clazz.getName() + "' (found" +
                    " type: '" + obj.getClass().getName() + "')");
        }
    }

    public static Request from(Relation relation, Map<String,
            Object> params) throws IllegalActionOnResourceException {
        // We create the request first, we will use it in error message
        DefaultRequest request = new DefaultRequest(relation.getAction(), relation.getHref(), params);

        // Check if the params match the relation params (identified by name)
        // The rules are:
        // * 1) All mandatory parameters must be set
        // * 2) Types must be compatible
        // * 3) Optional parameter can be ignored
        // * 4) If an optional parameter is set, types must be compatible
        // * 5) Additional parameter are allowed

        for (Parameter parameter : relation.getParameters()) {
            // Do we have it:
            boolean set = params.containsKey(parameter.name());
            if (! set) {
                // is the parameter mandatory ?
                if (! parameter.optional()) {
                    throw new IllegalActionOnResourceException(
                            request,
                            "The parameter " + parameter.name() + " of type " + parameter.type().toString() + " is " +
                                    "mandatory");
                }
                // Ok, it's an optional parameter (rule 3)
            } else {
                // The parameter is set, check the type.

                Object value = params.get(parameter.name());
                // Is it null ?
                if (value == null) {
                    // We refuse null value for non optional parameter.
                    if (! parameter.optional()) {
                        throw new IllegalActionOnResourceException(
                                request,
                                "The parameter " + parameter.name() + " of type " + parameter.type().toString() + " " +
                                        "is mandatory");
                    }
                    // Ok, null is accepted for optional parameter, we let the resource manage this sneaky case.
                } else {
                    // It's not null, we must check the type compatibility
                    if (! parameter.type().isInstance(value)) {
                        // Rule 2 violated.
                        throw new IllegalActionOnResourceException(
                                request,
                                "The parameter " + parameter.name() + " of type " + parameter.type().toString() + " " +
                                        "is incompatible with the given parameter " + value.getClass().toString());
                    }
                }
            }
        }

        return request;
    }
}
