package org.apache.felix.ipojo.everest.services;

import java.util.Map;

/**
 * Resource metadata represents the state of the resource.
 */
public interface ResourceMetadata extends Map<String, Object> {

    public <T> T get(String key, Class<? extends T> clazz);

}
