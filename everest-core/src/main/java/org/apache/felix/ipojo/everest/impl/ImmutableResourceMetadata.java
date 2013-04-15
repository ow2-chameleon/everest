package org.apache.felix.ipojo.everest.impl;

import org.apache.felix.ipojo.everest.services.ResourceMetadata;

import java.util.HashMap;
import java.util.Map;

/**
 * We recommend resource metadata to be immutable.
 * Here is a basic implementation.
 */
public class ImmutableResourceMetadata  extends HashMap<String, Object> implements ResourceMetadata {

    public static ImmutableResourceMetadata of(ResourceMetadata metadata) {
        return of((Map<String, Object>) metadata);
    }

    public static ImmutableResourceMetadata of(Map<String , ?> metadata) {
        ImmutableResourceMetadata irm = new ImmutableResourceMetadata();
        for (Map.Entry<String, ?> entry : metadata.entrySet()) {
            irm._put(entry.getKey(), entry.getValue());
        }
        return irm;
    }

    /**
     * A private delegate on the super put method.
     * @param key the key
     * @param value the value
     */
    private void _put(String key, Object value) {
        super.put(key, value);
    }

    public <T> T get(String key, Class<? extends T> clazz) {
        Object obj = get(key);

        if (obj == null) {
            return null;
        }

        if (clazz.isInstance(obj)) {
            return clazz.cast(obj);
        } else {
            throw new IllegalArgumentException("The metadata '" + key + "' is not a '" + clazz.getName() + "' (found " +
                    "type: '" + obj.getClass().getName() + "')");
        }

    }

    /**
     * Resource metadata are immutable, not supported.
     */
    public Object put(String key, Object value) {
        throw new UnsupportedOperationException("Immutable metadata");
    }

    /**
     * Resource metadata are immutable, not supported.
     */
    public Object remove(Object key) {
        throw new UnsupportedOperationException("Immutable metadata");
    }

    /**
     * Resource metadata are immutable, not supported.
     */
    public void putAll(Map m) {
        throw new UnsupportedOperationException("Immutable metadata");
    }

    /**
     * Resource metadata are immutable, not supported.
     */
    public void clear() {
        throw new UnsupportedOperationException("Immutable metadata");
    }

    public static class Builder {

        Map<String, Object> accumulator = new HashMap<String, Object>();

        public Builder() {}

        public Builder set(String k, Object v) {
            accumulator.put(k ,v);
            return this;
        }

        public ImmutableResourceMetadata build() {
            return of(accumulator);
        }

    }
}
