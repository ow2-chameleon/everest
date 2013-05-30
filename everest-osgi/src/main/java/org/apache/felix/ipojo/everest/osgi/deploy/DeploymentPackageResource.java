package org.apache.felix.ipojo.everest.osgi.deploy;

import org.apache.felix.ipojo.everest.impl.DefaultParameter;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.osgi.bundle.BundleResourceManager;
import org.apache.felix.ipojo.everest.services.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
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
            ArrayList<Bundle> bundles = new ArrayList<Bundle>();
            for (BundleInfo bundleInfo : m_bundleInfos) {
                Bundle bundle = m_deploymentPackage.getBundle(bundleInfo.getSymbolicName());
                bundles.add(bundle);
            }
            Builder builder = BundleResourceManager.relationsBuilder(getPath().addElements("bundles"), bundles);
            try {
                resources.add(builder.build());
            } catch (IllegalResourceException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return resources;
    }

    @Override
    public Resource delete(Request request) throws IllegalActionOnResourceException {
        boolean forced = request.get("forced", Boolean.class);
        try {
            this.uninstall(forced);
        } catch (IllegalActionOnResourceException e) {
            throw new IllegalActionOnResourceException(request, e.getMessage());
        }
        //TODO should return some empty resource
        return this;
    }

    @Override
    public <A> A adaptTo(Class<A> clazz) {
        if (DeploymentPackage.class.equals(clazz)) {
            return (A) m_deploymentPackage;
        } else if (DeploymentPackageResource.class.equals(clazz)) {
            return (A) this;
        } else {
            return null;
        }
    }

    public String getDisplayName() {
        return m_deploymentPackage.getDisplayName();
    }

    public String getName() {
        return m_deploymentPackage.getName();
    }

    public Version getVersion() {
        return m_deploymentPackage.getVersion();
    }

    public String[] getPackageResources() {
        return m_deploymentPackage.getResources();
    }

    public boolean isStale() {
        return m_deploymentPackage.isStale();
    }

    public void uninstall(boolean forced) throws IllegalActionOnResourceException {
        try {
            if (forced) {
                m_deploymentPackage.uninstallForced();
            } else {
                m_deploymentPackage.uninstall();
            }
        } catch (DeploymentException e) {
            throw new IllegalActionOnResourceException(null, e.getMessage());
        }
    }

}
