package org.apache.felix.ipojo.everest.osgi.bundle;

import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.osgi.AbstractResourceCollection;
import org.apache.felix.ipojo.everest.osgi.ReadOnlyLeafCollectionResource;
import org.apache.felix.ipojo.everest.osgi.packages.PackageResourceManager;
import org.apache.felix.ipojo.everest.services.IllegalResourceException;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;

import java.util.*;

import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.PackageNamespace.REQUIREMENT_RESOLUTION_DIRECTIVE;
import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.PackageNamespace.RESOLUTION_DYNAMIC;
import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.metadataFrom;
import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.uniqueRequirementId;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/21/13
 * Time: 3:51 PM
 */
public class BundleHeadersResource extends AbstractResourceCollection {

    public static final String HEADERS_PATH = "headers";

    public static final String EXPORT_PACKAGE = "export-package";

    public static final String IMPORT_PACKAGE = "import-package";

    public static final String DYNAMIC_IMPORT_PACKAGE = "dynamicimport-package";

    public static final String REQUIRE_BUNDLE = "require-bundle";

    private final Bundle m_bundle;

    private final ImmutableResourceMetadata metadata;

    public BundleHeadersResource(Path path, Bundle bundle) {
        super(path.addElements(HEADERS_PATH));
        m_bundle = bundle;
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        Dictionary<String, String> headers = m_bundle.getHeaders();
        //
        Enumeration<String> keys = headers.keys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            //omitting those
            if (!(key.equals(Constants.EXPORT_PACKAGE) ||
                    key.equals(Constants.IMPORT_PACKAGE) ||
                    key.equals(Constants.DYNAMICIMPORT_PACKAGE) ||
                    key.equals(Constants.REQUIRE_BUNDLE) ||
                    key.equals(Constants.BUNDLE_NATIVECODE))) {
                metadataBuilder.set(key, headers.get(key));
            }
        }
        metadata = metadataBuilder.build();
    }

    @Override
    public ResourceMetadata getMetadata() {
        return metadata;
    }

    @Override
    public List<Resource> getResources() {
        ArrayList<Resource> resources = new ArrayList<Resource>();
        BundleRevision rev = m_bundle.adapt(BundleRevision.class);
        // package export
        List<BundleCapability> capabilities = rev.getDeclaredCapabilities(BundleRevision.PACKAGE_NAMESPACE);
        Builder builder = PackageResourceManager.relationsBuilder(getPath().addElements(EXPORT_PACKAGE), capabilities);
        try {
            resources.add(builder.build());
        } catch (IllegalResourceException e) {
            // should never happen
        }

        // package import / dynamic import
        List<BundleRequirement> requirements = rev.getDeclaredRequirements(BundleRevision.PACKAGE_NAMESPACE);
        Map<String, ResourceMetadata> imports = new HashMap<String, ResourceMetadata>();
        Map<String, ResourceMetadata> dynamicImports = new HashMap<String, ResourceMetadata>();
        for (BundleRequirement req : requirements) {

            ResourceMetadata metadata = metadataFrom(new ImmutableResourceMetadata.Builder(), req).build();
            String reqId = uniqueRequirementId(req);
            if (RESOLUTION_DYNAMIC.equals(req.getDirectives().get(REQUIREMENT_RESOLUTION_DIRECTIVE))) {
                dynamicImports.put(reqId, metadata);
            } else {
                imports.put(reqId, metadata);
            }
        }
        // Dynamic import package leaf resource collection
        resources.add(new ReadOnlyLeafCollectionResource(getPath().addElements(DYNAMIC_IMPORT_PACKAGE), dynamicImports));
        // Import package leaf resource collection
        resources.add(new ReadOnlyLeafCollectionResource(getPath().addElements(IMPORT_PACKAGE), imports));

        // Bundle requirements
        List<BundleRequirement> bundleRequirements = rev.getDeclaredRequirements(BundleRevision.BUNDLE_NAMESPACE);
        Map<String, ResourceMetadata> bundleRequires = new HashMap<String, ResourceMetadata>();
        for (BundleRequirement req : bundleRequirements) {
            bundleRequires.put(uniqueRequirementId(req), metadataFrom(new ImmutableResourceMetadata.Builder(), req).build());
        }
        resources.add(new ReadOnlyLeafCollectionResource(getPath().addElements(REQUIRE_BUNDLE), bundleRequires));

        // TODO Native-Code

        return resources;
    }
}
