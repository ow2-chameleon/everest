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
 * Time: 11:11
 * 
 */


import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.ow2.chameleon.everest.impl.ImmutableResourceMetadata;
import org.ow2.chameleon.everest.services.Path;
import org.ow2.chameleon.everest.services.Resource;
import org.ow2.chameleon.everest.services.ResourceMetadata;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


@Component
@Provides(specifications = Resource.class)
@Instantiate
public class FileSystemRootResource extends AbstractResourceCollection {

    /**
     * Name of the file system root resource
     */
    public static final String m_fileSystemName = "fs";

    /**
     * Description of the file system root resource
     */
    public static final String FILE_SYSTEM_DESCRIPTION = "This root represents file System ";

    /**
     * Path to File System root : "/virtualFileSystem"
     */
    public static final Path m_fileSystemPath = Path.from(Path.SEPARATOR + m_fileSystemName);

    /**
     * Static instance of this singleton class
     */
    private static final FileSystemRootResource m_instance = new FileSystemRootResource();



    public static FileSystemRootResource getInstance() {
        return m_instance;
    }

    public FileSystemRootResource() {
        super(m_fileSystemPath);

        m_representedFile = new File("/");
    }



}
