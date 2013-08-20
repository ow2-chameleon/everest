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
 * Time: 13:15
 * 
 */


import org.ow2.chameleon.everest.impl.DefaultParameter;
import org.ow2.chameleon.everest.impl.DefaultRelation;
import org.ow2.chameleon.everest.impl.ImmutableResourceMetadata;
import org.ow2.chameleon.everest.services.*;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class DirectoryResource extends AbstractResourceCollection {

    /**
     * Name of the file system root resource
     */
    public final String m_directoryName;


    /**
     * Path to File System root : "/virtualFileSystem"
     */
    public final Path m_directoryPath;

    /**
     * Represented File
     */

    public final AbstractResourceCollection m_parent;


    public DirectoryResource(String name, Path parentPath, File fileObject,AbstractResourceCollection parent) {
        super(parentPath.add(Path.from(Path.SEPARATOR + name)));
        m_directoryName = name;
        m_directoryPath = parentPath;
        m_representedFile = fileObject;
        m_parent = parent;
        List<Relation> relations = new ArrayList<Relation>();
        relations = getRelations();
        relations.add(new DefaultRelation(getPath(), Action.DELETE, "Delete:Directory"));

        setRelations(relations);


    }

    @Override
    public Resource delete(Request request) {
        this.deleteDirectory(this.m_representedFile);
        m_parent.deleteSubDirectory(this);
        return m_parent;
    }







}
