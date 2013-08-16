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

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

import static org.ow2.chameleon.everest.system.SystemRootResource.SYSTEM_ROOT_PATH;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 5/28/13
 * Time: 1:42 PM
 */
public class MemoryResource extends DefaultReadOnlyResource {

    private static final Path MEMORY_PATH = SYSTEM_ROOT_PATH.addElements("memory");

    private final MemoryMXBean m_memoryMXBean;

    private static final MemoryResource instance = new MemoryResource();

    public static MemoryResource getInstance() {
        return instance;
    }

    public MemoryResource() {
        super(MEMORY_PATH);
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
        File file = new File("/");
        metadataBuilder.set("total-space", file.getTotalSpace());
        metadataBuilder.set("free-space", file.getFreeSpace());
        metadataBuilder.set("usable-space", file.getUsableSpace());
        return metadataBuilder.build();
    }
}


