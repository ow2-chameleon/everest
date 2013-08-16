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

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

import static org.ow2.chameleon.everest.system.SystemRootResource.SYSTEM_ROOT_PATH;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 5/28/13
 * Time: 1:39 PM
 */
public class OperatingSystemResource extends DefaultReadOnlyResource {

    private static final Path OS_PATH = SYSTEM_ROOT_PATH.addElements("os");

    private final OperatingSystemMXBean m_osMXBean;

    private static final OperatingSystemResource instance = new OperatingSystemResource();

    public static OperatingSystemResource getInstance() {
        return instance;
    }

    public OperatingSystemResource() {
        super(OS_PATH);
        m_osMXBean = ManagementFactory.getOperatingSystemMXBean();
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        metadataBuilder.set("arch", m_osMXBean.getArch());
        metadataBuilder.set("version", m_osMXBean.getVersion());
        metadataBuilder.set("processors", m_osMXBean.getAvailableProcessors());
        metadataBuilder.set("name", m_osMXBean.getName());
        metadataBuilder.set("system-load", m_osMXBean.getSystemLoadAverage());
        return metadataBuilder.build();
    }
}
