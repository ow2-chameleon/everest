package org.apache.felix.ipojo.everest.ipojo.components;

import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.PrimitiveHandler;
import org.apache.felix.ipojo.annotations.Handler;
import org.apache.felix.ipojo.metadata.Element;

import java.util.Dictionary;

@Handler(namespace = "foo.bar", name = "qux")
public class QuxHandler extends PrimitiveHandler {

    @Override
    public void configure(Element metadata, Dictionary configuration) throws ConfigurationException {
    }

    @Override
    public void stop() {
    }

    @Override
    public void start() {
    }
}
