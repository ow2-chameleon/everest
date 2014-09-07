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
import org.ow2.chameleon.everest.impl.DefaultRelation;
import org.ow2.chameleon.everest.impl.ImmutableResourceMetadata;
import org.ow2.chameleon.everest.services.*;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.ow2.chameleon.everest.system.SystemRootResource.SYSTEM_ROOT_PATH;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 5/28/13
 * Time: 1:40 PM
 */
public class ThreadManagerResource extends DefaultReadOnlyResource {

    private static final Path THREADS_PATH = SYSTEM_ROOT_PATH.addElements("threads");

    private final ThreadMXBean m_threadMXBean;

    private static final ThreadManagerResource instance = new ThreadManagerResource();

    private final Map<Long, ThreadResource> m_threadResource = new HashMap<Long, ThreadResource>();

    public static ThreadManagerResource getInstance() {
        return instance;
    }

    public ThreadManagerResource() {
        super(THREADS_PATH);
        m_threadMXBean = ManagementFactory.getThreadMXBean();
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        metadataBuilder.set("Peak-Thread-Count", m_threadMXBean.getPeakThreadCount());
        metadataBuilder.set("Thread-Count", m_threadMXBean.getThreadCount());
        metadataBuilder.set("Tread-Current-CPU-Time-Enable", m_threadMXBean.isThreadCpuTimeEnabled());
        metadataBuilder.set("Tread-Current-CPU-Time-Supported", m_threadMXBean.isCurrentThreadCpuTimeSupported());
        metadataBuilder.set("Object-Monitor-Usage-Supported", m_threadMXBean.isObjectMonitorUsageSupported());
        metadataBuilder.set("Synchronizer-Usage-Supported", m_threadMXBean.isSynchronizerUsageSupported());
        metadataBuilder.set("Contention-Monitoring-Enable", m_threadMXBean.isThreadContentionMonitoringEnabled());
        metadataBuilder.set("Contention-Monitoring-Supported", m_threadMXBean.isThreadContentionMonitoringSupported());
        return metadataBuilder.build();
    }

    @Override
    public List<Resource> getResources() {
        long[] threadIds;
        List<Resource> resources = new ArrayList<Resource>();

        m_threadResource.clear();

        threadIds = m_threadMXBean.getAllThreadIds();

        for (long ids : threadIds) {
            m_threadResource.put(ids, new ThreadResource(ids, this));
        }

        for (Long ids : m_threadResource.keySet()) {
            resources.add(m_threadResource.get(ids));
        }
        return resources;

    }

    public ThreadMXBean getBean() {
        return m_threadMXBean;
    }

    public List<Relation> getRelations() {
        List<Relation> relations = new ArrayList<Relation>();
        relations.addAll(super.getRelations());
        for (Resource resource : getResources()) {
            int size = getCanonicalPath().getCount();
            String name = resource.getCanonicalPath().getElements()[size];
            relations.add(new DefaultRelation(resource.getCanonicalPath(), Action.READ, getCanonicalPath().getLast() + ":" + name,
                    "Get " + name));
        }
        return relations;
    }

}
