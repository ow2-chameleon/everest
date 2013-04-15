package org.apache.felix.ipojo.everest.impl;

import org.apache.felix.ipojo.everest.services.Relation;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Default resource implementation
 */
public class DefaultResource implements Resource {

    private final String path;
    private final Resource[] resources;
    private final ResourceMetadata metadata;

    private Relation[] relations;

    public DefaultResource(String path) {
        this(path, null);
    }

    public DefaultResource(String path, ResourceMetadata metadata, Resource... resources) {
        this.path = path;
        this.resources = resources;
        this.metadata = metadata;
    }

    public DefaultResource setRelations(Relation... relations) {
        this.relations = relations;
        return this;
    }

    public DefaultResource setRelations(List<Relation> relations) {
        this.relations = relations.toArray(new Relation[relations.size()]);
        return this;
    }

    public String getPath() {
        return path;
    }

    public List<Resource> getResources() {
        if (resources == null) {
            return Collections.emptyList();
        }
        return new ArrayList<Resource>(Arrays.asList(resources));
    }

    public ResourceMetadata getMetadata() {
        return ImmutableResourceMetadata.of(metadata);
    }

    public List<Relation> getRelations() {
        if (relations == null) {
            return Collections.emptyList();
        }
        return new ArrayList<Relation>(Arrays.asList(relations));
    }

    public static class Builder {

        private String path;
        private ResourceMetadata metadata;
        private List<Relation> relations;
        private List<Resource> resources;

        Builder() { }

        Builder(String path) {
            fromPath(path);
        }

        public Builder fromPath(String path) {
            this.path = path;
            return this;
        }

        public Builder with(ResourceMetadata resourceMetadata) {
            this.metadata = resourceMetadata;
            return this;
        }

        public Builder with(Resource resource) {
            if (this.resources == null) { this.resources = new ArrayList<Resource>(); }
            resources.add(resource);
            return this;
        }

        public Builder with(Relation relation) {
            if (this.relations == null) { this.relations = new ArrayList<Relation>(); }
            relations.add(relation);
            return this;
        }

        public Resource build() {
            Resource[] sub = null;
            if (resources != null) {
                sub = resources.toArray(sub);
            }
            DefaultResource res = new DefaultResource(path, metadata, sub);
            res.setRelations(relations);
            return res;
        }
    }
}
