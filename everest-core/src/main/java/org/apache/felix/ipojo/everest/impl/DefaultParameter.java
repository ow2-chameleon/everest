package org.apache.felix.ipojo.everest.impl;

import org.apache.felix.ipojo.everest.services.Parameter;

/**
 * Default implementation of parameters.
 */
public class DefaultParameter implements Parameter {

    String name;
    Class<?> type;
    String description;
    boolean optional;

    public String name() {
        return name;
    }

    public Class type() {
        return type;
    }

    public String description() {
        return description;
    }

    public boolean optional() {
        return optional;
    }

    public DefaultParameter name(String name) {
        this.name = name;
        return this;
    }

    public DefaultParameter description(String name) {
        this.description = description;
        return this;
    }

    public DefaultParameter optional(boolean opt) {
        this.optional = opt;
        return this;
    }

    public DefaultParameter type(Class<?> clazz) {
        this.type = clazz;
        return this;
    }

    public DefaultParameter type(String type) throws ClassNotFoundException {
        try {
            this.type = this.getClass().getClassLoader().loadClass(type);
        } catch (ClassNotFoundException e) {
            // Try the TCCL
            ClassLoader tccl = Thread.currentThread().getContextClassLoader();
            if (tccl != null) {
                this.type = tccl.loadClass(type);
            } else {
                throw e;
            }
        }
        return this;
    }
}
