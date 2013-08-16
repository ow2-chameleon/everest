/*
 * Copyright 2013 OW2 Chameleon
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ow2.chameleon.everest.ipojo;

import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.FactoryStateListener;
import org.apache.felix.ipojo.HandlerFactory;
import org.ow2.chameleon.everest.core.Everest;
import org.ow2.chameleon.everest.impl.DefaultReadOnlyResource;
import org.ow2.chameleon.everest.impl.DefaultRelation;
import org.ow2.chameleon.everest.impl.ImmutableResourceMetadata;
import org.ow2.chameleon.everest.services.Action;
import org.ow2.chameleon.everest.services.Relation;
import org.ow2.chameleon.everest.services.ResourceEvent;
import org.ow2.chameleon.everest.services.ResourceMetadata;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static org.ow2.chameleon.everest.ipojo.FactoryResource.stateAsString;
import static org.ow2.chameleon.everest.ipojo.IpojoRootResource.*;

/**
 * '/ipojo/handler/$namespace/$name' resource.
 */
public class HandlerResource extends DefaultReadOnlyResource implements FactoryStateListener {

    /**
     * The underlying HandlerFactory service.
     */
    private final WeakReference<HandlerFactory> m_handler;

    public HandlerResource(HandlerFactory handler, ServiceReference<HandlerFactory> ref) {
        super(HANDLERS.addElements(handler.getNamespace(), handler.getName()),
                new ImmutableResourceMetadata.Builder()
                        .set("namespace", handler.getNamespace())
                        .set("name", handler.getName())
                        .build());
        m_handler = new WeakReference<HandlerFactory>(handler);
        handler.addFactoryStateListener(this);
        // Set the immutable relations
        List<Relation> relations = new ArrayList<Relation>();
        relations.add(new DefaultRelation(
                PATH_TO_OSGI_SERVICES.addElements(String.valueOf(ref.getProperty(Constants.SERVICE_ID))),
                Action.READ,
                "service",
                "The HandlerFactory OSGi service"));
        relations.add(new DefaultRelation(
                PATH_TO_OSGI_BUNDLES.addElements(String.valueOf(handler.getBundleContext().getBundle().getBundleId())),
                Action.READ,
                "bundle",
                "The declaring OSGi bundle"));
        relations.add(new DefaultRelation(
                TYPE_DECLARATIONS.addElements(handler.getName(), "null"),
                Action.READ,
                "declaration",
                "The declaration of this handler")); // May not exist! Do we mind? Really?
        // Add relation 'requiredHandler:$ns:$name' to READ the handlers required by this factory
        @SuppressWarnings("unchecked")
        List<String> required = (List<String>) handler.getRequiredHandlers();
        for (String nsName : required) {
            int i = nsName.lastIndexOf(':');
            String ns = nsName.substring(0, i);
            String name = nsName.substring(i + 1);
            relations.add(new DefaultRelation(
                    IpojoRootResource.HANDLERS.addElements(ns, name),
                    Action.READ,
                    "requiredHandler[" + nsName + "]",
                    String.format("Required handler '%s'", nsName)));
        }
        setRelations(relations);
    }

    @Override
    public ResourceMetadata getMetadata() {
        HandlerFactory h = m_handler.get();
        ResourceMetadata m = super.getMetadata();
        if (h == null) {
            // Reference has been released
            return m;
        }
        // Add dynamic metadata
        return new ImmutableResourceMetadata.Builder(m)
                .set("state", stateAsString(h.getState())) // String
                .set("missingHandlers", h.getMissingHandlers()) // List<String>
                .build();
    }

    @Override
    public boolean isObservable() {
        return true;
    }

    @Override
    public <A> A adaptTo(Class<A> clazz) {
        if (clazz == HandlerFactory.class) {
            // Returns null if reference has been released
            return clazz.cast(m_handler.get());
        } else {
            return super.adaptTo(clazz);
        }
    }

    public void stateChanged(Factory factory, int newState) {
        // Fire UPDATED event
        Everest.postResource(ResourceEvent.UPDATED, this);
    }

    public void cleanup() {
        HandlerFactory h = m_handler.get();
        if (h == null) {
            return;
        }
        h.removeFactoryStateListener(this);
    }
}
