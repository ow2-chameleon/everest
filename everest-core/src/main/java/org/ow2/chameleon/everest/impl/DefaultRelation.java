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

import org.ow2.chameleon.everest.services.*;

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

    @Override
    public boolean equals(Object obj){
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof DefaultRelation)){

            return false;
        }
        Relation oRef = (Relation) obj;
        return ( (oRef.getName().equals(this.getName())) && (oRef.getDescription().equals(this.getDescription())) && (oRef.getAction() == this.getAction()) && (oRef.getParameters().equals(this.getParameters())) && (oRef.getHref()==this.getHref())  ) ;
    }

    @Override
    public int hashCode() {
        String string = this.getDescription()+this.getAction().toString()+this.getName()+this.getHref().toString()+this.getParameters().toString();
        return string.hashCode();
    }
}
