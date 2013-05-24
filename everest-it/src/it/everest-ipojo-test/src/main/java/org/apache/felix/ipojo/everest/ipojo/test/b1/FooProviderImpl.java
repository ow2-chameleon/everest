package org.apache.felix.ipojo.everest.ipojo.test.b1;

import org.apache.felix.ipojo.annotations.*;

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
