package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.IPojoFactory;
import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.DefaultRequest;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static org.apache.felix.ipojo.everest.ipojo.IpojoRootResource.PATH_TO_OSGI_BUNDLES;

/**
 * '/ipojo/factory/$name/$version' resource, where $name stands for the name of a factory, and $version for its version.
 */
public class FactoryResource extends DefaultReadOnlyResource {

    /**
     * The enclosing iPOJO resource.
     */
    private final IpojoRootResource m_ipojo;

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
    //TODO add relation to bundle that defines the implementation class
    //TODO add relation to Factory service

    /**
     * @param factory the factory represented by this resource
     */
    @SuppressWarnings("deprecation")
    public FactoryResource(IpojoRootResource ipojo, Factory factory) {
        super(canonicalPathOf(factory));
        m_ipojo = ipojo;
        m_factory = factory;
        // Build the immutable metadata of this factory.
        ImmutableResourceMetadata.Builder mb = new ImmutableResourceMetadata.Builder();
        mb.set("name", m_factory.getName()); // String
        mb.set("version", m_factory.getVersion()); // String
        mb.set("className", m_factory.getClassName()); // String, deprecated but who cares?
        m_baseMetadata = mb.build();

        // Relations
        List<Relation> relations = new ArrayList<Relation>();

        // Add relation 'bundle' to READ the bundle that declares this factory
        relations.add(new DefaultRelation(
                PATH_TO_OSGI_BUNDLES.addElements(String.valueOf(m_factory.getBundleContext().getBundle().getBundleId())),
                Action.READ, "bundle"));

        // Add relation 'requiredHandler[$ns:$name]' to READ the handlers required by this factory
        @SuppressWarnings("unchecked")
        List<String> required = (List<String>) m_factory.getRequiredHandlers();
        for (String nsName : required) {
            int i = nsName.lastIndexOf(':');
            String ns = nsName.substring(0, i);
            String name = nsName.substring(i + 1);
            relations.add(new DefaultRelation(IpojoRootResource.HANDLERS.addElements(ns, name), Action.READ,
                    "requiredHandler[" + nsName + "]"));
        }

        setRelations(relations);
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

    @Override
    public List<Relation> getRelations() {
        List<Relation> relations = super.getRelations();

        // Add relation to created instances
        for (String instanceName : getCreatedInstanceNames()) {
            relations.add(new DefaultRelation(IpojoRootResource.INSTANCES.addElements(instanceName), Action.READ,
                    "instance[" + instanceName + "]"));
        }

        return relations;
    }

    private static Path canonicalPathOf(Factory f) {
        // Canonical path is '/ipojo/factory/$factoryName/$factoryVersion'
        // If m_version == null, then $factoryVersion is the literal 'null'
        return IpojoRootResource.FACTORIES.addElements(f.getName(), String.valueOf(f.getVersion()));
    }

    public static String stateAsString(int state) {
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
            config = new Hashtable<String, Object>(request.parameters());
        } else {
            config = new Hashtable<String, Object>();
        }

        // Create the instance.
        String name;
        try {
            name = m_factory.createComponentInstance(config).getInstanceName();
        } catch (Exception e) {
            IllegalActionOnResourceException ee = new IllegalActionOnResourceException(request, this,
                    "cannot create component instance");
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

    // Get the instances created by this factory
    // This is a hack!
    private Set<String> getCreatedInstanceNames() {
        Field weapon = null;
        try {
            weapon = IPojoFactory.class.getDeclaredField("m_componentInstances");
            weapon.setAccessible(true);
            return ((Map<String, ComponentInstance>) weapon.get(m_factory)).keySet();
        } catch (Exception e) {
            throw new RuntimeException("cannot get factory created instances", e);
        } finally {
            if (weapon != null) {
                weapon.setAccessible(false);
            }
        }
    }

    @Override
    public boolean isObservable() {
        return true;
    }

    @Override
    public <A> A adaptTo(Class<A> clazz) {
        if (clazz == Factory.class) {
            return (A) m_factory;
        }
        return null;
    }

}
