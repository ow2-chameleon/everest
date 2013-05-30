package org.apache.felix.ipojo.everest.osgi.log;

import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.osgi.bundle.BundleResourceManager;
import org.apache.felix.ipojo.everest.osgi.service.ServiceResourceManager;
import org.apache.felix.ipojo.everest.services.Action;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Relation;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogEntry;

import java.util.ArrayList;

import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.logLevelToString;
import static org.apache.felix.ipojo.everest.osgi.log.LogServiceResourceManager.LOG_PATH;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 5/1/13
 * Time: 5:22 PM
 */
public class LogEntryResource extends DefaultResource {

    private final LogEntry m_logEntry;

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
