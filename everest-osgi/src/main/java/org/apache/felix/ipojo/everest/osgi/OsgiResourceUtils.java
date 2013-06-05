package org.apache.felix.ipojo.everest.osgi;

import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.Path;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.service.log.LogService;

import java.util.Collection;
import java.util.Map;

import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.PackageNamespace.*;


/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/21/13
 * Time: 7:42 PM
 */
public class OsgiResourceUtils {

    public enum BundleState {
        ACTIVE, STARTING, STOPPING, RESOLVED, INSTALLED, UNINSTALLED
    }


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
                throw new IllegalArgumentException(bundleState + " is not a bundle state");
        }
    }

    public static int toBundleState(String bundleState) throws IllegalArgumentException {
        BundleState state;
        try {
            state = BundleState.valueOf(bundleState);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(bundleState + " is not a bundle state");
        }
        switch (state) {
            case ACTIVE:
                return Bundle.ACTIVE;
            case STARTING:
                return Bundle.STARTING;
            case STOPPING:
                return Bundle.STOPPING;
            case RESOLVED:
                return Bundle.RESOLVED;
            case INSTALLED:
                return Bundle.INSTALLED;
            case UNINSTALLED:
                return Bundle.UNINSTALLED;
            default:
                throw new IllegalArgumentException(bundleState + " is not a bundle state");
        }
    }

    public static String logLevelToString(int level) {
        switch (level) {
            case LogService.LOG_DEBUG:
                return "DEBUG";
            case LogService.LOG_INFO:
                return "INFO";
            case LogService.LOG_WARNING:
                return "WARNING";
            case LogService.LOG_ERROR:
                return "ERROR";
            default:
                return "";
        }
    }

    public static String uniqueCapabilityId(BundleCapability bundleCapability) {
        long bundleId = bundleCapability.getRevision().getBundle().getBundleId();
        String capabilityName = "";
        Object capability = bundleCapability.getAttributes().get(bundleCapability.getNamespace());
        if (capability != null) { // you never know
            // crossing fingers capability can be a collection
            if (capability instanceof Collection) {
                for (Object c : (Collection) capability) {
                    capabilityName += c.toString() + PACKAGE_ID_SEPARATOR;
                }
                capabilityName = capabilityName.substring(0, capabilityName.length() - 1);
            } else if (capability instanceof Object[]) { // in some cases string array
                for (Object c : (Object[]) capability) {
                    capabilityName += c.toString() + PACKAGE_ID_SEPARATOR;
                }
                capabilityName = capabilityName.substring(0, capabilityName.length() - 1);
            } else {
                capabilityName = capability.toString();
            }
            // look for {@code Path.SEPARATOR} in capabilityName
            capabilityName = capabilityName.replaceAll(Path.SEPARATOR, PACKAGE_ID_SEPARATOR);
        }
        // crossing fingers :: Version can also be list of versions
        String versionString = "";
        Object version = bundleCapability.getAttributes().get(PACKAGE_VERSION_ATTRIBUTE);
        if (version != null) { // never know
            if (version instanceof Collection) {
                for (Object v : (Collection) version) {
                    versionString += v.toString() + PACKAGE_ID_SEPARATOR;
                }
                versionString = versionString.substring(0, versionString.length() - 1);
            } else {
                versionString = version.toString();
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(bundleId);
        sb.append(PACKAGE_ID_SEPARATOR);
        sb.append(bundleCapability.getNamespace());
        sb.append(PACKAGE_ID_SEPARATOR);
        sb.append(capabilityName);
        sb.append(PACKAGE_ID_SEPARATOR);
        sb.append(versionString);
        return sb.toString();
    }

    public static String uniqueRequirementId(BundleRequirement bundleRequirement) {
        long bundleId = bundleRequirement.getRevision().getBundle().getBundleId();
        String requirementString = "";
        Object requirementName = bundleRequirement.getDirectives().get(REQUIREMENT_FILTER_ATTRIBUTE);
        if (requirementName != null) { // you never know
            requirementString = requirementName.toString();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(bundleId);
        sb.append(PACKAGE_ID_SEPARATOR);
        sb.append(bundleRequirement.getNamespace());
        sb.append(PACKAGE_ID_SEPARATOR);
        sb.append(requirementString);
        return sb.toString();
    }

    public static String uniqueWireId(BundleWire wire) {
        return "wire-" + wire.hashCode();
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

    // TODO
    public static String[] packageNamesFromService(ServiceReference m_serviceReference) {
        String[] interfaces = (String[]) m_serviceReference.getProperty(Constants.OBJECTCLASS);
        String[] packageNames = new String[interfaces.length];
        for (int i = 0; i < interfaces.length; i++) {
            int index = interfaces[i].lastIndexOf(".");
            String s = interfaces[i].substring(0, index);
            packageNames[i] = s;
        }
        return packageNames;
    }

    public class BundleNamespace {

        public static final String BUNDLE_NAMESPACE = "osgi.wiring.bundle";

        public static final String BUNDLE_STATE = "bundle-state";

        public static final String BUNDLE_ID = "bundle-id";

        public static final String BUNDLE_FRAGMENT = "bundle-fragment";

        public static final String BUNDLE_LOCATION = "bundle-location";

        public static final String BUNDLE_LAST_MODIFIED = "bundle-last-modified";
    }

    public class PackageNamespace {

        public static final String PACKAGE_ID_SEPARATOR = "-";

        public static final String PACKAGE_NAMESPACE = "osgi.wiring.package";

        // TODO should be externalized into a general namespace
        public static final String PACKAGE_VERSION_ATTRIBUTE = "version";

        public static final String REQUIREMENT_FILTER_ATTRIBUTE = "filter";

        public static final String CAPABILITY_BUNDLE_SYMBOLICNAME_ATTRIBUTE = "bundle-symbolic-name";

        public static final String CAPABILITY_BUNDLE_VERSION_ATTRIBUTE = "bundle-version";

        public static final String CAPABILITY_EXCLUDE_DIRECTIVE = "exclude";

        public static final String CAPABILITY_INCLUDE_DIRECTIVE = "include";

        public static final String REQUIREMENT_RESOLUTION_DIRECTIVE = "resolution";

        public static final String RESOLUTION_DYNAMIC = "dynamic";
    }
}
