package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.*;
import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.DefaultRequest;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.*;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Hashtable;

/**
 * '/ipojo/factory/$name/$version' resource, where $name stands for the name of a factory, and $version for its version.
 */
public class FactoryNameVersionResource extends DefaultReadOnlyResource {

    /**
     * The enclosing iPOJO resource.
     */
    private final IpojoResource m_ipojo;

    /**
     * The represented factory.
     */
    private final Factory m_factory;

    /**
     * Flag indicating if the underlying Factory service still exists.
     */
    private volatile boolean m_isStale = false;

    /**
     * The base immutable metadata of this resource.
     */
    private final ResourceMetadata m_baseMetadata;

    //TODO add relation to factory declaration
    //TODO add relation to instances created by this factory
    //TODO add relation to declaring bundle
    //TODO add relation to Factory service
    //TODO add relation to used handlers

    /**
     * @param factory the factory represented by this resource
     */
    @SuppressWarnings("deprecation")
    public FactoryNameVersionResource(IpojoResource ipojo, Factory factory) {
        super(canonicalPathOf(factory));
        m_ipojo = ipojo;
        m_factory = factory;
        // Build the immutable metadata of this factory.
        ImmutableResourceMetadata.Builder mb = new ImmutableResourceMetadata.Builder();
        mb.set("name", m_factory.getName()); // String
        mb.set("version", m_factory.getVersion()); // String
        mb.set("className", m_factory.getClassName()); // String, deprecated but who cares?
        m_baseMetadata = mb.build();
    }

    /**
     * Set this factory resource as stale. It happens when the underlying Factory service vanishes.
     */
    void setStale() {
        m_isStale = true;
    }

    @Override
    public synchronized ResourceMetadata getMetadata() {
        // Append mutable state to the immutable metadata.
        ImmutableResourceMetadata.Builder mb = new ImmutableResourceMetadata.Builder(m_baseMetadata);


        mb.set("state", stateAsString(m_factory.getState())); // String

        // Some factory getters miserably fail when the factory is stale.
        mb.set("missingHandlers", !m_isStale ? m_factory.getMissingHandlers() : Collections.emptyList()); // List<String>

        return mb.build();
    }

    private static Path canonicalPathOf(Factory f) {
        // Canonical path is '/ipojo/factory/$factoryName/$factoryVersion'
        // If m_version == null, then $factoryVersion is the literal 'null'
        return FactoriesResource.PATH.addElements(f.getName(), String.valueOf(f.getVersion()));
    }

    private static String stateAsString(int state) {
        switch (state) {
            case Factory.VALID:
                return "valid";
            case Factory.INVALID:
                return "invalid";
            default:
                return "unknown";
        }
    }

    @Override
    public Resource create(Request request) throws IllegalActionOnResourceException {
        // Get configuration of the component instance to create.
        Hashtable<String, Object> config;
        if (request.parameters() != null) {
            config = new Hashtable(request.parameters());
        } else {
            config = new Hashtable();
        }

        // Create the instance.
        String name;
        try {
            name = m_factory.createComponentInstance(config).getInstanceName();
        } catch (Exception e) {
            IllegalActionOnResourceException ee = new IllegalActionOnResourceException(request, this, "cannot create component instance");
            ee.initCause(e);
            throw ee;
        }

        // Tricky part : return the resource representing the created instance.
        Resource instance;
        try {
            instance = m_ipojo.process(new DefaultRequest(Action.READ, Path.from("/ipojo/instance").addElements(name), null));
        } catch (ResourceNotFoundException e) {
            // An instance has been created, however its Architecture service is not present.
            //TODO Should we fail here??? Can null returned value be considered as a confession of failure?
            return null;
        }
        return instance;
    }

    @Override
    public Resource delete(Request request) throws IllegalActionOnResourceException {
        // The factory must be destroyed.
        IPojoFactory f = (IPojoFactory) m_factory;
        Method weapon = null;
        try {
            //TODO find a common agreement on how to kill a factory. Is this the right (messy) way???
            weapon = IPojoFactory.class.getDeclaredMethod("dispose");
            weapon.setAccessible(true);
            // FATALITY!!!
            weapon.invoke(f);
            // Rest in peace little factory!
        } catch (Exception e) {
            throw new IllegalStateException("cannot kill factory", e);
        } finally {
            // It's a bad idea to let kids play with such a weapon...
            if (weapon != null) {
                weapon.setAccessible(false);
            }
        }
        // This resource should now have be auto-removed from its parent and marked as stale, since the represented Factory service has gone (forever).
        // Assassin may want to analyze the cadaver, so let's return it.
        return this;
    }
}
