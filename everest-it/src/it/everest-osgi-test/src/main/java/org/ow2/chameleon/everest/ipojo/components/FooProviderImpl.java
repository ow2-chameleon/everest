package org.ow2.chameleon.everest.ipojo.components;

import org.apache.felix.ipojo.annotations.*;
import org.ow2.chameleon.everest.ipojo.services.FooService;

@Component(name = "Foo", version = "1.2.3.foo")
@Provides
public class FooProviderImpl implements FooService {

    @Property
    private String fooPrefix;

    @ServiceProperty
    private int fooCounter;

    public String getFoo() {
        return fooPrefix + Integer.toString(fooCounter);
    }
    
}
