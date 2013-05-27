package org.apache.felix.ipojo.everest.osgi.system;

import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;

import java.util.Map;

import static org.apache.felix.ipojo.everest.osgi.system.SystemResourceManager.SYSTEM_ROOT_PATH;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 5/23/13
 * Time: 3:46 PM
 */
public class EnvironmentPropertiesResource extends DefaultResource {

    public static final String ENV_NAME = "env";
    private ResourceMetadata metadata;


    public EnvironmentPropertiesResource() {
        super(SYSTEM_ROOT_PATH.addElements(ENV_NAME));

        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
            metadataBuilder.set(entry.getKey(), entry.getValue());
        }
        metadata = metadataBuilder.build();
    }


    @Override
    public ResourceMetadata getMetadata() {
        return metadata;
    }
}
