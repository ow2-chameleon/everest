package org.apache.felix.ipojo.everest.impl;

import org.apache.felix.ipojo.everest.services.Action;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Relation;
import org.apache.felix.ipojo.everest.services.Resource;

/**
 * Default implementation of relations.
 */
public class DefaultRelation implements Relation {

    private final Path path;
    private final Action action;
    private final String name;
    private final String description;

    public DefaultRelation(Path path, Action action, String name, String description) {
        this.path = path;
        this.action = action;
        this.name = name;
        this.description = description;
    }

    public DefaultRelation(Path path, Action action, String name) {
        this(path, action, name, null);
    }

    public DefaultRelation(Resource resource, Action action, String name) {
        this(resource.getCanonicalPath(), action, name, null);
    }

    public Path getPath() {
        return path;
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
}
