package org.apache.felix.ipojo.everest.osgi;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.PackageNamespace.PACKAGE_ID_SEPARATOR;

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

    public static String uniquePackageId(long bundleId, String packageName, Version version) {
        StringBuilder sb = new StringBuilder();
        sb.append(bundleId);
        sb.append(PACKAGE_ID_SEPARATOR);
        sb.append(packageName);
        sb.append(PACKAGE_ID_SEPARATOR);
        sb.append(version);
        return sb.toString();
    }

    public class BundleNamespace {

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
