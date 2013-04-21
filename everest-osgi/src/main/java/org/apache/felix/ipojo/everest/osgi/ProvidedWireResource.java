package org.apache.felix.ipojo.everest.osgi;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.services.Path;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWire;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/21/13
 * Time: 9:08 PM
 */
public class ProvidedWireResource extends DefaultReadOnlyResource {

    Bundle m_provider;
    Bundle m_requirer;

    public ProvidedWireResource(Path path, BundleWire wire) {
        super(path.add(Path.from(Path.SEPARATOR + wire.hashCode())));
        m_provider = wire.getProviderWiring().getBundle();
        m_requirer = wire.getRequirerWiring().getBundle();
    }

}
