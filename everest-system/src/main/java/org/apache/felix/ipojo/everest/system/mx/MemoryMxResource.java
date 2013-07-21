package org.apache.felix.ipojo.everest.system.mx;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

import static org.apache.felix.ipojo.everest.system.mx.MXResourceManager.MX_PATH;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 5/28/13
 * Time: 1:42 PM
 */
public class MemoryMxResource extends DefaultReadOnlyResource {

    private static final Path MEMORY_MX_PATH = MX_PATH.addElements("memory");

    private final MemoryMXBean m_memoryMXBean;

    private static final MemoryMxResource instance = new MemoryMxResource();

    public static MemoryMxResource getInstance() {
        return instance;
    }

    public MemoryMxResource() {
        super(MEMORY_MX_PATH);
        m_memoryMXBean = ManagementFactory.getMemoryMXBean();
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        metadataBuilder.set("used-heap", m_memoryMXBean.getHeapMemoryUsage().getUsed());
        metadataBuilder.set("commited-heap", m_memoryMXBean.getHeapMemoryUsage().getCommitted());
        metadataBuilder.set("used-nonheap", m_memoryMXBean.getNonHeapMemoryUsage().getUsed());
        metadataBuilder.set("commited-nonheap", m_memoryMXBean.getNonHeapMemoryUsage().getCommitted());
        metadataBuilder.set("verbose", m_memoryMXBean.isVerbose());
        return metadataBuilder.build();
    }
}


