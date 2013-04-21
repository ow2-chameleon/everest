package org.apache.felix.ipojo.everest.osgi;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.impl.SymbolicLinkResource;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Resource;
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
public class ReadOnlyBundleSymlinksResource extends DefaultReadOnlyResource {

    private final Bundle[] m_uses;

    public ReadOnlyBundleSymlinksResource(Path path, Bundle[] uses) {
        super(path);
        m_uses = uses;
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        if (m_uses != null) {
            for (Bundle use : m_uses) {
                ImmutableResourceMetadata.Builder bundleMetadataBuilder = new ImmutableResourceMetadata.Builder();
                bundleMetadataBuilder.set(BUNDLE_STATE, bundleStateToString(use.getState()));
                bundleMetadataBuilder.set(Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE, use.getSymbolicName());
                bundleMetadataBuilder.set(Constants.BUNDLE_VERSION_ATTRIBUTE, use.getVersion());
                metadataBuilder.set(Long.toString(use.getBundleId()), bundleMetadataBuilder.build());
            }
        }
        return metadataBuilder.build();
    }

    @Override
    public List<Resource> getResources() {
        ArrayList<Resource> resources = new ArrayList<Resource>();
        if (m_uses != null) {
            for (Bundle use : m_uses) {
                Path usesPath = getPath().add(Path.from(Path.SEPARATOR + use.getBundleId()));
                Resource resource = BundleResourceManager.getInstance().getResource(BundleResourceManager.BUNDLE_PATH.add(Path.from(Path.SEPARATOR + use.getBundleId())).toString());
                if (resource != null) {
                    resources.add(new SymbolicLinkResource(usesPath, resource));
                }
            }
        }
        return resources;
    }
}
