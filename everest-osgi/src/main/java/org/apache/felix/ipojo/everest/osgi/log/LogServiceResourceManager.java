package org.apache.felix.ipojo.everest.osgi.log;

import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Resource;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogReaderService;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

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
    private final LogReaderService m_logService;

    public LogServiceResourceManager(LogReaderService logService) {
        super(LOG_PATH);
        m_logService = logService;
    }

    @Override
    public List<Resource> getResources() {
        ArrayList<Resource> resources = new ArrayList<Resource>();
        Enumeration logs = m_logService.getLog();
        while (logs.hasMoreElements()) {
            LogEntry entry = (LogEntry) logs.nextElement();
            resources.add(new LogEntryResource(entry));
        }
        return resources;
    }
}
