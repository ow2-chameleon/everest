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

package org.ow2.chameleon.everest.osgi.deploy;

import org.ow2.chameleon.everest.impl.DefaultParameter;
import org.ow2.chameleon.everest.impl.DefaultRelation;
import org.ow2.chameleon.everest.impl.ImmutableResourceMetadata;
import org.ow2.chameleon.everest.osgi.AbstractResourceCollection;
import org.ow2.chameleon.everest.services.*;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.ow2.chameleon.everest.osgi.OsgiRootResource.OSGI_ROOT_PATH;

/**
 * Resource manager for Deployment Package Admin.
 */
public class DeploymentAdminResourceManager extends AbstractResourceCollection {

    /**
     * Name for deployment packages resource
     */
    public static final String DEPLOY_ROOT_NAME = "deployments";

    /**
     * Path for this resource : "/osgi/deployments"
     */
    public static final Path DEPLOY_PATH = OSGI_ROOT_PATH.add(Path.from(Path.SEPARATOR + DEPLOY_ROOT_NAME));


    /**
     * Relation name for install
     */
    private static final String INSTALL_PARAMETER = "install";

    /**
     * Parameter for input for install relation
     */
    private static final String INPUT_PARAMETER = "input";

    /**
     * Deployment admin service
     */
    private final DeploymentAdmin m_deploymentAdmin;

    /**
     * Constructor for deployment admin resource
     *
     * @param deploymentAdmin {@code DeploymentAdmin}
     */
    public DeploymentAdminResourceManager(DeploymentAdmin deploymentAdmin) {
        super(DEPLOY_PATH);
        this.m_deploymentAdmin = deploymentAdmin;
        setRelations(new DefaultRelation(getPath(), Action.CREATE, INSTALL_PARAMETER,
                new DefaultParameter()
                        .name(INPUT_PARAMETER)
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
            InputStream input = request.get(INPUT_PARAMETER, InputStream.class);
            DeploymentPackage deploymentPackage = m_deploymentAdmin.installDeploymentPackage(input);
            return new DeploymentPackageResource(deploymentPackage);
        } catch (DeploymentException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
