package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.extender.Declaration;

import java.util.*;

import static org.apache.felix.ipojo.everest.ipojo.IpojoRootResource.IPOJO_ROOT_PATH;

/**
 * Resource representation for all iPOJO declarations.
 */
public class IpojoDeclarationRootResource extends DefaultReadOnlyResource {

    /**
     * The path of the iPOJO declaration resource manager.
     */
    public static final Path IPOJO_INSTANCE_ROOT_PATH = IPOJO_ROOT_PATH.add(Path.from("/declaration"));

    /**
     * The iPOJO declarations.
     */
    private final Set<Declaration> m_declarations = new LinkedHashSet<Declaration>();

    public IpojoDeclarationRootResource() {
        super(IPOJO_INSTANCE_ROOT_PATH);
    }

    /**
     * Add an iPOJO declaration.
     * @param declaration the arriving iPOJO declaration
     */
    void addDeclaration(Declaration declaration) {
        synchronized (m_declarations) {
            m_declarations.add(declaration);
        }
    }

    /**
     * Remove an iPOJO declaration.
     * @param declaration the leaving iPOJO declaration
     */
    void removeDeclaration(Declaration declaration) {
        synchronized (m_declarations) {
            m_declarations.remove(declaration);
        }
    }

}
