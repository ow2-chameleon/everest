package org.ow2.chameleon.everest.ipojo.test.b1;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;

@Component(name = "Foo", version = "1.2.3.foo")
@Provides
public class FooProviderImpl implements FooService {

    @Property(value = "")
    private String fooPrefix;

    @ServiceProperty(value = "0")
    private int fooCounter;

    public String getFoo() {
        return fooPrefix + Integer.toString(fooCounter++);
    }

}
