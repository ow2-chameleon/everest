package org.apache.felix.ipojo.everest.osgi;

import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import java.util.ArrayList;
import java.util.List;

import static org.apache.felix.ipojo.everest.osgi.BundleResourceManager.BUNDLE_PATH;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/19/13
 * Time: 4:56 PM
 */
public class BundleResource extends DefaultResource {

    public static final String BUNDLE_STATE = "bundle-state";
    public static final String BUNDLE_LOCATION = "bundle-location";

    private final Bundle m_bundle;

    public BundleResource(Bundle bundle) {
        super(BUNDLE_PATH.add(Path.from(String.valueOf(bundle.getBundleId()))));
        m_bundle = bundle;
    }

    public ResourceMetadata getSimpleMetadata(){
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        metadataBuilder.set(BUNDLE_STATE,bundleStateToString(m_bundle.getState()));
        metadataBuilder.set(Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE, m_bundle.getSymbolicName());
        metadataBuilder.set(Constants.BUNDLE_VERSION_ATTRIBUTE,m_bundle.getVersion());
        return metadataBuilder.build();
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder(getSimpleMetadata());
        metadataBuilder.set(BUNDLE_LOCATION,m_bundle.getLocation());
        //
        return metadataBuilder.build();
    }

    @Override
    public List<Resource> getResources() {
        ArrayList<Resource> resources = new ArrayList<Resource>();
        resources.add(new BundleHeadersResource(getPath(),m_bundle));
        resources.add(new BundleWiresResource(getPath(),m_bundle));
        resources.add(new BundleServicesResource(getPath(), m_bundle));
        return resources;
    }

    @Override
    public Resource post(Request request) throws IllegalActionOnResourceException {
          return this;
    }

    public static String bundleStateToString(int bundleState){
        switch (bundleState){
            case Bundle.ACTIVE:
                return "ACTIVE";
            case Bundle.STARTING:
                return "STARTING";
            case Bundle.STOPPING:
                return "STOPPING";
            case Bundle.RESOLVED:
                return "RESOLVED";
            case Bundle.INSTALLED:
                return "INSTALLED";
            case Bundle.UNINSTALLED:
                return "UNINSTALLED";
            default:
                return "";
        }
    }



}
