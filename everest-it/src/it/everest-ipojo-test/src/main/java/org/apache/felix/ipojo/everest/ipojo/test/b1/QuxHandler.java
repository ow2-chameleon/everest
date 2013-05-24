package org.apache.felix.ipojo.everest.ipojo.test.b1;

import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.PrimitiveHandler;
import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.ipojo.metadata.Element;

import java.util.Dictionary;


@Handler(namespace = "foo.bar", name = "qux",
        // Just to have a dependency on the "architecture" handler
        architecture = true)
// Just to have a dependency on the "provides" handler
@Provides(specifications = Runnable.class)
public class QuxHandler extends PrimitiveHandler implements Runnable {

    // Just to have a dependency on the "required" handler
    @Requires(id = "useless1", optional =  true)
    private Runnable m_useless1;

    // Just to have a dependency on the "property" handler
    @Property(name = "useless2", mandatory = false)
    private double m_useless2;

    // Just to have a dependency on the "controller" handler
    @Controller
    private boolean m_controller;

    @Override
    public void configure(Element metadata, Dictionary configuration) throws ConfigurationException {
    }

    @Override
    public void stop() {
    }

    @Override
    public void start() {
    }

    public void run() {
        System.err.println("Boohoo!");
    }

    // Just to have a dependency on the "callback" handler
    @Validate
    public void validate() {
    }
    @Invalidate
    public void invalidate() {
    }

}
