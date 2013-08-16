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
