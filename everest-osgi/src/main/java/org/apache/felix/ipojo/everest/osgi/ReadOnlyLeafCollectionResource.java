package org.apache.felix.ipojo.everest.osgi;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.IllegalResourceException;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/21/13
 * Time: 10:34 PM
 */
public class ReadOnlyLeafCollectionResource extends DefaultReadOnlyResource {

    private final Map<String, ResourceMetadata> m_leafMetadata;

    public ReadOnlyLeafCollectionResource(Path path, Map<String, ResourceMetadata> leafMetadata) {
        super(path);
        m_leafMetadata = leafMetadata;
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        for (String key : m_leafMetadata.keySet()) {
            metadataBuilder.set(key, null);
        }
        return metadataBuilder.build();
    }

    @Override
    public List<Resource> getResources() {
        ArrayList<Resource> resources = new ArrayList<Resource>();
        for (Entry<String, ResourceMetadata> leaf : m_leafMetadata.entrySet()) {
            Path leafPath = getPath().add(Path.from(Path.SEPARATOR + leaf.getKey()));
            Resource resource;
            try {
                resource = new Builder().fromPath(leafPath).with(leaf.getValue()).build();
            } catch (IllegalResourceException e) {
                throw new RuntimeException(e.getMessage());
            }
            if (resource != null) {
                resources.add(resource);
            }
        }
        return resources;
    }

}
