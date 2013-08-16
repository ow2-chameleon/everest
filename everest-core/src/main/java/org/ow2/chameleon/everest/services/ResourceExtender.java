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

/**
 * Resource extender service specification.
 * Resource extender <i>extends</i> the resources returned by some requests.
 */
public interface ResourceExtender {

    /**
     * The resource filter used to detects whether this extender is going to extend the resource returned by a request.
     * The returned filter is called with the returned resource.
     * @return the resource filter.
     */
    ResourceFilter getFilter();

    /**
     * Extends the given resources returned by the given request.
     * @param request the request
     * @param resource the resource
     * @return the extended resource.
     */
    Resource extend(Request request, Resource resource);


}
