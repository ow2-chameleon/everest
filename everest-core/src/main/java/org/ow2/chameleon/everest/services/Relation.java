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

package org.ow2.chameleon.everest.services;

import java.util.List;

/**
 * Relations are implementing 'Hypermedia as the Engine of Application State'.
 * Each resource gives a list of links.
 *
 * From a relation a request can be emitted.
 */
public interface Relation {

    /**
     * @return The path of the resource that will process the request.
     */
    Path getHref();

    /**
     * @return The action of the request to emit
     */
    Action getAction();

    /**
     * @return The relation name
     */
    String getName();

    /**
     * @return A description of the relation
     */
    String getDescription();

    /**
     * @return the formal parameters of the relation
     */
    List<Parameter> getParameters();

}
