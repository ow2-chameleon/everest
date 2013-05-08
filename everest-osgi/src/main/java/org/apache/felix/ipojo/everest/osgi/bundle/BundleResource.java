package org.apache.felix.ipojo.everest.osgi.bundle;

import org.apache.felix.ipojo.everest.impl.DefaultParameter;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.wiring.BundleRevision;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.BundleNamespace.*;
import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.bundleStateToString;
import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.toBundleState;


/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 4/19/13
 * Time: 4:56 PM
 */
public class BundleResource extends DefaultResource {

    private final static String NEW_STATE_RELATION_PARAMETER = "newState";

    private final static String UPDATE_RELATION = "update";

    private final static String UPDATE_INPUT_PARAMETER = "input";

    private static final String UNINSTALL_RELATION = "uninstall";

    private static final String UPDATE_PARAMETER = "update";

    private static final String REFRESH_PARAMETER = "refresh";

    private final Bundle m_bundle;

    private final boolean isFragment;

    private final BundleResourceManager m_bundleResourceManager;

    public BundleResource(Bundle bundle, BundleResourceManager bundleResourceManager) {
        super(BundleResourceManager.BUNDLE_PATH.addElements(Long.toString(bundle.getBundleId())));
        m_bundle = bundle;
        m_bundleResourceManager = bundleResourceManager;
        // Check if is fragment
        BundleRevision rev = m_bundle.adapt(BundleRevision.class);
        isFragment = (rev != null && (rev.getTypes() & BundleRevision.TYPE_FRAGMENT) != 0);

        setRelations(
                new DefaultRelation(getPath(), Action.UPDATE, UPDATE_RELATION,
                        new DefaultParameter()
                                .name(UPDATE_PARAMETER)
                                .name(UPDATE_PARAMETER)
                                .optional(false)
                                .type(Boolean.class),
                        new DefaultParameter()
                                .name(UPDATE_INPUT_PARAMETER)
                                .description(UPDATE_INPUT_PARAMETER)
                                .optional(true)
                                .type(ByteArrayInputStream.class),
                        new DefaultParameter()
                                .name(NEW_STATE_RELATION_PARAMETER)
                                .description(BUNDLE_STATE)
                                .optional(true)
                                .type(String.class),
                        new DefaultParameter()
                                .name(REFRESH_PARAMETER)
                                .description(REFRESH_PARAMETER)
                                .optional(true)
                                .type(Boolean.class)),
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
        // update bundle
        Boolean update = request.get(UPDATE_PARAMETER, Boolean.class);
        if (update != null && update) {
            InputStream ioStream = request.get(UPDATE_INPUT_PARAMETER, ByteArrayInputStream.class);
            try {
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
        }
        // Change bundle state
        String newStateString = request.get(NEW_STATE_RELATION_PARAMETER, String.class);
        if (newStateString != null) {
            try {
                int newState = toBundleState(newStateString);
                // calculate new state
                if (m_bundle.getState() != newState) {
                    switch (newState) {
                        case Bundle.ACTIVE:
                            m_bundle.start();
                            break;
                        case Bundle.RESOLVED:
                            if (m_bundle.getState() == Bundle.INSTALLED) {
                                this.m_bundleResourceManager.resolveBundles(Collections.singletonList(m_bundle.getBundleId()));
                            } else if (m_bundle.getState() == Bundle.ACTIVE) {
                                m_bundle.stop();
                            }
                            break;
                        case Bundle.INSTALLED:
                            // TODO update with cached bundle
                            // this is not a good idea.. should do m_bundle.update(cached bundle);  instead
                            m_bundle.stop();
                            m_bundle.update();
                        case Bundle.UNINSTALLED:
                            m_bundle.uninstall();
                            break;
                        default:
                            break;
                    }
                }
            } catch (BundleException e) {
                throw new IllegalActionOnResourceException(request, e.getMessage());
            } catch (IllegalArgumentException e) {
                throw new IllegalActionOnResourceException(request, e.getMessage());
            }
        }
        // Refresh bundle
        Boolean refresh = request.get(REFRESH_PARAMETER, Boolean.class);
        if (refresh != null && refresh) {
            this.m_bundleResourceManager.refreshBundles(Collections.singletonList(m_bundle.getBundleId()));
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
