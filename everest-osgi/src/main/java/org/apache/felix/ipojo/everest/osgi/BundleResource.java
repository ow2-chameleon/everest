package org.apache.felix.ipojo.everest.osgi;

import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.wiring.BundleRevision;

import java.util.ArrayList;
import java.util.List;

import static org.apache.felix.ipojo.everest.osgi.BundleResourceManager.BUNDLE_PATH;
import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.BundleNamespace.*;
import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.bundleStateToString;


/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/19/13
 * Time: 4:56 PM
 */
public class BundleResource extends DefaultResource {

    private final static String START_RELATION_NAME = "start";
    private final static String STOP_RELATION_NAME = "stop";
    private final static String UPDATE_RELATION_NAME = "start";

    private final Bundle m_bundle;

    private final boolean isFragment;

    public BundleResource(Bundle bundle) {
        super(BUNDLE_PATH.add(Path.from(Path.SEPARATOR + Long.toString(bundle.getBundleId()))));
        m_bundle = bundle;
        // Check if is fragment
        BundleRevision rev = m_bundle.adapt(BundleRevision.class);
        if (rev != null && (rev.getTypes() & BundleRevision.TYPE_FRAGMENT) != 0) {
            isFragment = true;
        } else {
            isFragment = false;
        }

        setRelations(
                //        new DefaultRelation(getPath(), Action.UPDATE, , "")
        );

    }

    public ResourceMetadata getSimpleMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        metadataBuilder.set(BUNDLE_STATE, bundleStateToString(m_bundle.getState()));
        metadataBuilder.set(Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE, m_bundle.getSymbolicName());
        metadataBuilder.set(Constants.BUNDLE_VERSION_ATTRIBUTE, m_bundle.getVersion());
        return metadataBuilder.build();
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder(getSimpleMetadata());
        metadataBuilder.set(BUNDLE_LOCATION, m_bundle.getLocation());
        metadataBuilder.set(BUNDLE_LAST_MODIFIED, m_bundle.getLastModified());
        metadataBuilder.set(BUNDLE_FRAGMENT, isFragment);
        //TODO find some properties to add here!
        return metadataBuilder.build();
    }

    @Override
    public List<Resource> getResources() {
        ArrayList<Resource> resources = new ArrayList<Resource>();
        resources.add(new BundleHeadersResource(getPath(), m_bundle));
        resources.add(new BundleWiresResource(getPath(), m_bundle));
        resources.add(new BundleServicesResource(getPath(), m_bundle));
        return resources;
    }

    @Override
    public Resource update(Request request) throws IllegalActionOnResourceException {
        return this;
    }


}
