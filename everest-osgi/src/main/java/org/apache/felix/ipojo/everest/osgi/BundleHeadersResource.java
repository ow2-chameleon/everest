package org.apache.felix.ipojo.everest.osgi;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.services.Path;
import org.osgi.framework.Bundle;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/21/13
 * Time: 3:51 PM
 */
public class BundleHeadersResource extends DefaultReadOnlyResource {
    public static final String HEADERS_PATH = "headers";

    private final Bundle m_bundle;

    public BundleHeadersResource(Path path,Bundle bundle){
        super(path.add(Path.from(Path.SEPARATOR+HEADERS_PATH)));
        m_bundle= bundle;
    }

}
