package org.apache.felix.ipojo.everest.impl;

import org.apache.felix.ipojo.everest.services.Action;
import org.apache.felix.ipojo.everest.services.Request;

import java.util.HashMap;
import java.util.Map;

/**
 * Default request implementation.
 */
public class DefaultRequest implements Request {

    private final Action action;
    private final String path;
    private final Map<String, Object> params;

    public static DefaultRequest createNormalizedRequest(Request request, String newPath) {
        return new DefaultRequest(request.action(), newPath, request.parameters());
    }

    public DefaultRequest(Action action, String path, Map<String, ? extends Object> params) {
        this.action = action;
        this.path = path;
        this.params = new HashMap<String, Object>();
        if (params != null) {
            this.params.putAll(params);
        }
    }

    public String path() {
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
}
