package org.apache.felix.ipojo.everest.client.api;

import org.apache.felix.ipojo.everest.impl.DefaultRequest;
import org.apache.felix.ipojo.everest.services.*;


/*
 * User: Colin
 * Date: 17/07/13
 * Time: 14:22
 *
 */
public class EverestClientApi {

    public static EverestService m_everest;


    public EverestClientApi(EverestService m_everest) {
        this.m_everest = m_everest;
    }

    public ResourceContainer read(String path) throws ResourceNotFoundException, IllegalActionOnResourceException {

        return new ResourceContainer(m_everest.process(new DefaultRequest(Action.READ, Path.from(path), null)));

    }
}
