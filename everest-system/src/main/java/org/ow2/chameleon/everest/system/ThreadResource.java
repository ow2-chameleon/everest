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

package org.ow2.chameleon.everest.system;

import org.ow2.chameleon.everest.impl.DefaultReadOnlyResource;
import org.ow2.chameleon.everest.impl.ImmutableResourceMetadata;
import org.ow2.chameleon.everest.services.Path;
import org.ow2.chameleon.everest.services.ResourceMetadata;

import java.lang.management.ThreadInfo;

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 15/07/13
 * Time: 16:42
 * To change this template use File | Settings | File Templates.
 */
public class ThreadResource extends DefaultReadOnlyResource {

    private final ThreadManagerResource m_parentManager;

    private final long m_ids;

    public ThreadResource(long ids, ThreadManagerResource parent) {
        super(parent.getPath().add(Path.from(Path.SEPARATOR + ids)));
        m_parentManager = parent;
        m_ids = ids;
    }

    public ResourceMetadata getMetadata() {
        ThreadInfo threadInfo;
        threadInfo = m_parentManager.getBean().getThreadInfo(m_ids);
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        metadataBuilder.set("Thread-Ids", m_ids);
        if (threadInfo!=null){
            metadataBuilder.set("Thread-Name", threadInfo.getThreadName());
            metadataBuilder.set("Block-Count", threadInfo.getBlockedCount());
            metadataBuilder.set("Block-Time", threadInfo.getBlockedTime());
            metadataBuilder.set("Lock-Name", threadInfo.getLockName());
            metadataBuilder.set("Lock-Owner-Id", threadInfo.getLockOwnerId());
            metadataBuilder.set("Thread-Name", threadInfo.getThreadName());
            metadataBuilder.set("Thread-State", threadInfo.getThreadState());
            metadataBuilder.set("Waited-Time", threadInfo.getWaitedTime());
        }
        metadataBuilder.set("Thread-User-Time", m_parentManager.getBean().getThreadUserTime(m_ids));
        metadataBuilder.set("CPU-Time", m_parentManager.getBean().getThreadCpuTime(m_ids));
        return metadataBuilder.build();
    }


}
