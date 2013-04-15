package org.apache.felix.ipojo.everest.services;

import java.util.Map;

/**
 * Represents a request.
 */
public interface Request {

    /**
     * @return the request path.
     */
    public String path();

    /**
     * @return the request action.
     */
    public Action action();

    /**
     * @return the request parameters.
     */
    public Map<String, ? extends Object> parameters();

    /**
     * Retrieves the parameter named `key` of type `clazz`.
     * @param key the key
     * @param clazz the class
     * @return the parameter values, {@literal null} if the parameter does not exist
     */
    public <T> T get(String key, Class<? extends T> clazz);

}
