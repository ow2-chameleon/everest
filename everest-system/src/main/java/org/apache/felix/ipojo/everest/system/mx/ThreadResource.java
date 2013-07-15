package org.apache.felix.ipojo.everest.system.mx;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;

import java.lang.management.ThreadInfo;

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 15/07/13
 * Time: 16:42
 * To change this template use File | Settings | File Templates.
 */
public class ThreadResource extends DefaultReadOnlyResource {

    private final ThreadMxResource m_parentManager;

    private final long m_ids;

    public ThreadResource(long ids, ThreadMxResource parent) {
        super(parent.getPath().add(Path.from(Path.SEPARATOR + ids)));
        m_parentManager = parent;
        m_ids = ids;
    }

    public ResourceMetadata getMetadata() {
        ThreadInfo threadInfo;
        threadInfo = m_parentManager.getBean().getThreadInfo(m_ids);
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        metadataBuilder.set("Thread-Ids", m_ids);
        metadataBuilder.set("Thread-User-Time", m_parentManager.getBean().getThreadUserTime(m_ids));
        metadataBuilder.set("CPU-Time", m_parentManager.getBean().getThreadCpuTime(m_ids));
        metadataBuilder.set("Block-Count", threadInfo.getBlockedCount());
        metadataBuilder.set("Block-Time", threadInfo.getBlockedTime());
        metadataBuilder.set("Lock-Name", threadInfo.getLockName());
        metadataBuilder.set("Lock-Owner-Id", threadInfo.getLockOwnerId());
        metadataBuilder.set("Thread-Name", threadInfo.getThreadName());
        metadataBuilder.set("Thread-State", threadInfo.getThreadState());
        metadataBuilder.set("Waited-Time", threadInfo.getWaitedTime());
        return metadataBuilder.build();
    }


}
