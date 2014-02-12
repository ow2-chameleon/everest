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

import org.ow2.chameleon.everest.impl.DefaultRelation;
import org.ow2.chameleon.everest.impl.DefaultResource;
import org.ow2.chameleon.everest.impl.ImmutableResourceMetadata;
import org.ow2.chameleon.everest.osgi.bundle.BundleResourceManager;
import org.ow2.chameleon.everest.osgi.service.ServiceResourceManager;
import org.ow2.chameleon.everest.services.Action;
import org.ow2.chameleon.everest.services.Path;
import org.ow2.chameleon.everest.services.Relation;
import org.ow2.chameleon.everest.services.ResourceMetadata;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogEntry;

import java.util.ArrayList;

import static org.ow2.chameleon.everest.osgi.OsgiResourceUtils.logLevelToString;
import static org.ow2.chameleon.everest.osgi.log.LogServiceResourceManager.LOG_PATH;

/**
 * Resource representing a {@code LogEntry}.
 */
public class LogEntryResource extends DefaultResource<LogEntry> {

    /**
     * Represented log entry
     */
    private final LogEntry m_logEntry;

    /**
     * Constructor for log entry resource
     *
     * @param logEntry {@code LogEntry}
     */
    public LogEntryResource(LogEntry logEntry) {
        super(LOG_PATH.addElements(Long.toString(logEntry.getTime())));
        m_logEntry = logEntry;
        ArrayList<Relation> relations = new ArrayList<Relation>();
        Path bundlePath = BundleResourceManager.getInstance().getPath().add(Path.from(Path.SEPARATOR + m_logEntry.getBundle().getBundleId()));
        relations.add(new DefaultRelation(bundlePath, Action.READ, "bundle"));
        ServiceReference serviceReference = m_logEntry.getServiceReference();
        if (serviceReference != null) {
            String serviceId = serviceReference.getProperty(Constants.SERVICE_ID).toString();
            Path servicePath = ServiceResourceManager.getInstance().getPath().addElements(serviceId);
            relations.add(new DefaultRelation(servicePath, Action.READ, "service"));
        }
        setRelations(relations);
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        metadataBuilder.set("log level", logLevelToString(m_logEntry.getLevel()));
        metadataBuilder.set("time", m_logEntry.getTime());
        metadataBuilder.set("message", m_logEntry.getMessage());
        metadataBuilder.set("bundle", m_logEntry.getBundle().getBundleId());
        metadataBuilder.set("service", m_logEntry.getServiceReference().getProperty(Constants.SERVICE_ID));
        metadataBuilder.set("exception", m_logEntry.getException().getStackTrace());
        return metadataBuilder.build();
    }

    @Override
    public <A> A adaptTo(Class<A> clazz) {
        if (LogEntry.class.equals(clazz)) {
            return (A) m_logEntry;
        } else if (LogEntryResource.class.equals(clazz)) {
            return (A) this;
        } else {
            return null;
        }
    }

    public String getLogLevel() {
        return logLevelToString(m_logEntry.getLevel());
    }

    public long getTime() {
        return m_logEntry.getTime();
    }

    public String getMessage() {
        return m_logEntry.getMessage();
    }

    public long getBundleId() {
        return m_logEntry.getBundle().getBundleId();
    }

    public Long getServiceId() {
        if (m_logEntry.getServiceReference() != null) {
            return (Long) m_logEntry.getServiceReference().getProperty(Constants.SERVICE_ID);
        }
        return null;
    }

    public StackTraceElement[] getStackTrace() {
        return m_logEntry.getException().getStackTrace();
    }

}
