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
    public void create(@Descriptor("create") String handleId) {

    }

    @Descriptor("retrieve a Resource")
    public void retrieve(@Descriptor("retrieve") String... handleId) {
        String bufferOut = new String();
        try {
            String path;
            if (handleId.length == 0) {

            } else if (handleId.length == 1) {
                Resource resource;
                path = handleId[0];
                resource = m_everestClient.read(path).retrieve();
                bufferOut = bufferOut + "Name : " + resource.getPath().getLast().toString() + "\n";
                bufferOut = bufferOut + "\nMETADATA\n";
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
                        System.out.println("KEY" + handleId[i - 1] + " VALUE " + handleId[i]);
                        m_everestClient.with(handleId[i - 1], handleId[i]);
                    }
                }
                System.out.println(m_everestClient.getM_currentPath().toString());
                System.out.println(m_everestClient.getM_currentAction());
                System.out.println(m_everestClient.getM_currentParams());

                Resource resource = m_everestClient.doIt().retrieve();
                bufferOut = bufferOut + "Name : " + resource.getPath().getLast().toString() + "\n";
                bufferOut = bufferOut + "\nMETADATA\n";
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
    public void delete(@Descriptor("delete") String handleId) throws ResourceNotFoundException, IllegalActionOnResourceException {
        m_everestClient.delete(handleId).doIt();
    }

}
