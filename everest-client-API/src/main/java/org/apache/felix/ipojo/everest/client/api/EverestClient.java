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


    public Path getM_currentPath() {
        return m_currentPath;
    }

    public EverestClient(EverestService m_everest) throws ResourceNotFoundException, IllegalActionOnResourceException {
        super(m_everest.process(new DefaultRequest(Action.READ, Path.from("/"), null)));
        this.m_everest = m_everest;
        m_currentPath = m_resource.getPath();
    }

    public ResourceContainer read(String path) throws ResourceNotFoundException, IllegalActionOnResourceException {
        this.m_resource = m_everest.process(new DefaultRequest(Action.READ, Path.from(path), null));
        m_currentPath = m_resource.getPath();
        m_currentAction = Action.READ;
        m_currentParams.clear();
        return this; //new ResourceContainer(m_everest.process(new DefaultRequest(Action.READ, Path.from(path), null)));
    }

    public synchronized ResourceContainer update(String path) throws ResourceNotFoundException, IllegalActionOnResourceException {
        m_currentPath = Path.from(path);
        m_currentAction = Action.UPDATE;
        m_currentParams.clear();
        return this;
    }

    public synchronized ResourceContainer update(Resource resource) throws ResourceNotFoundException, IllegalActionOnResourceException {
        m_currentPath = resource.getPath();
        m_currentAction = Action.UPDATE;
        m_currentParams.clear();
        return this;
    }


    public synchronized ResourceContainer create(String path) throws ResourceNotFoundException, IllegalActionOnResourceException {
        m_currentPath = Path.from(path);
        m_currentAction = Action.CREATE;
        m_currentParams.clear();
        return this;
    }

    public synchronized ResourceContainer create(Resource resource) throws ResourceNotFoundException, IllegalActionOnResourceException {
        m_currentPath = resource.getPath();
        m_currentAction = Action.CREATE;
        m_currentParams.clear();
        return this;
    }


    public synchronized ResourceContainer delete(String path) throws ResourceNotFoundException, IllegalActionOnResourceException {
        m_currentPath = Path.from(path);
        m_currentAction = Action.DELETE;
        m_currentParams.clear();
        return this;

    }

    public synchronized ResourceContainer delete(Resource resource) throws ResourceNotFoundException, IllegalActionOnResourceException {
        m_currentPath = resource.getPath();
        m_currentAction = Action.DELETE;
        m_currentParams.clear();
        return this;
    }

    @Override
    public synchronized ResourceContainer doIt() throws ResourceNotFoundException, IllegalActionOnResourceException {
        return new ResourceContainer(EverestClient.m_everest.process(new DefaultRequest(m_currentAction, m_currentPath, m_currentParams)));
    }

}
