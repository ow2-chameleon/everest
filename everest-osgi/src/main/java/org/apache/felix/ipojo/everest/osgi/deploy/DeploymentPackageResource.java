package org.apache.felix.ipojo.everest.osgi.deploy;

import org.apache.felix.ipojo.everest.impl.DefaultParameter;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.osgi.bundle.BundleRelationsResource;
import org.apache.felix.ipojo.everest.services.*;
import org.osgi.framework.Bundle;
import org.osgi.service.deploymentadmin.BundleInfo;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;

import java.util.ArrayList;
import java.util.List;

import static org.apache.felix.ipojo.everest.osgi.deploy.DeploymentAdminResourceManager.DEPLOY_PATH;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 5/1/13
 * Time: 6:36 PM
 */
public class DeploymentPackageResource extends DefaultResource {

    private final DeploymentPackage m_deploymentPackage;

    private final BundleInfo[] m_bundleInfos;

    public DeploymentPackageResource(DeploymentPackage deploymentPackage) {
        super(DEPLOY_PATH.addElements(deploymentPackage.getName()));
        this.m_deploymentPackage = deploymentPackage;
        m_bundleInfos = m_deploymentPackage.getBundleInfos();

        setRelations(new DefaultRelation(getPath(), Action.DELETE, "uninstall",
                new DefaultParameter()
                        .name("forced")
                        .description("uninstall is forced or not")
                        .type(Boolean.class)
                        .optional(true)));
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        metadataBuilder.set("Name", m_deploymentPackage.getName());
        metadataBuilder.set("DisplayName", m_deploymentPackage.getDisplayName());
        metadataBuilder.set("Version", m_deploymentPackage.getVersion());
        metadataBuilder.set("isStale", m_deploymentPackage.isStale());
        //TODO add some metadata from headers, resources and bundleinfo
        return metadataBuilder.build();
    }

    @Override
    public List<Resource> getResources() {
        ArrayList<Resource> resources = new ArrayList<Resource>();
        if (m_bundleInfos != null) {
            Bundle[] bundles = new Bundle[m_bundleInfos.length];
            for (BundleInfo bundleInfo : m_bundleInfos) {
                String symbolicName = bundleInfo.getSymbolicName();
                Bundle bundle = m_deploymentPackage.getBundle(symbolicName);
                if (bundle != null) {
                    resources.add(new BundleRelationsResource(getPath().addElements("bundles"), bundles));
                }
            }
        }
        return resources;
    }

    @Override
    public Resource delete(Request request) throws IllegalActionOnResourceException {
        boolean forced = request.get("forced", Boolean.class);
        try {
            if (forced) {
                this.m_deploymentPackage.uninstallForced();
            } else {
                this.m_deploymentPackage.uninstall();
            }
        } catch (DeploymentException e) {
            throw new RuntimeException(e.getMessage());
        }

        //TODO should return some empty resource
        return this;
    }

}
