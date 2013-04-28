package org.apache.felix.ipojo.everest.osgi;

import org.apache.felix.ipojo.everest.impl.DefaultParameter;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.wiring.BundleRevision;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.apache.felix.ipojo.everest.osgi.BundleResourceManager.BUNDLE_PATH;
import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.BundleNamespace.*;
import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.bundleStateToString;


/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/19/13
 * Time: 4:56 PM
 */
public class BundleResource extends DefaultResource {

    private final static String NEW_STATE_RELATION = "new-state";

    private final static String NEW_STATE_RELATION_PARAMETER = "newState";

    private final static String UPDATE_RELATION = "update";

    private final static String UPDATE_RELATION_PARAMETER = "input";

    private static final String UNINSTALL_RELATION = "uninstall";

    private final Bundle m_bundle;

    private final boolean isFragment;

    public BundleResource(Bundle bundle) {
        super(BUNDLE_PATH.addElements(Long.toString(bundle.getBundleId())));
        m_bundle = bundle;
        // Check if is fragment
        BundleRevision rev = m_bundle.adapt(BundleRevision.class);
        isFragment = (rev != null && (rev.getTypes() & BundleRevision.TYPE_FRAGMENT) != 0);

        setRelations(
                new DefaultRelation(getPath(), Action.UPDATE, UPDATE_RELATION,
                        new DefaultParameter()
                                .name(UPDATE_RELATION_PARAMETER)
                                .description(UPDATE_RELATION_PARAMETER)
                                .optional(false)
                                .type(InputStream.class)),
                new DefaultRelation(getPath(), Action.UPDATE, NEW_STATE_RELATION,
                        new DefaultParameter()
                                .name(NEW_STATE_RELATION_PARAMETER)
                                .description(BUNDLE_STATE)
                                .optional(false)
                                .type(Integer.class)),
                new DefaultRelation(getPath(), Action.DELETE, UNINSTALL_RELATION)
        );

    }

    public Bundle getBundle() {
        return m_bundle;
    }

    public ResourceMetadata getSimpleMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder();
        metadataBuilder.set(BUNDLE_STATE, bundleStateToString(m_bundle.getState()));
        metadataBuilder.set(Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE, m_bundle.getSymbolicName());
        metadataBuilder.set(Constants.BUNDLE_VERSION_ATTRIBUTE, m_bundle.getVersion());
        return metadataBuilder.build();
    }

    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder metadataBuilder = new ImmutableResourceMetadata.Builder(getSimpleMetadata());
        metadataBuilder.set(BUNDLE_LOCATION, m_bundle.getLocation());
        metadataBuilder.set(BUNDLE_LAST_MODIFIED, m_bundle.getLastModified());
        metadataBuilder.set(BUNDLE_FRAGMENT, isFragment);
        //TODO find some properties to add here!
        return metadataBuilder.build();
    }

    @Override
    public List<Resource> getResources() {
        ArrayList<Resource> resources = new ArrayList<Resource>();
        resources.add(new BundleHeadersResource(getPath(), m_bundle));
        resources.add(new BundleWiresResource(getPath(), m_bundle));
        resources.add(new BundleServicesResource(getPath(), m_bundle));
        return resources;
    }

    @Override
    public Resource update(Request request) throws IllegalActionOnResourceException {
        Resource resource = this;
        if (request.parameters().get(UPDATE_RELATION_PARAMETER) != null) {
            try {
                InputStream ioStream = request.get(UPDATE_RELATION_PARAMETER, InputStream.class);
                if (ioStream != null) {
                    m_bundle.update(ioStream);
                } else {
                    m_bundle.update();
                }
            } catch (BundleException e) {
                throw new IllegalActionOnResourceException(request, e.getMessage());
            } catch (Throwable t) {
                throw new IllegalActionOnResourceException(request, t.getMessage());
            }
        } else if (request.parameters().get(NEW_STATE_RELATION_PARAMETER) != null) {
            try {
                Integer newState = request.get(NEW_STATE_RELATION_PARAMETER, Integer.class);
                if (newState != null) {
                    // calculate new state
                    if (m_bundle.getState() != newState) {
                        switch (newState) {
                            case Bundle.ACTIVE:
                                m_bundle.start();
                                break;
                            case Bundle.RESOLVED:
                                m_bundle.stop();
                                break;
                            case Bundle.UNINSTALLED:
                                m_bundle.uninstall();
                                break;
                            default:
                                break;
                        }
                    }// else noop
                }
            } catch (Throwable t) {
                throw new IllegalActionOnResourceException(request, t.getMessage());
            }
        } else {
            throw new IllegalActionOnResourceException(request, "Operation not recognized");
        }
        return resource;
    }

    @Override
    public Resource delete(Request request) throws IllegalActionOnResourceException {
        try {
            m_bundle.uninstall();
        } catch (BundleException e) {
            throw new IllegalActionOnResourceException(request, e.getMessage());
        }
        //TODO should build a resource here new DefaultReadOnlyResource.Builder()
        return null;
    }
}
