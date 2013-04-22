package org.apache.felix.ipojo.everest.osgi;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.impl.SymbolicLinkResource;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleWire;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.getChild;
import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.metadataFrom;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/22/13
 * Time: 12:52 PM
 */
public class BundleRequirementResource extends DefaultReadOnlyResource {

    public static final String REQUIRED_WIRES_PATH = "required-wires";

    private static final String REQUIRER_PACKAGE = "requirer-package-import";

    private static final String REQUIRER_BUNDLE = "requirer-bundle-require";

    private final Set<BundleWire> m_wires;
    private final BundleRequirement m_requirement;
    private final boolean isPackage;
    private final boolean isBundle;

    public BundleRequirementResource(Path path, BundleRequirement requirement, Set<BundleWire> wireSet) {
        super(path);
        m_requirement = requirement;
        m_wires = wireSet;
        isPackage = m_requirement.getNamespace().equals(OsgiResourceUtils.PackageNamespace.PACKAGE_NAMESPACE) ? true : false;
        isBundle = m_requirement.getNamespace().equals(OsgiResourceUtils.BundleNamespace.BUNDLE_NAMESPACE) ? true : false;
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        metadataFrom(metadataBuilder, m_requirement);
        return metadataBuilder.build();
    }

    @Override
    public List<Resource> getResources() {
        ArrayList<Resource> resources = new ArrayList<Resource>();
        String requirementId = OsgiResourceUtils.uniqueRequirementId(m_requirement);
        if (isPackage) {
            // add link to package import
            Resource headersRes = getChild(BundleResourceManager.getInstance(), BundleHeadersResource.HEADERS_PATH);
            // it is whether a normal import or a dynamicimport
            Resource importsRes = getChild(headersRes, BundleHeadersResource.IMPORT_PACKAGE);
            Resource dynamicImportsRes = getChild(headersRes, BundleHeadersResource.DYNAMIC_IMPORT_PACKAGE);
            // test if it is a normal import
            Resource requirementRes = getChild(importsRes, requirementId);
            if (requirementRes != null) {
                // maybe it is a dynamic import?
                requirementRes = getChild(dynamicImportsRes, requirementId);
            }
            if (requirementRes != null) {
                resources.add(new SymbolicLinkResource(getPath().add(Path.from(Path.SEPARATOR + REQUIRER_PACKAGE)), requirementRes));
            }
        }
        if (isBundle) {
            // add link to require-bundle header
            Resource requirerBundleRes = getChild(BundleResourceManager.getInstance(), Long.toString(m_requirement.getRevision().getBundle().getBundleId()));
            Resource requireBundlesRes = getChild(requirerBundleRes, BundleHeadersResource.REQUIRE_BUNDLE);
            if (requireBundlesRes != null) {
                Resource requireBundle = getChild(requireBundlesRes, requirementId);
                resources.add(new SymbolicLinkResource(getPath().add(Path.from(Path.SEPARATOR + REQUIRER_BUNDLE)), requireBundle));
            }
        }
        for (BundleWire wire : m_wires) {
            resources.add(new RequiredWireResource(getPath().add(Path.from(Path.SEPARATOR + REQUIRED_WIRES_PATH)), wire));
        }
        return resources;
    }
}
