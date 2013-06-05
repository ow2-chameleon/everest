package org.apache.felix.ipojo.everest.osgi.bundle;

import org.apache.felix.ipojo.everest.osgi.AbstractResourceCollection;
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
public class BundleWiresResource extends AbstractResourceCollection {

    public static final String WIRES_PATH = "wires";

    private final Bundle m_bundle;

    List<BundleWireResource> bundleWireResources = new ArrayList<BundleWireResource>();

    public BundleWiresResource(Path path, Bundle bundle) {
        super(path.addElements(WIRES_PATH));
        m_bundle = bundle;
        BundleWiring wiring = m_bundle.adapt(BundleWiring.class);
        if (wiring != null) {
            // get provided wires from all namespaces
            List<BundleWire> providedWires = wiring.getProvidedWires(null);
            if (providedWires != null) {
                for (BundleWire providedWire : providedWires) {
                    bundleWireResources.add(new BundleWireResource(getPath(), providedWire));
                }
            }
            // get required wires from all namespaces
            List<BundleWire> requiredWires = wiring.getRequiredWires(null);
            if (requiredWires != null) {
                for (BundleWire requiredWire : requiredWires) {
                    bundleWireResources.add(new BundleWireResource(getPath(), requiredWire));
                }
            }
        }
    }

    @Override
    public List<Resource> getResources() {
        ArrayList<Resource> resources = new ArrayList<Resource>();
        resources.addAll(bundleWireResources);
        return resources;
    }
}