package org.apache.felix.ipojo.everest.osgi;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Resource;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/21/13
 * Time: 3:52 PM
 */
public class BundleWiresResource extends DefaultReadOnlyResource {

    public static final String WIRES_PATH = "wires";

    public static final String PROVIDED_WIRES_PATH = "provided";
    public static final String REQUIRED_WIRES_PATH = "required";

    private final Bundle m_bundle;

    public BundleWiresResource(Path path, Bundle bundle) {
        super(path.add(Path.from(Path.SEPARATOR + WIRES_PATH)));
        m_bundle = bundle;
    }

    @Override
    public List<Resource> getResources() {
        ArrayList<Resource> resources = new ArrayList<Resource>();
        BundleWiring wiring = m_bundle.adapt(BundleWiring.class);
        // get provided wires from all namespaces
        List<BundleWire> providedWires = wiring.getProvidedWires(null);
        for (BundleWire providedWire : providedWires) {
            resources.add(new ProvidedWireResource(getPath().add(Path.from(Path.SEPARATOR + PROVIDED_WIRES_PATH)), providedWire));
        }
        // get required wires from all namespaces
        List<BundleWire> requiredWires = wiring.getRequiredWires(null);
        for (BundleWire requiredWire : requiredWires) {
            resources.add(new RequiredWireResource(getPath().add(Path.from(Path.SEPARATOR + REQUIRED_WIRES_PATH)), requiredWire));
        }
        return resources;
    }
}