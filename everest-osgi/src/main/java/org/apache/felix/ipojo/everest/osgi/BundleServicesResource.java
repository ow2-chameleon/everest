package org.apache.felix.ipojo.everest.osgi;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.services.Path;
import org.osgi.framework.Bundle;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/21/13
 * Time: 4:13 PM
 */
public class BundleServicesResource extends DefaultReadOnlyResource {

    public static final String BUNDLE_SERVICES_PATH = "services";

    public BundleServicesResource(Path path, Bundle bundle) {
        super(path.add(Path.from(Path.SEPARATOR + BUNDLE_SERVICES_PATH)));
    }
}
