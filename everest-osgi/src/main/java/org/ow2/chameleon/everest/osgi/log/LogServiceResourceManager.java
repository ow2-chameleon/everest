/*
 * Copyright 2013 OW2 Chameleon
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ow2.chameleon.everest.osgi.log;

import org.ow2.chameleon.everest.osgi.AbstractResourceCollection;
import org.ow2.chameleon.everest.services.Path;
import org.ow2.chameleon.everest.services.Resource;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogReaderService;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static org.ow2.chameleon.everest.osgi.OsgiRootResource.OSGI_ROOT_PATH;

/**
 * Resource manager for log service.
 */
public class LogServiceResourceManager extends AbstractResourceCollection {

    /**
     * Name for logs resource
     */
    public static final String LOG_ROOT_NAME = "logs";

    /**
     * Path for osgi logs : "/osgi/logs"
     */
    public static final Path LOG_PATH = OSGI_ROOT_PATH.add(Path.from(Path.SEPARATOR + LOG_ROOT_NAME));

    /**
     * Log reader service
     */
    private final LogReaderService m_logService;

    /**
     * Constructor for this resource manager
     *
     * @param logService
     */
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
