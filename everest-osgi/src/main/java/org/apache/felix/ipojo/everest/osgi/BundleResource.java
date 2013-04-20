package org.apache.felix.ipojo.everest.osgi;

import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.services.Path;
import org.osgi.framework.Bundle;

import static org.apache.felix.ipojo.everest.osgi.BundleResourceManager.BUNDLE_PATH;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/19/13
 * Time: 4:56 PM
 */
public class BundleResource extends DefaultResource {

    public BundleResource(Bundle bundle) {
        super(BUNDLE_PATH.add(Path.from(String.valueOf(bundle.getBundleId()))));
    }

}
