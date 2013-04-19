package org.apache.felix.ipojo.everest.osgi;

import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Resource;
import org.osgi.service.deploymentadmin.DeploymentAdmin;

import static org.apache.felix.ipojo.everest.osgi.OsgiRootResource.OSGI_ROOT_PATH;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/19/13
 * Time: 10:56 AM
 */
public class DeploymentAdminResourceManager extends DefaultResource {

    public static final String DEPLOY_ROOT_NAME = "deployments";

    public static final Path DEPLOY_PATH = OSGI_ROOT_PATH.add(Path.from(Path.SEPARATOR + DEPLOY_ROOT_NAME));

    public DeploymentAdminResourceManager(DeploymentAdmin deploymentAdmin) {
         super(DEPLOY_PATH);
    }
}
