package org.apache.felix.ipojo.everest.system.mx;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import static org.apache.felix.ipojo.everest.system.mx.MXResourceManager.MX_PATH;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 5/28/13
 * Time: 1:40 PM
 */
public class ThreadMxResource extends DefaultReadOnlyResource {

    private static final Path THREADS_MX_PATH = MX_PATH.addElements("threads");

    private final ThreadMXBean m_threadMXBean;

    private static final ThreadMxResource instance = new ThreadMxResource();

    public static ThreadMxResource getInstance() {
        return instance;
    }

    public ThreadMxResource() {
        super(THREADS_MX_PATH);
        m_threadMXBean = ManagementFactory.getThreadMXBean();
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        metadataBuilder.set("peak-thread-count", m_threadMXBean.getPeakThreadCount());
        metadataBuilder.set("thread-count", m_threadMXBean.getThreadCount());
        return metadataBuilder.build();
    }

}
