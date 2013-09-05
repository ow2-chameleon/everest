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

package org.ow2.chameleon.everest.fileSystem;


import org.ow2.chameleon.everest.impl.DefaultParameter;
import org.ow2.chameleon.everest.impl.DefaultReadOnlyResource;
import org.ow2.chameleon.everest.impl.DefaultRelation;
import org.ow2.chameleon.everest.impl.ImmutableResourceMetadata;
import org.ow2.chameleon.everest.services.*;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;


/**
 * Abstract class for resources with child resources.
 * Creates {@code Action.GET} relations to these children
 */
public abstract class AbstractResourceCollection extends DefaultReadOnlyResource {


    public List<DirectoryResource> m_subDirectoryResource = new ArrayList<DirectoryResource>();

    public List<FileResource> m_subFileResource = new ArrayList<FileResource>();

    /**
     * Represented File
     */

    public File m_representedFile;

    /**
     * Constructor, same as {@code DefaultReadOnlyResource}
     *
     * @param path path of the resource
     */
    public AbstractResourceCollection(Path path) {
        super(path);
        ArrayList<Relation> relations = new ArrayList<Relation>();

        relations.add(new DefaultRelation(getPath(), Action.CREATE, "Create:Ref",
                new DefaultParameter()
                        .name("DescriptionFile")
                        .description("Create file or folder, Must contain : name/$name,type/$type")
                        .optional(false)
                        .type(Map.class)));
        relations.add(new DefaultRelation(getPath(), Action.CREATE, "Create:Url",
                new DefaultParameter()
                        .name("DescriptionFile")
                        .description("Create file, Must contain : url/$url")
                        .optional(false)
                        .type(Map.class)));
        setRelations(relations);
    }


    @Override
    public Resource process(Request request) throws IllegalActionOnResourceException, ResourceNotFoundException {

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

        tempFileObject = new File(request.path().subtract(FileSystemRootResource.getInstance().getPath()).toString());
        if (!(tempFileObject.exists())) {
            throw new ResourceNotFoundException(request);
        }


        File subResource = null;

        subResource = new File(getPath().add(Path.from(Path.SEPARATOR + rel.getFirst())).subtract(FileSystemRootResource.getInstance().getPath()).toString());
        if (subResource.isFile()) {
            isDirectory = false;
        }


        Path path = getPath().add(Path.fromElements(rel.getFirst()));

        for (Resource resource : getResources()) {
            if (resource.getPath().equals(path)) {
                return resource.process(request);
            }
        }

        if (isDirectory) {
            DirectoryResource tempDirectory = new DirectoryResource(rel.getFirst(), getPath(), subResource,this);
            m_subDirectoryResource.add(tempDirectory);
            return m_subDirectoryResource.get(m_subDirectoryResource.size() - 1).process(request);
        } else {

            FileResource tempFile = new FileResource(rel.getFirst(), getPath(), subResource,this);
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

    public void deleteSubFile(FileResource fileResource){
        m_subFileResource.remove(fileResource);
    }

    public void deleteSubDirectory(DirectoryResource fileResource){
        m_subDirectoryResource.remove(fileResource);
    }


    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();


        metadataBuilder.set("name", m_representedFile.getName());
        metadataBuilder.set("URI", m_representedFile.toURI());
        metadataBuilder.set("path", m_representedFile.getPath());

        metadataBuilder.set("numberOfChild", m_representedFile.listFiles().length);
        List<String> listName = new ArrayList<String>();
        for (String currentName : m_representedFile.list()) {
            listName.add(currentName);
        }

        if (!listName.isEmpty()) {
            metadataBuilder.set("nameChild", listName);
        }

        if (m_representedFile.isFile()) {
            metadataBuilder.set("type", "FILE");
        } else {
            metadataBuilder.set("type", "DIRECTORY");
        }

        metadataBuilder.set("readable", m_representedFile.canRead());

        metadataBuilder.set("writable", m_representedFile.canWrite());

        metadataBuilder.set("hidden", m_representedFile.isHidden());


        return metadataBuilder.build();

    }

    @Override
    public Resource create(Request request) {
        String name = null;
        String type = null;
        String url = null;
        String checksum = null;
        Map<Object,Object> headers = null;
        Map<String, ?> newMap = request.parameters();
        if (newMap != null) {
            for (String key : newMap.keySet()) {
                if (key.equalsIgnoreCase("name")) {
                    name = newMap.get(key).toString();
                }
                if (key.equalsIgnoreCase("type")) {
                    type = newMap.get(key).toString();
                }
                if (key.equalsIgnoreCase("url")) {
                    url = newMap.get(key).toString();
                }
                if (key.equalsIgnoreCase("checksum")) {
                    checksum = newMap.get(key).toString();
                }
                if (key.equalsIgnoreCase("headers")) {
                    try
                    {
                        headers = (Map<Object,Object>) newMap.get(key);
                    }  catch (Exception e){
                       headers = null;
                    }
                }
            }

            if (url != null) {
                downloadAndVerifyBundle(m_representedFile, url,checksum,headers);
            }

            if (name != null && type != null) {
                if (type.equalsIgnoreCase("file")) {

                    File file = new File(getFilePath() + "/" + name);
                    try {
                        if (file.createNewFile()) {
                            FileResource fileResource = new FileResource(name, getPath(), file,this);
                            m_subFileResource.add(fileResource);
                            return fileResource;
                        } else {
                            return null;
                        }

                    } catch (IOException e) {
                        return null;
                    }

                } else if (type.equalsIgnoreCase("directory")) {
                    File file = new File(getFilePath() + "/" + name);


                    if (file.mkdir()) {
                        DirectoryResource directoryResource = new DirectoryResource(name, getPath(), file,this);
                        m_subDirectoryResource.add(directoryResource);
                        return directoryResource;
                    } else {
                        return null;
                    }


                } else {
                    return null;
                }

            }
        }
        return null;
    }

    public String getFilePath() {
        return m_representedFile.getPath();
    }

    private File downloadAndVerifyBundle(File directory, String urlString, String checksum,Map<Object,Object> headers) {
        File cachedFile = null;
        InputStream input = null;
        OutputStream output = null;
        try {
            // create file for cache and save url contents
            URL url = new URL(urlString);
            String fileName = calculateFileName(url);
            cachedFile = new File(directory, fileName);
            //FileUtils.copyURLToFile(url, cachedFile);

            input = url.openStream();
            copyInputStreamToFile(input, cachedFile);
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException e) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
        if (checksum!=null) {
            try {
                String fileChecksum = checksum(cachedFile, "SHA1");
                if (!fileChecksum.equals(checksum)) {
                    return null;
                }
            } catch (NoSuchAlgorithmException e) {
                // log
                System.out.println("Failed to find the checksum algorithm " + "SHA1");
            } catch (IOException e) {
                return null;
            }
        }

        try {
            // check if the file contains the bundle with correct manifest
            JarFile jarFile = new JarFile(cachedFile);
            Manifest manifest = jarFile.getManifest();
            Attributes mainAttributes = manifest.getMainAttributes();
            if(headers!=null){
                for (Map.Entry entry : headers.entrySet()) {
                    String keyString = entry.getKey().toString();
                    if (mainAttributes.getValue(keyString) != null) {
                        if (!mainAttributes.getValue(keyString).equals(headers.get(keyString))) {
                            // FAIL! should delete the downloaded file
                            deleteDirectory(directory);
                            //             FileUtils.deleteDirectory(directory);
                            return null;
                        }
                    }
                }
            }
            return cachedFile;
        }catch (IOException e) {
            // log
            return null;
        }
    }

    private String calculateFileName(URL url) {
        String urlString = url.toString();
        // lets look if url finishes with a filename
        String fileName = urlString.substring(urlString.lastIndexOf('/') + 1, urlString.length());
        if (!fileName.endsWith(".jar")) {
            //else we create our file id
            fileName = fileName.concat(".jar");
        }
        return fileName;
    }


    private void copyInputStreamToFile(final InputStream in, final File dest)
            throws IOException {
        copyInputStreamToOutputStream(in, new FileOutputStream(dest));
    }


    private void copyInputStreamToOutputStream(final InputStream in,
                                               final OutputStream out) throws IOException {
        try {
            try {
                final byte[] buffer = new byte[1024];
                int n;
                while ((n = in.read(buffer)) != -1)
                    out.write(buffer, 0, n);
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }



    protected   void deleteDirectory(File r) {
        if(r.isDirectory()){
            supprRepRecursive(r);
        }
        r.delete();
    }

    private  void supprRepRecursive(File r){
        File [] fileList = r.listFiles();
        for(int i = 0;i<fileList.length;i++){
            if(fileList[i].isDirectory() ){
                supprRepRecursive(fileList[i]);
                fileList[i].delete();
            }else{
                fileList[i].delete();
            }
        }
    }

    /**
     * Find the checksum of the file according to the algorithm name
     * @param file file
     * @param algo algorithm name
     * @return checksum
     * @throws NoSuchAlgorithmException if cannot find the algorithm
     * @throws IOException can occur while reading the given file
     */
    public  String checksum(File file, String algo) throws NoSuchAlgorithmException, IOException {
        MessageDigest sha = MessageDigest.getInstance(algo);
        FileInputStream fis = new FileInputStream(file);

        byte[] data = new byte[1024];
        int read = 0;
        while ((read = fis.read(data)) != -1) {
            sha.update(data, 0, read);
        }
        ;
        byte[] hashBytes = sha.digest();

        return checksumBytesToString(hashBytes);
    }

    /**
     * Converts a message digest byte array into a String based
     * on the hex values appearing in the array.
     */
    private  String checksumBytesToString(byte[] digestBytes) {
        StringBuffer hexString = new StringBuffer();
        for (int i=0; i<digestBytes.length; i++) {
            String hex=Integer.toHexString(0xff & digestBytes[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}