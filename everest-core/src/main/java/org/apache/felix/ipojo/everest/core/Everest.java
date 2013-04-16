package org.apache.felix.ipojo.everest.core;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.everest.impl.DefaultRequest;
import org.apache.felix.ipojo.everest.services.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Everest Core.
 */
@Component
@Instantiate
public class Everest {

    private List<ResourceManager> managers = new ArrayList<ResourceManager>();

    @Bind(optional = true, aggregate = true)
    public void bindResourceManager(ResourceManager manager) {
        synchronized (this) {
            managers.add(manager);
        }
    }

    @Unbind
    public void unbindResourceManager(ResourceManager manager) {
        synchronized (this) {
            managers.remove(manager);
        }
    }

    public Resource process(Request request) throws NotManagedRequestException, ResourceNotFoundException, IllegalActionOnResourceException {
        List<ResourceManager> managerList;
        synchronized (this) {
            managerList = new ArrayList<ResourceManager>(managers);
        }

        // Detect the right manager by prefix.
        String path = request.path();
        path = normalize(path);

        Request delegatedRequest = DefaultRequest.createNormalizedRequest(request, path);
        for (ResourceManager manager : managerList) {
            if (path.startsWith(manager.getName())) {
                return manager.process(delegatedRequest);
            }
        }

        throw new NotManagedRequestException(request);

    }

    private String normalize(String path) {
        if (path.startsWith("/")) {
            return path.substring(1);
        }

        if (path.startsWith("everest://")) {
            return path.substring("everest://".length());
        }

        if (path.startsWith("everest:/")) {
            return path.substring("everest:/".length());
        }

        if (path.startsWith("everest:")) {
            return path.substring("everest:".length());
        }

        // Unchanged path.
        return path;
    }

    public synchronized List<ResourceManager> getResourceManagers() {
        return new ArrayList<ResourceManager>(managers);
    }
}
