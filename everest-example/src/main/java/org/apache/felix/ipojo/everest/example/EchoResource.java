package org.apache.felix.ipojo.everest.example;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.*;

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
