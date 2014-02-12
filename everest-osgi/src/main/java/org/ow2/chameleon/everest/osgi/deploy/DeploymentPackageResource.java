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
import org.ow2.chameleon.everest.impl.DefaultResource;
import org.ow2.chameleon.everest.impl.ImmutableResourceMetadata;
import org.ow2.chameleon.everest.osgi.bundle.BundleResourceManager;
import org.ow2.chameleon.everest.services.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.osgi.service.deploymentadmin.BundleInfo;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;

import java.util.ArrayList;
import java.util.List;

import static org.ow2.chameleon.everest.osgi.deploy.DeploymentAdminResourceManager.DEPLOY_PATH;

/**
 * Resource representing a {@code DeploymentPackage}.
 */
public class DeploymentPackageResource extends DefaultResource<DeploymentPackage> {

    /**
     * Represented deployment package
     */
    private final DeploymentPackage m_deploymentPackage;

    /**
     * Bundle info contained in the deployment package
     */
    private final BundleInfo[] m_bundleInfos;

    /**
     * Constructor for deployment package resource
     *
     * @param deploymentPackage
     */
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
    public List<Resource<?>> getResources() {
        ArrayList<Resource<?>> resources = new ArrayList<Resource<?>>();
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
