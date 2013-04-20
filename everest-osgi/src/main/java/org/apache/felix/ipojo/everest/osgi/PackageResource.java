package org.apache.felix.ipojo.everest.osgi;

import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.services.Path;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

import static org.apache.felix.ipojo.everest.osgi.PackageResourceManager.PACKAGE_PATH;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/20/13
 * Time: 9:06 AM
 */
public class PackageResource extends DefaultResource {

    public static final String PACKAGE_ID_SEPARATOR = "-";

    public static String uniquePackageId(long bundleId, String packageName, Version version){
        StringBuilder sb = new StringBuilder();
        sb.append(bundleId);
        sb.append(PACKAGE_ID_SEPARATOR);
        sb.append(packageName);
        sb.append(PACKAGE_ID_SEPARATOR);
        sb.append(version);
        return sb.toString();
    }

    public PackageResource(Bundle bundle, String packageName, Version version){
        super(PACKAGE_PATH.add(Path.from(uniquePackageId(bundle.getBundleId(), packageName, version))));

    }



}
