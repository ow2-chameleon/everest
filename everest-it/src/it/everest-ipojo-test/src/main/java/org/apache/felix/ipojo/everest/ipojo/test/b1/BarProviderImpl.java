package org.apache.felix.ipojo.everest.ipojo.test.b1;

import org.apache.felix.ipojo.annotations.*;

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
