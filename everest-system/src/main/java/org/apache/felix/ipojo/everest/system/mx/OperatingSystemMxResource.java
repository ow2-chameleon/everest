package org.apache.felix.ipojo.everest.system.mx;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

import static org.apache.felix.ipojo.everest.system.mx.MXResourceManager.MX_PATH;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 5/28/13
 * Time: 1:39 PM
 */
public class OperatingSystemMxResource extends DefaultReadOnlyResource {

    private static final Path OS_MX_PATH = MX_PATH.addElements("os");

    private final OperatingSystemMXBean m_osMXBean;

    private static final OperatingSystemMxResource instance = new OperatingSystemMxResource();

    public static OperatingSystemMxResource getInstance() {
        return instance;
    }

    public OperatingSystemMxResource() {
        super(OS_MX_PATH);
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
