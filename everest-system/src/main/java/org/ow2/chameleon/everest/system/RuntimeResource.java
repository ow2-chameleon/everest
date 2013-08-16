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
import java.lang.management.RuntimeMXBean;

import static org.ow2.chameleon.everest.system.SystemRootResource.SYSTEM_ROOT_PATH;


/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 5/28/13
 * Time: 1:33 PM
 */
public class RuntimeResource extends DefaultReadOnlyResource {

    private static final Path RUNTIME_PATH = SYSTEM_ROOT_PATH.addElements("runtime");

    private final RuntimeMXBean m_runtimeMXBean;

    private static final RuntimeResource instance = new RuntimeResource();

    public static RuntimeResource getInstance() {
        return instance;
    }

    public RuntimeResource() {
        super(RUNTIME_PATH);
        m_runtimeMXBean = ManagementFactory.getRuntimeMXBean();
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        metadataBuilder.set("uptime", m_runtimeMXBean.getUptime());
        metadataBuilder.set("starttime", m_runtimeMXBean.getStartTime());
        metadataBuilder.set("args", m_runtimeMXBean.getInputArguments());
        metadataBuilder.set("vm-name", m_runtimeMXBean.getVmName());
        metadataBuilder.set("vm-vendor", m_runtimeMXBean.getVmVendor());
        metadataBuilder.set("vm-version", m_runtimeMXBean.getVmVersion());
        metadataBuilder.set("management-spec-version", m_runtimeMXBean.getManagementSpecVersion());
        metadataBuilder.set("class-path", m_runtimeMXBean.getClassPath());
        metadataBuilder.set("boot-class-path-supported", m_runtimeMXBean.isBootClassPathSupported());
        return metadataBuilder.build();
    }
}
