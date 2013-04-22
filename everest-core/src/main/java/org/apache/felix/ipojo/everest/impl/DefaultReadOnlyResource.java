package org.apache.felix.ipojo.everest.impl;

import org.apache.felix.ipojo.everest.filters.Filters;
import org.apache.felix.ipojo.everest.services.*;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Read only resource.
 * This resource rejects CREATE, UPDATE and DELETE actions
 */
public class DefaultReadOnlyResource extends DefaultResource {

    public DefaultReadOnlyResource(Path path) {
        super(path);
    }

    public DefaultReadOnlyResource(String path) {
        super(path);
    }

    public DefaultReadOnlyResource(Resource parent, String name) {
        super(parent, name);
    }

    public DefaultReadOnlyResource(Path path, ResourceMetadata metadata, Resource... resources) {
        super(path, metadata, resources);
    }

    @Override
    public Resource delete(Request request) throws IllegalActionOnResourceException {
        throw new IllegalActionOnResourceException(request, this);
    }

    @Override
    public Resource create(Request request) throws IllegalActionOnResourceException {
        throw new IllegalActionOnResourceException(request, this);
    }

    @Override
    public Resource update(Request request) throws IllegalActionOnResourceException {
        throw new IllegalActionOnResourceException(request, this);
    }
}
