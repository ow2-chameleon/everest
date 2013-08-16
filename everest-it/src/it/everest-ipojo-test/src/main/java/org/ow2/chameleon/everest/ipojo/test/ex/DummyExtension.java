package org.ow2.chameleon.everest.ipojo.test.ex;

import org.apache.felix.ipojo.*;
import org.apache.felix.ipojo.metadata.Element;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;

import java.util.Collections;
import java.util.Dictionary;
import java.util.List;

/**
 * Just a dummy iPOJO extension...
 */
public class DummyExtension extends IPojoFactory {

    public DummyExtension(BundleContext context, Element metadata) throws ConfigurationException {
        super(context, metadata);
    }

    @Override
    public String getFactoryName() {
        return "dummy";
    }

    @Override
    public List<RequiredHandler> getRequiredHandlerList() {
        return Collections.emptyList();
    }

    @Override
    public ComponentInstance createInstance(Dictionary config, IPojoContext context, HandlerManager[] handlers) throws ConfigurationException {
        throw new UnsupportedOperationException("dummy extension is not supposed to create instances");
    }

    @Override
    public String getClassName() {
        return "dummy";
    }

    public String getVersion() {
        return Version.emptyVersion.toString();
    }

    @Override
    public void starting() {
        System.out.println("Dummy extension is starting...");
    }

    @Override
    public void stopping() {
        System.out.println("Dummy extension is stopping...");
    }
}
