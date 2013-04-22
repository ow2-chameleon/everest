package org.apache.felix.ipojo.everest.osgi;

import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Resource;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;

import java.util.Map;

import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.PackageNamespace.PACKAGE_ID_SEPARATOR;
import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.PackageNamespace.PACKAGE_VERSION_ATTRIBUTE;


/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/21/13
 * Time: 7:42 PM
 */
public class OsgiResourceUtils {

    public static String bundleStateToString(int bundleState) {
        switch (bundleState) {
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

    public static String uniqueCapabilityId(BundleCapability bundleCapability) {
        long bundleId = bundleCapability.getRevision().getBundle().getBundleId();
        String packageName = bundleCapability.getAttributes().get(bundleCapability.getNamespace()).toString();
        Version version = (Version) bundleCapability.getAttributes().get(PACKAGE_VERSION_ATTRIBUTE); // crossing fingers
        StringBuilder sb = new StringBuilder();
        sb.append(bundleId);
        sb.append(PACKAGE_ID_SEPARATOR);
        sb.append(packageName);
        sb.append(PACKAGE_ID_SEPARATOR);
        sb.append(version);
        return sb.toString();
    }

    public static String uniqueRequirementId(BundleRequirement bundleRequirement) {
        long bundleId = bundleRequirement.getRevision().getBundle().getBundleId();
        String packageName = (String) bundleRequirement.getAttributes().get(bundleRequirement.getNamespace());
        StringBuilder sb = new StringBuilder();
        sb.append(bundleId);
        sb.append(PACKAGE_ID_SEPARATOR);
        sb.append(packageName);
        return sb.toString();
    }

    public static ImmutableResourceMetadata.Builder metadataFrom(ImmutableResourceMetadata.Builder metadataBuilder, BundleRequirement bundleRequirement) {
        for (Map.Entry<String, Object> att : bundleRequirement.getAttributes().entrySet()) {
            metadataBuilder.set(att.getKey(), att.getValue());
        }
        for (Map.Entry<String, String> dir : bundleRequirement.getDirectives().entrySet()) {
            metadataBuilder.set(dir.getKey(), dir.getValue());
        }
        return metadataBuilder;
    }

    public static ImmutableResourceMetadata.Builder metadataFrom(ImmutableResourceMetadata.Builder metadataBuilder, BundleCapability bundleCapability) {
        for (Map.Entry<String, Object> att : bundleCapability.getAttributes().entrySet()) {
            metadataBuilder.set(att.getKey(), att.getValue());
        }
        for (Map.Entry<String, String> dir : bundleCapability.getDirectives().entrySet()) {
            metadataBuilder.set(dir.getKey(), dir.getValue());
        }
        return metadataBuilder;
    }

    public static Resource getChild(Resource parentResource, String childName) {
        Resource resource = null;
        if (parentResource != null) {
            resource = parentResource.getResource(parentResource.getPath().add(Path.from(Path.SEPARATOR + childName)).toString());
        }
        return resource;
    }

    public class BundleNamespace {

        public static final String BUNDLE_NAMESPACE = "osgi.wiring.bundle";

        public static final String BUNDLE_STATE = "bundle-state";

        public static final String BUNDLE_FRAGMENT = "bundle-fragment";

        public static final String BUNDLE_LOCATION = "bundle-location";

        public static final String BUNDLE_LAST_MODIFIED = "bundle-last-modified";
    }

    public class PackageNamespace {

        public static final String PACKAGE_ID_SEPARATOR = "-";

        public static final String PACKAGE_NAMESPACE = "osgi.wiring.package";

        public static final String PACKAGE_VERSION_ATTRIBUTE = "version";

        public static final String CAPABILITY_BUNDLE_SYMBOLICNAME_ATTRIBUTE = "bundle-symbolic-name";

        public static final String CAPABILITY_BUNDLE_VERSION_ATTRIBUTE = "bundle-version";

        public static final String CAPABILITY_EXCLUDE_DIRECTIVE = "exclude";

        public static final String CAPABILITY_INCLUDE_DIRECTIVE = "include";

        public static final String REQUIREMENT_RESOLUTION_DIRECTIVE = "resolution";

        public static final String RESOLUTION_DYNAMIC = "dynamic";
    }
}
