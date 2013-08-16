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

package org.apache.ipojo.everest.fileSystem;/*
 * User: Colin
 * Date: 12/08/13
 * Time: 13:21
 * 
 */


import org.ow2.chameleon.everest.impl.DefaultResource;
import org.ow2.chameleon.everest.impl.ImmutableResourceMetadata;
import org.ow2.chameleon.everest.services.Path;
import org.ow2.chameleon.everest.services.ResourceMetadata;

import java.io.File;

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


    public FileResource(String name, Path parentPath, File fileObject) {
        super(parentPath.add(Path.from(Path.SEPARATOR + name)));
        m_fileName = name;
        m_filePath = parentPath;
        m_representedFile = fileObject;
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

        metadataBuilder.set("readable", m_representedFile.canRead());

        metadataBuilder.set("writable", m_representedFile.canWrite());

        metadataBuilder.set("hidden", m_representedFile.isHidden());


        return metadataBuilder.build();


    }

}
