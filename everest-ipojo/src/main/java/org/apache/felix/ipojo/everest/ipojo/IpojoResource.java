package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.HandlerFactory;
import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.ipojo.architecture.Architecture;
import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.*;
import org.apache.felix.ipojo.extender.Declaration;
import org.osgi.framework.*;

import java.util.*;

/**
 * '/ipojo' resource.
 */
@Component
@Instantiate
@Provides(specifications = Resource.class)
public class IpojoResource extends DefaultReadOnlyResource {

    public static final Path PATH = Path.from("/ipojo");

    /**
     * The version of iPOJO.
     */
    private final Version m_ipojoVersion;

    /**
     * The '/factory' sub-resource.
     */
    private final FactoriesResource m_factories;

    /**
     * The '/handler' sub-resource.
     */
    private final HandlersResource m_handlers;

    /**
     * The '/instance' sub-resource.
     */
    private final InstancesResource m_instances;

    /**
     * The '/declaration' sub-resource.
     */
    private final DeclarationsResource m_declarations;

    /**
     * Construct the iPOJO root resource
     *
     * @param context bundle context of the everest-ipojo bundle.
     */
    public IpojoResource(BundleContext context) {
        super(PATH);

        // Retrieve used version of iPOJO.
        Version v = null;
        for (Bundle b : context.getBundles()) {
            if ("org.apache.felix.ipojo".equals(b.getSymbolicName())) {
                v = b.getVersion();
                break;
            }
        }
        m_ipojoVersion = v;

        // Create the sub-resources
        m_factories = new FactoriesResource();
        m_handlers = new HandlersResource();
        m_instances = new InstancesResource();
        m_declarations = new DeclarationsResource();
    }


    @Override
    public ResourceMetadata getMetadata() {
        // Return the used version of iPOJO
        return new ImmutableResourceMetadata.Builder().set("version", m_ipojoVersion.toString()).build();
    }

    @Override
    public List<Resource> getResources() {
        List<Resource> l = new ArrayList<Resource>(4);
        l.add(m_factories);
        l.add(m_handlers);
        l.add(m_instances);
        l.add(m_declarations);
        return l;
    }

    // Callback for tracking iPOJO factories.
    // Delegated to m_factories.

    @Bind(id = "factories", optional = true, aggregate = true)
    public void bindFactory(Factory factory) {
        m_factories.addFactory(factory);
    }

    @Unbind(id = "factories")
    public void unbindFactory(Factory factory) {
        m_factories.removeFactory(factory);
    }

    // Callback for tracking iPOJO handlers.
    // Delegated to m_handlers

    @Bind(id = "handlers", optional = true, aggregate = true)
    public void bindHandler(HandlerFactory handler) {
        m_handlers.addHandler(handler);
    }

    @Unbind(id = "handlers")
    public void unbindHandler(HandlerFactory handler) {
        m_handlers.removeHandler(handler);
    }

    // Callback for tracking iPOJO instances.
    // Delegated to m_instances

    @Bind(id = "instances", optional = true, aggregate = true)
    public void bindInstance(Architecture instance) {
        m_instances.addInstance(instance);
    }

    @Unbind(id = "instances")
    public void unbindInstance(Architecture instance) {
        m_instances.removeInstance(instance);
    }

    // Callback for tracking iPOJO declarations.
    // Delegated to m_declarations

    @Bind(id = "declarations", optional = true, aggregate = true)
    public void bindDeclaration(Declaration instance) {
        m_declarations.addDeclaration(instance);
    }

    @Unbind(id = "declarations")
    public void unbindDeclaration(Declaration instance) {
        m_declarations.removeDeclaration(instance);
    }

}