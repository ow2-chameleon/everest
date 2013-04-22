package org.apache.felix.ipojo.everest.ipojo.components;

import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.ipojo.everest.ipojo.services.BarService;
import org.apache.felix.ipojo.everest.ipojo.services.FooService;

@Component
@Provides
public class BarProviderImpl implements BarService {

    @Requires
    private FooService m_foo;

    @Property
    private String barPrefix;

    @ServiceProperty
    private String barSuffix;

    public String getBar() {
        return barPrefix + m_foo.getFoo() + barSuffix;
    }
    
}
