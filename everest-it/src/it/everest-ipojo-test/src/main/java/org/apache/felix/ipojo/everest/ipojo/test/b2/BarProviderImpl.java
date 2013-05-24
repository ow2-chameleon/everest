package org.apache.felix.ipojo.everest.ipojo.test.b2;

import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.ipojo.everest.ipojo.test.b1.BarService;
import org.apache.felix.ipojo.everest.ipojo.test.b1.FooService;

@Component(name = "org.apache.felix.ipojo.everest.ipojo.test.b1.BarProviderImpl", version = "2.0.0")
@Provides
public class BarProviderImpl implements BarService {

    @Requires
    private FooService m_foo;

    @Property
    private String barPrefix;

    @ServiceProperty
    private String barSuffix;

    public String getBar() {
        return barPrefix + m_foo.getFoo() + barSuffix + "-v2.0.0";
    }
    
}
