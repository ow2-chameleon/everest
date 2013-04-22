package org.apache.felix.ipojo.everest.impl;

import org.apache.felix.ipojo.everest.services.*;

import java.util.*;

/**
 * Default implementation of relations.
 */
public class DefaultRelation implements Relation {

    private final Path href;
    private final Action action;
    private final String name;
    private final String description;
    private final List<Parameter> params;

    public DefaultRelation(Path href, Action action, String name, String description, Parameter... params) {
        this.href = href;
        this.action = action;
        this.name = name;
        this.description = description;
        if (params != null) {
            this.params = Arrays.asList(params);
        } else {
            this.params = Collections.emptyList();
        }
    }

    public DefaultRelation(Path href, Action action, String name) {
        this(href, action, name, null, new Parameter[]{});
    }

    public DefaultRelation(Path href, Action action, String name, Parameter... params) {
        this(href, action, name, null, params);
    }

    public DefaultRelation(Resource resource, Action action, String name) {
        this(resource.getCanonicalPath(), action, name);
    }

    public DefaultRelation(Resource resource, Action action, String name, Parameter... params) {
        this(resource.getCanonicalPath(), action, name, null, params);
    }

    public Path getHref() {
        return href;
    }

    public Action getAction() {
        return action;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Parameter> getParameters() {
        return params;
    }
}
