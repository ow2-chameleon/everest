package org.apache.felix.ipojo.everest.system.mx;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import static org.apache.felix.ipojo.everest.system.mx.MXResourceManager.MX_PATH;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 5/28/13
 * Time: 1:33 PM
 */
public class RuntimeMxResource extends DefaultReadOnlyResource {

    private static final Path RUNTIME_MX_PATH = MX_PATH.addElements("runtime");

    private final RuntimeMXBean m_runtimeMXBean;

    private static final RuntimeMxResource instance = new RuntimeMxResource();

    public static RuntimeMxResource getInstance() {
        return instance;
    }

    public RuntimeMxResource() {
        super(RUNTIME_MX_PATH);
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
