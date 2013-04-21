package org.apache.felix.ipojo.everest.osgi;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.felix.ipojo.everest.osgi.PackageResource.PackageNamespace.PACKAGE_NAMESPACE;
import static org.apache.felix.ipojo.everest.osgi.PackageResource.PackageNamespace.PACKAGE_VERSION_ATTRIBUTE;
import static org.apache.felix.ipojo.everest.osgi.PackageResourceManager.PACKAGE_PATH;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/20/13
 * Time: 9:06 AM
 */
public class PackageResource extends DefaultReadOnlyResource {

    public static final String PACKAGE_ID_SEPARATOR = "-";

    private final BundleCapability m_bundleCapability;
    private final String m_packageName;
    private final Version m_version;
    private final Map<String, Object> m_attributes;
    private final Map<String, String> m_directives;

    public static String uniquePackageId(long bundleId, String packageName, Version version){
        StringBuilder sb = new StringBuilder();
        sb.append(bundleId);
        sb.append(PACKAGE_ID_SEPARATOR);
        sb.append(packageName);
        sb.append(PACKAGE_ID_SEPARATOR);
        sb.append(version);
        return sb.toString();
    }

    public PackageResource(BundleCapability bundleCapability){
        super(PACKAGE_PATH.add(Path.from(uniquePackageId(bundleCapability.getRevision().getBundle().getBundleId(),
                (String) bundleCapability.getAttributes().get(PACKAGE_NAMESPACE),
                (Version) bundleCapability.getAttributes().get(PACKAGE_VERSION_ATTRIBUTE)))));
        m_bundleCapability = bundleCapability;
        m_attributes = bundleCapability.getAttributes();
        m_directives = bundleCapability.getDirectives();
        m_packageName = (String) m_attributes.get(PACKAGE_NAMESPACE);
        m_version = (Version) m_attributes.get(PACKAGE_VERSION_ATTRIBUTE);
    }

    public String getUniquePackageId(){
        return uniquePackageId(m_bundleCapability.getRevision().getBundle().getBundleId(), m_packageName, m_version);
    }

    public Bundle getBundle(){
        return m_bundleCapability.getRevision().getBundle();
    }

    public ResourceMetadata getSimpleMetadata(){
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();

        return metadataBuilder.build();
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();

        return metadataBuilder.build();
    }

    @Override
    public List<Resource> getResources() {
        ArrayList<Resource> resources = new ArrayList<Resource>();
        // provider bundle
        Bundle m_bundle = m_bundleCapability.getRevision().getBundle();
        //TODO create a link to m_bundle

        // importers of this package
        BundleWiring wiring = m_bundleCapability.getRevision().getBundle().adapt(BundleWiring.class);
        List<BundleWire> wires = wiring.getProvidedWires(PACKAGE_NAMESPACE);
        for(BundleWire wire : wires){
            if(wire.getCapability().equals(m_bundleCapability)){
                Bundle requirerBundle = wire.getRequirerWiring().getBundle();
                //TODO create link to requirerBundle
            }
        }
        return resources;
    }

    public class PackageNamespace {

        public static final String PACKAGE_NAMESPACE = "osgi.wiring.package";

        public static final String PACKAGE_VERSION_ATTRIBUTE = "version";

        public static final String CAPABILITY_BUNDLE_SYMBOLICNAME_ATTRIBUTE = "bundle-symbolic-name";

        public static final String CAPABILITY_EXCLUDE_DIRECTIVE = "exclude";

        public static final String CAPABILITY_INCLUDE_DIRECTIVE = "include";

        public static final String RESOLUTION_DYNAMIC = "dynamic";
    }
}
