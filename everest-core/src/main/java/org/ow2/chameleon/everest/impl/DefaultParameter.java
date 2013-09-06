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

import org.ow2.chameleon.everest.services.Parameter;

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
        this.description = name;
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

    // To be serializable, it must be a bean, so add getters and setters:


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof DefaultParameter)){

            return false;
        }
        Parameter oRef = (Parameter) obj;
        return ( (this.getName().equals(oRef.name())) && (this.type().equals(oRef.type())) && (this.description().equals(oRef.description())) &&( oRef.optional() == this.optional()) ) ;
    }

    @Override
    public int hashCode() {
        String string = this.getDescription() + getName() + getType() +optional();
        return string.hashCode();
    }
}
