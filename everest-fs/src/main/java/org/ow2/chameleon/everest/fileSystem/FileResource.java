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

package org.ow2.chameleon.everest.fileSystem;/*
 * User: Colin
 * Date: 12/08/13
 * Time: 13:21
 * 
 */


import org.ow2.chameleon.everest.impl.DefaultParameter;
import org.ow2.chameleon.everest.impl.DefaultRelation;
import org.ow2.chameleon.everest.impl.DefaultResource;
import org.ow2.chameleon.everest.impl.ImmutableResourceMetadata;
import org.ow2.chameleon.everest.services.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class FileResource extends DefaultResource {

    /**
     * Name of the file system root resource
     */
    public final String m_fileName;


    /**
     * Path to File System root : "/virtualFileSystem"
     */
    public final Path m_filePath;


    /**
     * Represented File
     */

    public final File m_representedFile;

    /**
     * Represented File
     */

    public final AbstractResourceCollection m_parent;

    public FileResource(String name, Path parentPath, File fileObject,AbstractResourceCollection parent) {
        super(parentPath.add(Path.from(Path.SEPARATOR + name)));
        m_fileName = name;
        m_filePath = parentPath;
        m_representedFile = fileObject;
        m_parent = parent;
        List<Relation> relations = new ArrayList<Relation>();

        relations.add(new DefaultRelation(getPath(), Action.UPDATE, "Update:Permission",
                new DefaultParameter()
                        .name("Permission")
                        .description("Update  permission on a file, $permission/$value , permission is readable & writable & executable,  value is true or false")
                        .optional(false)
                        .type(Map.class)));
        relations.add(new DefaultRelation(getPath(), Action.DELETE, "Delete:File"));
        setRelations(relations);

    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();

        metadataBuilder.set("name", m_representedFile.getName());
        metadataBuilder.set("URI", m_representedFile.toURI());
        metadataBuilder.set("path", m_representedFile.getPath());


        if (m_representedFile.isFile()) {
            metadataBuilder.set("type", "FILE");
        } else {
            metadataBuilder.set("type", "DIRECTORY");
        }
        metadataBuilder.set("hidden", getFileExtension(m_representedFile));

        metadataBuilder.set("readable", m_representedFile.canRead());

        metadataBuilder.set("writable", m_representedFile.canWrite());

        metadataBuilder.set("hidden", m_representedFile.isHidden());

        try {
            // check if the file contains the bundle with correct manifest
            JarFile jarFile = new JarFile(m_representedFile);
            Manifest manifest = jarFile.getManifest();
            Attributes mainAttributes = manifest.getMainAttributes();
            metadataBuilder.set("manifest",manifest);
        }catch (Exception e) {
        }
        return metadataBuilder.build();


    }

    @Override
    public Resource update(Request request) {
        Map<String, ?> newMap = request.parameters();
        if (newMap != null) {
            for (String key : newMap.keySet()) {
                if (key.equalsIgnoreCase("writable")){
                    if (newMap.get(key).toString().equalsIgnoreCase("true")){
                        m_representedFile.setWritable(true);
                    }else if (newMap.get(key).toString().equalsIgnoreCase("false")){
                        m_representedFile.setWritable(false);
                    }

                }else if (key.equalsIgnoreCase("readable")){
                    if (newMap.get(key).toString().equalsIgnoreCase("true")){
                        m_representedFile.setReadable(true);
                    }else if (newMap.get(key).toString().equalsIgnoreCase("false")){
                        m_representedFile.setReadable(false);
                    }
                }else if (key.equalsIgnoreCase("executable")){
                    if (newMap.get(key).toString().equalsIgnoreCase("true")){
                        m_representedFile.setExecutable(true);
                    }else if (newMap.get(key).toString().equalsIgnoreCase("false")){
                        m_representedFile.setExecutable(false);
                    }
                }

            }
        }
        return this;

    }

    private  String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }

    @Override
    public Resource delete(Request request) throws IllegalActionOnResourceException {
        m_parent.deleteSubFile(this);
        if (m_representedFile.delete()){
            return m_parent;

        }else{
            throw new IllegalActionOnResourceException(request,this);
        }
    }
}
