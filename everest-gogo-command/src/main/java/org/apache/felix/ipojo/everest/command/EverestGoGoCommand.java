package org.apache.felix.ipojo.everest.command;/*
 * User: Colin
 * Date: 19/07/13
 * Time: 14:12
 * 
 */

import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.ipojo.everest.client.api.EverestClient;
import org.apache.felix.ipojo.everest.services.*;
import org.apache.felix.service.command.Descriptor;

@Component(immediate = true)
@Instantiate
@Provides(specifications = EverestGoGoCommand.class)
public class EverestGoGoCommand {

    /**
     * Defines the command scope (rondo).
     */
    @ServiceProperty(name = "osgi.command.scope", value = "everest")
    String m_scope;

    /**
     * Defines the functions (commands).
     */
    @ServiceProperty(name = "osgi.command.function", value = "{}")
    String[] m_function = new String[]{"create", "retrieve", "update", "delete"};


    @Requires(optional = false)
    EverestService m_everest;

    EverestClient m_everestClient;

    @Validate
    public void start() {
        try {
            m_everestClient = new EverestClient(m_everest);
        } catch (ResourceNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalActionOnResourceException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    @Descriptor("create a Resource")
    public void create(@Descriptor("create") String... handleId) {
        String bufferOut = new String();
        try {
            String path;
            if (handleId.length < 1) {

                bufferOut = bufferOut + " Error : Need At least 1 Arguments";
            } else {
                path = handleId[0];
                m_everestClient.create(path);
                for (int i = 1; i < handleId.length; i++) {
                    if ((i % 2) == 0) {
                        m_everestClient.with(handleId[i - 1], handleId[i]);
                    }
                }

                Resource resource = m_everestClient.doIt().retrieve();
                if (!(resource == null)) {
                    ResourceMetadata resourceMetadata = resource.getMetadata();
                    for (String currentString : resourceMetadata.keySet()) {
                        bufferOut = bufferOut + currentString + " : \"" + resourceMetadata.get(currentString) + "\"" + "\n";
                    }
                } else {
                    bufferOut = bufferOut + "Fail creation ";
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
            bufferOut = null;
        }

        System.out.println(bufferOut);
    }

    @Descriptor("retrieve a Resource")
    public void retrieve(@Descriptor("retrieve") String... handleId) {
        String bufferOut = new String();
        try {
            String path;
            if (handleId.length == 0) {
                bufferOut = bufferOut + "Error : Must have at least 1 argument \n";
            } else if (handleId.length == 1) {
                Resource resource;
                path = handleId[0];
                resource = m_everestClient.read(path).retrieve();
                bufferOut = bufferOut + "Name : " + resource.getPath().getLast().toString() + "\n";
                bufferOut = bufferOut + "\nMETADATA : \n";
                ResourceMetadata resourceMetadata = resource.getMetadata();
                for (String currentString : resourceMetadata.keySet()) {
                    bufferOut = bufferOut + currentString + " : \"" + resourceMetadata.get(currentString) + "\"" + "\n";
                }

            } else {
                path = handleId[0];
                Resource resource = m_everestClient.read(path).retrieve();
                bufferOut = bufferOut + "Name : " + resource.getPath().getLast().toString() + "\n";
                bufferOut = bufferOut + "\nMETADATA\n";
                for (String currentString : handleId) {
                    if (!(currentString.equalsIgnoreCase(handleId[0]))) {
                        bufferOut = bufferOut + currentString + " : \"" + m_everestClient.read(path).retrieve(currentString) + "\"" + "\n";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            bufferOut = null;
        }

        System.out.println(bufferOut);


    }

    @Descriptor("update a Resource")
    public void update(@Descriptor("update") String... handleId) {
        String bufferOut = new String();
        try {
            String path;
            if (handleId.length < 3) {

                bufferOut = bufferOut + " Error : Need At least 3 Arguments";
            } else {
                path = handleId[0];
                m_everestClient.update(path);
                for (int i = 1; i < handleId.length; i++) {
                    if ((i % 2) == 0) {
                        m_everestClient.with(handleId[i - 1], handleId[i]);
                    }
                }
                Resource resource = m_everestClient.doIt().retrieve();
                bufferOut = bufferOut + "Success : Update of " + resource.getPath().getLast().toString() + " at " + resource.getPath().toString() + "\n";
                bufferOut = bufferOut + "\nMETADATA : \n";
                ResourceMetadata resourceMetadata = resource.getMetadata();
                for (String currentString : resourceMetadata.keySet()) {
                    bufferOut = bufferOut + currentString + " : \"" + resourceMetadata.get(currentString) + "\"" + "\n";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            bufferOut = null;
        }

        System.out.println(bufferOut);


    }

    @Descriptor("delete a Resource")
    public void delete(@Descriptor("delete") String... handleId) throws ResourceNotFoundException, IllegalActionOnResourceException {
        String bufferOut = new String();
        try {
            String path;
            if (handleId.length == 0) {
                bufferOut = bufferOut + "Error : Must have at least 1 argument \n";
            } else {

                for (String current : handleId) {
                    path = current;
                    m_everestClient.delete(path);
                    bufferOut = bufferOut + "Success : The resource at " + path + " have been destroy\n";
                }
            }
        } catch (Exception e) {
            System.out.println(bufferOut);
            e.printStackTrace();
            bufferOut = null;
        }

        System.out.println(bufferOut);


    }

}
