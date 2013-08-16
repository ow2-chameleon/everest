package org.apache.ipojo.everest.fileSystem;/*
 * User: Colin
 * Date: 12/08/13
 * Time: 11:11
 * 
 */


import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;

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

    /**
     * Represented File
     */

    public File m_representedFile;

    public static FileSystemRootResource getInstance() {
        return m_instance;
    }

    public FileSystemRootResource() {
        super(m_fileSystemPath);

        m_representedFile = new File("/");


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
}
