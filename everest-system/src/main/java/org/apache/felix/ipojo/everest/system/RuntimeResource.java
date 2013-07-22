package org.apache.felix.ipojo.everest.system;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import static org.apache.felix.ipojo.everest.system.SystemRootResource.SYSTEM_ROOT_PATH;


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
