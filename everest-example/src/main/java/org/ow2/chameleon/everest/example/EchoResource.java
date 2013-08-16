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

package org.ow2.chameleon.everest.example;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.ow2.chameleon.everest.impl.DefaultResource;
import org.ow2.chameleon.everest.impl.ImmutableResourceMetadata;
import org.ow2.chameleon.everest.services.*;

/**
 * '/echo' resource.
 */
@Component
@Instantiate
@Provides(specifications = Resource.class)
public class EchoResource extends DefaultResource {

    public static final Path PATH = Path.from("/echo");

    public EchoResource() {
        super(PATH);
    }

    @Override
    public Resource process(Request request) {
        ResourceMetadata metadata = new ImmutableResourceMetadata.Builder()
                .set("action", request.action())
                .set("path", request.path())
                .set("parameters", request.parameters())
                .build();
        Resource resource = null;
        try {
            resource = new Builder()
                    .fromPath(PATH)
                    .with(metadata)
                    .build();
        } catch (IllegalResourceException e) {
            // Should never happen!
            throw new AssertionError(e);
        }
        return resource;
    }

}
