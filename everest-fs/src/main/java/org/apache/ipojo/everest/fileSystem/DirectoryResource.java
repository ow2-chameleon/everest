package org.apache.ipojo.everest.fileSystem;/*
 * User: Colin
 * Date: 12/08/13
 * Time: 13:15
 * 
 */


import org.apache.felix.ipojo.everest.impl.DefaultParameter;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.*;

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

    public final File m_representedFile;

    public DirectoryResource(String name, Path parentPath, File fileObject) {
        super(parentPath.add(Path.from(Path.SEPARATOR + name)));
        m_directoryName = name;
        m_directoryPath = parentPath;
        m_representedFile = fileObject;
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

    public String getFilePath() {
        return m_representedFile.getPath();
    }

    @Override
    public Resource create(Request request) {
        String name = null;
        String type = null;
        String url = null;
        Map<String, ?> newMap = request.parameters();
        if (newMap != null) {
            for (String key : newMap.keySet()) {
                if (key.equalsIgnoreCase("name")) {
                    name = newMap.get(key).toString();
                }
                if (key.equalsIgnoreCase("type")) {
                    type = newMap.get(key).toString();
                    System.out.println("Type" + type);
                }
                if (key.equalsIgnoreCase("url")) {
                    url = newMap.get(key).toString();
                    downloadAndVerifyBundle(m_representedFile, url);
                }
            }
            if (name == null) {
                return null;
            }
            if (type == null) {
                return null;
            }

            if (type.equalsIgnoreCase("file")) {

                File file = new File(getFilePath() + "/" + name);
                try {
                    if (file.createNewFile()) {
                        FileResource fileResource = new FileResource(name, getPath(), file);
                        m_subFileResource.add(fileResource);
                        return fileResource;
                    } else {
                        return null;
                    }

                } catch (IOException e) {
                    return null;
                }

            } else if (type.equalsIgnoreCase("folder")) {
                File file = new File(getFilePath() + "/" + name);


                if (file.mkdir()) {
                    DirectoryResource directoryResource = new DirectoryResource(name, getPath(), file);
                    m_subDirectoryResource.add(directoryResource);
                    return directoryResource;
                } else {
                    return null;
                }


            } else {
                return null;
            }

        }
        return null;

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

    private File downloadAndVerifyBundle(File directory, String urlString) {
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
        return cachedFile;
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

}
