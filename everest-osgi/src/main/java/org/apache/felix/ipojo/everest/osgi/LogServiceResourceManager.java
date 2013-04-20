package org.apache.felix.ipojo.everest.osgi;

import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.services.Path;
import org.osgi.service.log.LogService;

import static org.apache.felix.ipojo.everest.osgi.OsgiRootResource.OSGI_ROOT_PATH;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/20/13
 * Time: 11:45 AM
 */
public class LogServiceResourceManager extends DefaultResource {

    public static final String LOG_ROOT_NAME = "logs";

    public static final Path LOG_PATH = OSGI_ROOT_PATH.add(Path.from(Path.SEPARATOR + LOG_ROOT_NAME));

    public LogServiceResourceManager(LogService logService) {
        super(LOG_PATH);
    }
}
