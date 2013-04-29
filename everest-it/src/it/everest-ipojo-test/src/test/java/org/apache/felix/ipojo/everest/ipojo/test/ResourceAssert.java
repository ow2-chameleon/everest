package org.apache.felix.ipojo.everest.ipojo.test;

import org.apache.felix.ipojo.everest.services.*;
import org.fest.assertions.GenericAssert;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Fluent assertions for everest resources.
 */
public class ResourceAssert extends GenericAssert<ResourceAssert, Resource> {

    protected ResourceAssert(Resource actual) {
        super(ResourceAssert.class, actual);
    }

    public ResourceAssert hasPath(Path path) {
        assertThat((Object) actual.getPath()).isEqualTo(path);
        return this;
    }

    public ResourceAssert hasPath(String path) {
        return hasPath(Path.from(path));
    }

    public ResourceAssert hasCanonicalPath(Path path) {
        assertThat((Object) actual.getCanonicalPath()).isEqualTo(path);
        return this;
    }

    public ResourceAssert hasCanonicalPath(String path) {
        return hasCanonicalPath(Path.from(path));
    }

    public ResourceAssert hasResource(Resource resource) {
        assertThat(actual.getResources()).contains(resource);
        return this;
    }

    public ResourceAssert hasResource(Path resourcePath) {
        assertThat(actual.getResource(resourcePath.toString())).isNotNull();
        return this;
    }

    public ResourceAssert hasRelation(RelationFilter filter) {
        List<Relation> relations = new ArrayList<Relation>();

        for (Relation rel : actual.getRelations()) {
            if (filter.accept(rel)) {
                relations.add(rel);
            }
        }
        assertThat(relations).isNotEmpty();
        return this;
    }

    public static ResourceAssert assertThatResource(Resource r) {
        return new ResourceAssert(r);
    }

}
