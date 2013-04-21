package org.apache.felix.ipojo.everest.osgi;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.services.Path;
import org.osgi.framework.Bundle;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/21/13
 * Time: 3:52 PM
 */
public class BundleWiresResource extends DefaultReadOnlyResource {

    public static final String WIRES_PATH = "wires";

    private final Bundle m_bundle;

    public BundleWiresResource(Path path, Bundle bundle){
        super(path.add(Path.from(Path.SEPARATOR+WIRES_PATH)));
        m_bundle = bundle;
    }
}
