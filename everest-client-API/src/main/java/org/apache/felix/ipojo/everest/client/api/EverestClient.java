package org.apache.felix.ipojo.everest.client.api;

import org.apache.felix.ipojo.everest.impl.DefaultRequest;
import org.apache.felix.ipojo.everest.services.*;


/*
 * User: Colin
 * Date: 17/07/13
 * Time: 14:22
 *
 */
public class EverestClient extends ResourceContainer {

    public static EverestService m_everest;

    public Path m_currentPath;

    public EverestClient(EverestService m_everest) throws ResourceNotFoundException, IllegalActionOnResourceException {
        super(m_everest.process(new DefaultRequest(Action.READ, Path.from("/"), null)));
        this.m_everest = m_everest;
        m_currentPath = m_resource.getPath();
    }

    public ResourceContainer read(String path) throws ResourceNotFoundException, IllegalActionOnResourceException {
        this.m_resource = m_everest.process(new DefaultRequest(Action.READ, Path.from(path), null));
        return this;// new ResourceContainer(m_everest.process(new DefaultRequest(Action.READ, Path.from(path), null)));
    }

    public synchronized ResourceContainer update(String path) throws ResourceNotFoundException, IllegalActionOnResourceException {
        m_currentPath = Path.from(path);
        m_currentAction = Action.UPDATE;
        return this;
    }


    public synchronized ResourceContainer create(String path) throws ResourceNotFoundException, IllegalActionOnResourceException {
        m_currentPath = Path.from(path);
        m_currentAction = Action.CREATE;
        return this;
    }


    public synchronized ResourceContainer delete(String path) throws ResourceNotFoundException, IllegalActionOnResourceException {
        m_currentPath = Path.from(path);
        m_currentAction = Action.DELETE;
        return this;

    }

    @Override
    public synchronized ResourceContainer doIt() throws ResourceNotFoundException, IllegalActionOnResourceException {
        return new ResourceContainer(EverestClient.m_everest.process(new DefaultRequest(m_currentAction, m_currentPath.getParent(), m_currentParams)));
    }

}
