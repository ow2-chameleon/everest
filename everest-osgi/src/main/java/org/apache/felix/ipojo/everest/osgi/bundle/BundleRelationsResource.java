package org.apache.felix.ipojo.everest.osgi.bundle;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.Action;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Relation;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import java.util.ArrayList;
import java.util.List;

import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.BundleNamespace.BUNDLE_STATE;
import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.bundleStateToString;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/21/13
 * Time: 7:01 PM
 */
public class BundleRelationsResource extends DefaultReadOnlyResource {

    private final Bundle[] m_bundles;

    public BundleRelationsResource(Path path, Bundle[] bundles) {
        super(path);
        m_bundles = bundles;
        if (m_bundles != null) {
            List<Relation> relations = new ArrayList<Relation>();
            for (Bundle bundle : m_bundles) {
                Path bundlePath = BundleResourceManager.getInstance().getPath().add(Path.from(Path.SEPARATOR + bundle.getBundleId()));
                relations.add(new DefaultRelation(bundlePath, Action.READ, Long.toString(bundle.getBundleId())));
            }
            setRelations(relations);
        }
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        if (m_bundles != null) {
            for (Bundle bundle : m_bundles) {
                ImmutableResourceMetadata.Builder bundleMetadataBuilder = new ImmutableResourceMetadata.Builder();
                bundleMetadataBuilder.set(BUNDLE_STATE, bundleStateToString(bundle.getState()));
                bundleMetadataBuilder.set(Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE, bundle.getSymbolicName());
                bundleMetadataBuilder.set(Constants.BUNDLE_VERSION_ATTRIBUTE, bundle.getVersion());
                metadataBuilder.set(Long.toString(bundle.getBundleId()), bundleMetadataBuilder.build());
            }
        }
        return metadataBuilder.build();
    }
}
