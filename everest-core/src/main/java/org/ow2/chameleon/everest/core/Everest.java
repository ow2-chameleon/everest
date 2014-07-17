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

package org.ow2.chameleon.everest.core;

import org.apache.felix.ipojo.annotations.*;
import org.ow2.chameleon.everest.impl.DefaultReadOnlyResource;
import org.ow2.chameleon.everest.managers.everest.EverestRootResource;
import org.ow2.chameleon.everest.services.*;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import java.util.*;

/**
 * Everest Core.
 */
@Component
@Instantiate
@Provides(specifications = EverestService.class)
public class Everest extends DefaultReadOnlyResource implements EverestService {

    /**
     * The system property used to send events synchronously.
     */
    public static final String SYNCHRONOUS_PROCESSING = "everest.processing.synchronous";

    /**
     * The system property used to dump requests to System.out.
     */
    public static final String DEBUG_REQUEST = "everest.debug.request";

    private Map<Path, Resource> resources = new LinkedHashMap<Path, Resource>();
    private List<ResourceExtender> extenders = new ArrayList<ResourceExtender>();

    public Everest() {
        super(Path.from("/"));
        // Add the everest domain
        resources.put(Path.from("/everest"), new EverestRootResource(this));
    }

    /**
     * The EventAdmin service, or {@code null} if it's not present.
     */
    private static volatile EventAdmin eventAdmin;

    @Bind(optional = true, aggregate = true)
    public void bindRootResource(Resource resource) {
        synchronized (this) {
            resources.put(resource.getCanonicalPath(), resource);
        }
    }

    @Unbind
    public void unbindRootResource(Resource resource) {
        synchronized (this) {
            resources.remove(resource.getCanonicalPath());
        }
    }

    @Bind(optional = true, aggregate = true)
    public void bindExtender(ResourceExtender extender) {
        synchronized (this) {
            extenders.add(extender);
        }
    }

    @Unbind
    public void unbindExtender(ResourceExtender extender) {
        synchronized (this) {
            extenders.remove(extender);
        }
    }

    public synchronized Map<Path, Resource> getEverestResources() {
        return new TreeMap<Path, Resource>(resources);
    }

    public synchronized List<Resource> getResources() {
        return new ArrayList<Resource>(resources.values());
    }

    public synchronized List<ResourceExtender> getExtenders() {
        return new ArrayList<ResourceExtender>(extenders);
    }

    public Resource process(Request request) throws IllegalActionOnResourceException, ResourceNotFoundException {
        // We can't extend when the original action fails.

        Resource result = super.process(request);

        // Extensions
        // We must update the resulted resource with the extensions
        for (ResourceExtender extender : getExtenders()) {
            if (extender.getFilter().accept(result)) {
                result = extender.extend(request, result);
            }
        }

        return result;
    }

    @Bind(optional = true, proxy = false)
    public void bindEventAdmin(EventAdmin ea) {
        eventAdmin = ea;
    }

    @Unbind(optional = true, proxy = false)
    public void unbindEventAdmin(EventAdmin ea) {
        eventAdmin = null;
    }

    /**
     * Post (asynchronously) the state of the given resource.
     * <p>
     * The topic of the sent event is the complete canonical path of the resource ({@code /everest/...}).
     * </p>
     *
     * @param eventType type of posted resource event
     * @param resource  concerned resource
     * @return true if event is posted to event admin, else false.
     */
    public static boolean postResource(ResourceEvent eventType, Resource resource) {
        EventAdmin ea = eventAdmin;
        if (ea == null || !resource.isObservable()) {
            return false;
        }

        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("eventType", eventType.toString());
        map.put("canonicalPath", resource.getCanonicalPath().toString());
        map.put("metadata", resource.getMetadata());
        map.put("relations", resource.getRelations());

        Event e = new Event(topicFromPath(resource.getCanonicalPath()), map);

        String mode = System.getProperty(SYNCHRONOUS_PROCESSING);
        try {
            if (mode != null && mode.equalsIgnoreCase("true")) {
                // Sync mode
                ea.sendEvent(e);
            } else {
                // Async mode (default)
                ea.postEvent(e);
            }
        } catch (SecurityException ex) {
            return false;
        } catch (IllegalStateException ex) {
            // The EventAdmin may be shutting down...
            return false;
        }
        return true;
    }

    /**
     * Transforms a path to event admin topic
     *
     * @param path resource path
     * @return topic string
     */
    public static String topicFromPath(Path path) {
        String pathString = path.toString();
        pathString = "everest".concat(pathString);
        pathString = pathString.replaceAll("\\.", "-");
        pathString = pathString.replaceAll("\\s", "_");
        pathString = pathString.replaceAll(":", "_");
        pathString = pathString.replace("[", "_");
        pathString = pathString.replace("]", "_");
        // TODO replace more forbidden characters...
        return pathString;
    }
}
