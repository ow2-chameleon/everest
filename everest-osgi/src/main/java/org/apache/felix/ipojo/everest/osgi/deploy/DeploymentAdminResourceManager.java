package org.apache.felix.ipojo.everest.osgi.deploy;

import org.apache.felix.ipojo.everest.impl.DefaultParameter;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.osgi.AbstractResourceCollection;
import org.apache.felix.ipojo.everest.services.*;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.apache.felix.ipojo.everest.osgi.OsgiRootResource.OSGI_ROOT_PATH;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/19/13
 * Time: 10:56 AM
 */
public class DeploymentAdminResourceManager extends AbstractResourceCollection {

    public static final String DEPLOY_ROOT_NAME = "deployments";

    public static final Path DEPLOY_PATH = OSGI_ROOT_PATH.add(Path.from(Path.SEPARATOR + DEPLOY_ROOT_NAME));

    private final DeploymentAdmin m_deploymentAdmin;

    public DeploymentAdminResourceManager(DeploymentAdmin deploymentAdmin) {
        super(DEPLOY_PATH);
        this.m_deploymentAdmin = deploymentAdmin;
        setRelations(new DefaultRelation(getPath(), Action.CREATE, "install",
                new DefaultParameter()
                        .name("input")
                        .description("input stream")
                        .type(InputStream.class)
                        .optional(false)));
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        DeploymentPackage[] deploymentPackages = m_deploymentAdmin.listDeploymentPackages();
        if (deploymentPackages != null) {
            for (DeploymentPackage deploymentPackage : deploymentPackages) {
                metadataBuilder.set("", deploymentPackage.getDisplayName());
            }
        }
        return metadataBuilder.build();
    }

    @Override
    public List<Resource> getResources() {
        ArrayList<Resource> resources = new ArrayList<Resource>();
        DeploymentPackage[] deploymentPackages = m_deploymentAdmin.listDeploymentPackages();
        if (deploymentPackages != null) {
            for (DeploymentPackage deploymentPackage : deploymentPackages) {
                resources.add(new DeploymentPackageResource(deploymentPackage));
            }
        }
        return resources;
    }

    @Override
    public Resource create(Request request) throws IllegalActionOnResourceException {
        try {
            InputStream input = request.get("input", InputStream.class);
            DeploymentPackage deploymentPackage = m_deploymentAdmin.installDeploymentPackage(input);
            return new DeploymentPackageResource(deploymentPackage);
        } catch (DeploymentException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
