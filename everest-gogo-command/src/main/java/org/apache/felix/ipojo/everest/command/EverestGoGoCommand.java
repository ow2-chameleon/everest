package org.apache.felix.ipojo.everest.command;/*
 * User: Colin
 * Date: 19/07/13
 * Time: 14:12
 * 
 */

import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.ipojo.everest.client.api.EverestClient;
import org.apache.felix.ipojo.everest.services.EverestService;
import org.apache.felix.ipojo.everest.services.IllegalActionOnResourceException;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.services.ResourceNotFoundException;
import org.apache.felix.service.command.Descriptor;
import org.osgi.service.component.annotations.Component;

@Component(immediate = true)
@Instantiate
@Provides

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

    @Requires(optional = false)
    EverestClient m_everestClient;


    @Bind
    public void bindDeploymentHandle() {
        try {
            m_everestClient = new EverestClient(m_everest);
        } catch (ResourceNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalActionOnResourceException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    @Unbind
    public void unbindDeploymentHandle() {

    }

    @Descriptor("create a Resource")
    public void create(@Descriptor("create") String handleId) {

    }

    @Descriptor("retrieve a Resource")
    public Resource retrieve(@Descriptor("retrieve") String handleId) throws ResourceNotFoundException, IllegalActionOnResourceException {
        return m_everestClient.read(handleId).retrieve();
    }

    @Descriptor("retrieve a Resource")
    public Resource retrieve(@Descriptor("retrieve") String... handleId) throws ResourceNotFoundException, IllegalActionOnResourceException {
        String path;
        if (handleId.length == 0) {
            return m_everestClient.retrieve();
        } else {
            path = handleId[0];

        }


    }

    @Descriptor("update a Resource")
    public void update(@Descriptor("update") String handleId) {

    }

    @Descriptor("delete a Resource")
    public void delete(@Descriptor("delete") String handleId) {

    }

}
