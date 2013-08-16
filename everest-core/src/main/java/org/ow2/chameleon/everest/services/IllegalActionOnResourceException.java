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
 * Exception thrown when a request is illegal for the targeted resource.
 */
public class IllegalActionOnResourceException extends Exception {

    private final Request request;

    private final Resource resource;

    public IllegalActionOnResourceException(Request request, Resource resource) {
        this.request = request;
        this.resource = resource;
    }

    public IllegalActionOnResourceException(Request request, Resource resource, String message) {
        this.getMessage();
        this.request = request;
        this.resource = resource;
    }

    public IllegalActionOnResourceException(Request request, String message) {
        super(message);
        this.resource = null;
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }

    public Resource getResource() {
        return resource;
    }
}
