package org.apache.felix.ipojo.everest.system;

import org.apache.felix.ipojo.everest.impl.DefaultParameter;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.*;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Map;

import static org.apache.felix.ipojo.everest.system.SystemRootResource.SYSTEM_ROOT_PATH;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 5/23/13
 * Time: 3:50 PM
 */
public class SystemPropertiesResource extends DefaultResource {

    private static final String SYSTEM_PROP_NAME = "properties";

    private ResourceMetadata metadata;

    public SystemPropertiesResource() {
        super(SYSTEM_ROOT_PATH.addElements(SYSTEM_PROP_NAME));
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        for (String key : System.getProperties().stringPropertyNames()) {
            metadataBuilder.set(key, System.getProperty(key));
        }
        metadata = metadataBuilder.build();

        setRelations(new DefaultRelation(getPath(), Action.UPDATE, "updateProperties",
                new DefaultParameter()
                        .name("properties")
                        .description("properties to update")
                        .type(Dictionary.class)
                        .optional(true),
                new DefaultParameter()
                        .name("configuration")
                        .type(Map.class)
                        .description("The set of configuration of the component")
                        .optional(true)));


    }


    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        for (String key : System.getProperties().stringPropertyNames()) {
            metadataBuilder.set(key, System.getProperty(key));
        }
        metadata = metadataBuilder.build();
        return metadata;
    }

    @Override
    public Resource update(Request request) throws IllegalActionOnResourceException {
        //  this.update(request.get("properties", Dictionary.class));
        Dictionary newDictionary = request.get("properties", Dictionary.class);
        if (newDictionary != null) {
            this.update(newDictionary);
        }


        //Map<String, String> newMap = request.get("configuration", Map.class);
        Map<String, ?> newMap = request.parameters();
        if (newMap != null) {
            for (String key : newMap.keySet()) {
                update(key, newMap.get(key).toString());
            }
        }
        return this;
    }

    public void update(Dictionary properties) throws IllegalActionOnResourceException {

        Enumeration keys = properties.keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            //TODO should check for exceptions
            System.setProperty(key.toString(), properties.get(key).toString());
        }
    }

    public void update(String key, String value) throws IllegalActionOnResourceException {
        //TODO should check for exceptions
        System.setProperty(key.toString(), value.toString());

    }
}
