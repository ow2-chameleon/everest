package org.apache.felix.ipojo.everest.impl;

import org.apache.felix.ipojo.everest.services.Action;
import org.apache.felix.ipojo.everest.services.Relation;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to handle collection of relations.
 */
public class Relations {


    public static class Builder {

        List<Relation> relations = new ArrayList<Relation>();

        public Builder addRelation(String path, Action action, String name, String description) {
            relations.add(new DefaultRelation(path, action, name, description));
            return this;
        }

        public Builder addRelation(String path, Action action, String name) {
            relations.add(new DefaultRelation(path, action, name));
            return this;
        }

        public List<Relation> build() {
            return relations;
        }

    }

}
