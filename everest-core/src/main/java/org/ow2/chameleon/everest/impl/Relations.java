/*
 * Copyright 2013 OW2 Chameleon
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ow2.chameleon.everest.impl;

import org.ow2.chameleon.everest.services.Action;
import org.ow2.chameleon.everest.services.Path;
import org.ow2.chameleon.everest.services.Relation;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to handle collection of relations.
 */
public class Relations {


    public static class Builder {

        List<Relation> relations = new ArrayList<Relation>();

        public Builder addRelation(Path href, Action action, String name, String description) {
            relations.add(new DefaultRelation(href, action, name, description));
            return this;
        }

        public Builder addRelation(Path href, Action action, String name) {
            relations.add(new DefaultRelation(href, action, name));
            return this;
        }

        public List<Relation> build() {
            return relations;
        }

    }

}
