package org.apache.ipojo.everest.fileSystem;


import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.services.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.apache.felix.ipojo.everest.core.Everest.DEBUG_REQUEST;

/**
 * Abstract class for resources with child resources.
 * Creates {@code Action.GET} relations to these children
 */
public abstract class AbstractResourceCollection extends DefaultReadOnlyResource {


    public List<DirectoryResource> m_subDirectoryResource = new ArrayList<DirectoryResource>();

    public List<FileResource> m_subFileResource = new ArrayList<FileResource>();

    /**
     * Constructor, same as {@code DefaultReadOnlyResource}
     *
     * @param path path of the resource
     */
    public AbstractResourceCollection(Path path) {
        super(path);
    }


    @Override
    public Resource process(Request request) throws IllegalActionOnResourceException, ResourceNotFoundException {

        String debug = System.getProperty(DEBUG_REQUEST);
        // 1) Substract our path from the request path.
        Path rel = request.path().subtract(this.getPath());
        // 2) The request is targeting us...
        if (request.path().equals(getPath())) {
            switch (request.action()) {
                case READ:
                    return read(request);
                case DELETE:
                    return delete(request);
                case CREATE:
                    return create(request);
                case UPDATE:
                    return update(request);
            }
            return null;
        }

        boolean isDirectory = true;
        File tempFileObject = null;

        System.out.println("Path " + request.path().subtract(FileSystemRootResource.getInstance().getPath()).toString());
        tempFileObject = new File(request.path().subtract(FileSystemRootResource.getInstance().getPath()).toString());
        if (!(tempFileObject.exists())) {
            System.out.println("FICHIER FINAL EXIST PAS ");
            throw new ResourceNotFoundException(request);
        }


        File subResource = null;

        subResource = new File(getPath().add(Path.from(Path.SEPARATOR + rel.getFirst())).subtract(FileSystemRootResource.getInstance().getPath()).toString());
        if (subResource.isFile()) {
            System.out.println("FICHIER INTER EST UN FICHIER ");

            isDirectory = false;
        }


        Path path = getPath().add(Path.fromElements(rel.getFirst()));

        for (Resource resource : getResources()) {
            if (resource.getPath().equals(path)) {
                return resource.process(request);
            }
        }

        if (isDirectory) {
            DirectoryResource tempDirectory = new DirectoryResource(rel.getFirst(), getPath(), subResource);
            m_subDirectoryResource.add(tempDirectory);
            return m_subDirectoryResource.get(m_subDirectoryResource.size() - 1).process(request);
        } else {

            FileResource tempFile = new FileResource(rel.getFirst(), getPath(), subResource);
            m_subFileResource.add(tempFile);
            return m_subFileResource.get(m_subFileResource.size() - 1).process(request);

        }

    }

    @Override
    public List<Resource> getResources() {
        List<Resource> resourceList = new ArrayList<Resource>();
        for (Resource resource : getM_subDirectoryResource()) {
            resourceList.add(resource);
        }
        for (Resource resource : getM_subFileResource()) {
            resourceList.add(resource);
        }
        return resourceList;

    }

    public List<DirectoryResource> getM_subDirectoryResource() {
        return m_subDirectoryResource;
    }

    public List<FileResource> getM_subFileResource() {
        return m_subFileResource;
    }


}