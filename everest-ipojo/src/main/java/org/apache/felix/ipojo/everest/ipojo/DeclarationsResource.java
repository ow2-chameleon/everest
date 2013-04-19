package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.extender.Declaration;

/**
 * '/ipojo/declaration' resource.
 */
public class DeclarationsResource extends DefaultReadOnlyResource {

    public static final Path PATH = IpojoResource.PATH.addElements("declaration");

    public DeclarationsResource() {
        super(PATH);
    }

    public void addDeclaration(Declaration declaration) {
        //TODO
    }

    public void removeDeclaration(Declaration declaration) {
        //TODO
    }

}
