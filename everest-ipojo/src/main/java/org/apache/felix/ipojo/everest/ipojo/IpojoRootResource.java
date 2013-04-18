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
 * Resource manager for iPOJO, with path '/ipojo'.
 */
@Component
@Instantiate
@Provides(specifications = Resource.class)
public class IpojoRootResource extends DefaultReadOnlyResource {

    /**
     * The path of the iPOJO factory resource manager.
     */
    public static final Path IPOJO_ROOT_PATH = Path.from("/ipojo");

    /**
     * The version of iPOJO.
     */
    private final Version m_ipojoVersion;

    /**
     * The '/factory' sub-resource.
     */
    private final IpojoFactoryRootResource m_factories;

    /**
     * The '/handler' sub-resource.
     */
    private final IpojoHandlerRootResource m_handlers;

    /**
     * The '/instance' sub-resource.
     */
    private final IpojoInstanceRootResource m_instances;

    /**
     * The '/declarations' sub-resource.
     */
    private final IpojoDeclarationRootResource m_declarations;

    /**
     * Construct the iPOJO root resource
     *
     * @param context bundle context of the everest-ipojo bundle.
     */
    public IpojoRootResource(BundleContext context) {
        super(IPOJO_ROOT_PATH);

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
        m_factories = new IpojoFactoryRootResource();
        m_handlers = new IpojoHandlerRootResource();
        m_instances = new IpojoInstanceRootResource();
        m_declarations = new IpojoDeclarationRootResource();
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
    private void bindFactory(Factory factory) {
        m_factories.addFactory(factory);
    }

    @Unbind(id = "factories")
    private void unbindFactory(Factory factory) {
        m_factories.removeFactory(factory);
    }

    // Callback for tracking iPOJO handlers.
    // Delegated to m_handlers

    @Bind(id = "handlers", optional = true, aggregate = true)
    private void bindHandler(HandlerFactory handler) {
        m_handlers.addHandler(handler);
    }

    @Unbind(id = "handlers")
    private void unbindHandler(HandlerFactory handler) {
        m_handlers.removeHandler(handler);
    }

    // Callback for tracking iPOJO instances.
    // Delegated to m_instances

    @Bind(id = "instances", optional = true, aggregate = true)
    private void bindInstance(Architecture instance) {
        m_instances.addInstance(instance);
    }

    @Unbind(id = "instances")
    private void unbindInstance(Architecture instance) {
        m_instances.removeInstance(instance);
    }

    // Callback for tracking iPOJO declarations.
    // Delegated to m_declarations

    @Bind(id = "declarations", optional = true, aggregate = true)
    private void bindInstance(Declaration instance) {
        m_declarations.addDeclaration(instance);
    }

    @Unbind(id = "declarations")
    private void unbindInstance(Declaration instance) {
        m_declarations.removeDeclaration(instance);
    }

}