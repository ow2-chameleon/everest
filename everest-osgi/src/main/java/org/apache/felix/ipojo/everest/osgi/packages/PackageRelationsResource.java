package org.apache.felix.ipojo.everest.osgi.packages;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.Action;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Relation;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleCapability;

import java.util.ArrayList;
import java.util.List;

import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.PackageNamespace.*;
import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.uniqueCapabilityId;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/21/13
 * Time: 9:46 PM
 */
public class PackageRelationsResource extends DefaultReadOnlyResource {

    BundleCapability[] m_capabilities;

    public PackageRelationsResource(Path path, BundleCapability[] capabilities) {
        super(path);
        m_capabilities = capabilities;
        if (m_capabilities != null) {
            List<Relation> relations = new ArrayList<Relation>();
            for (BundleCapability capability : m_capabilities) {
                // calculate unique package Id
                String packageId = uniqueCapabilityId(capability);
                Path packagePath = PackageResourceManager.getInstance().getPath().addElements(packageId);
                relations.add(new DefaultRelation(packagePath, Action.READ, packageId));
            }
            setRelations(relations);
        }
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        if (m_capabilities != null) {
            for (BundleCapability capability : m_capabilities) {
                ImmutableResourceMetadata.Builder packageMetadataBuilder = new ImmutableResourceMetadata.Builder();
                // calculate unique package Id
                String packageName = (String) capability.getAttributes().get(PACKAGE_NAMESPACE);
                Version version = (Version) capability.getAttributes().get(PACKAGE_VERSION_ATTRIBUTE);
                String packageId = uniqueCapabilityId(capability);
                packageMetadataBuilder.set(PACKAGE_NAMESPACE, packageName);
                packageMetadataBuilder.set(PACKAGE_VERSION_ATTRIBUTE, version);
                packageMetadataBuilder.set(CAPABILITY_BUNDLE_SYMBOLICNAME_ATTRIBUTE, capability.getRevision().getBundle().getSymbolicName());
                packageMetadataBuilder.set(CAPABILITY_BUNDLE_VERSION_ATTRIBUTE, capability.getRevision().getBundle().getVersion());
                metadataBuilder.set(packageId, packageMetadataBuilder.build());
            }
        }
        return metadataBuilder.build();
    }

}
