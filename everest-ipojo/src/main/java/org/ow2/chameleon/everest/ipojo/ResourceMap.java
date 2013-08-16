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

import org.ow2.chameleon.everest.impl.DefaultReadOnlyResource;
import org.ow2.chameleon.everest.impl.DefaultRelation;
import org.ow2.chameleon.everest.services.*;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A resource that is an holder for sub-resources.
 * <p/>
 * <p>
 * By default, it has :
 * <ul>
 * <li>no metadata</li>
 * <li>customizable relations to its direct child resources</li>
 * </ul>
 * </p>
 *
 * @ThreadSafe
 */
// TODO refactor : this class gets more and more dirty
public class ResourceMap extends DefaultReadOnlyResource {

    /**
     * The lock regulating concurrent accesses to this resource map.
     */
    protected final ReadWriteLock m_lock = new ReentrantReadWriteLock(true);

    /**
     * The underlying map of children resources.
     *
     * @GuardedBy m_lock
     */
    private final Map<Path, Resource> m_children = new LinkedHashMap<Path, Resource>();

    /**
     * The relations to the children.
     *
     * @GuardedBy m_lock
     */
    private final Map<Path, Relation> m_relations = new LinkedHashMap<Path, Relation>();

    /**
     * The names of the relations to the children.
     *
     * @GuardedBy m_lock
     */
    private final Set<String> m_relationNames = new LinkedHashSet<String>();

    /**
     * The flag indicating if this resource map is observable.
     *
     * @see #isObservable()
     */
    private final boolean m_isObservable;

    /**
     * Creates a new resource map with the given path.
     *
     * @param path         path of this resource map
     * @param isObservable the flag that indicates if the resource map to be created is observable.
     */
    public ResourceMap(Path path, boolean isObservable) {
        this(path, isObservable, null);
    }

    /**
     * Creates a new resource map with the given path.
     *
     * @param path          path of this resource map
     * @param isObservable  the flag that indicates if the resource map to be created is observable.
     * @param metadata      the metadata for this resource map, may be {@code null}.
     * @param baseRelations the base relations of this resource map.
     */
    public ResourceMap(Path path, boolean isObservable, ResourceMetadata metadata, Relation... baseRelations) {
        super(path, metadata);
        setRelations(baseRelations);
        m_isObservable = isObservable;
    }

    /**
     * Adds the given resource to this resource map.
     *
     * @param child resource to add
     * @throws NullPointerException     if {@code resource} is {@code null}, or if the generated relation name is {@code null}
     * @throws IllegalArgumentException if {@code resource}'s path is not a direct child of this resource map's path, or
     *                                  if this resource map already contains a resource with the same path.
     * @throws IllegalStateException    if the generated relation name is not unique..
     */
    public void addResource(Resource child, String relationName, String relationDescription) {
        if (child == null) {
            throw new NullPointerException("resource is null");
        } else if (relationName == null) {
            throw new NullPointerException("relationName is null");
        }

        // Checks path of the resource is a direct sub-path of this resource map
        Path path = child.getPath();
        if (path.subtract(getPath()).getCount() != 1) {
            throw new IllegalArgumentException("resource is not a direct child: " + path);
        }

        // Create the relation  to the child
        Relation relation = new DefaultRelation(child.getPath(), Action.READ, relationName, relationDescription, null);

        // Do add the resource, and the relation.
        m_lock.writeLock().lock();
        try {
            if (m_children.containsKey(path)) {
                throw new IllegalArgumentException("resource with same path already present: " + path);
            } else if (m_relationNames.contains(relationName)) {
                throw new IllegalStateException("relation name is not unique: " + relationName);
            }
            m_children.put(child.getPath(), child);
            m_relations.put(child.getPath(), relation);
            m_relationNames.add(relationName);
        } finally {
            m_lock.writeLock().unlock();
        }
    }

    // TODO
    public AtomicInsertionResult<ResourceMap> addResourceMapIfAbsent(Path path, boolean isObservable, String relationName, String relationDescription) {
        if (path == null) {
            throw new NullPointerException("path is null");
        }
        m_lock.writeLock().lock();
        try {
            if (m_children.containsKey(path)) {
                // Check for presence and return fast (we own the write lock!).
                Resource r = m_children.get(path);
                if (!(r instanceof ResourceMap)) {
                    throw new IllegalArgumentException("resource present but not a resource map");
                }
                return new AtomicInsertionResult<ResourceMap>(true, ResourceMap.class.cast(r));
            } else {
                @SuppressWarnings("unchecked")
                ResourceMap child = new ResourceMap(path, isObservable);
                addResource(child, relationName, relationDescription);
                return new AtomicInsertionResult<ResourceMap>(false, child);
            }
        } finally {
            m_lock.writeLock().unlock();
        }
    }

    /**
     * Removes the given resource from this resource map.
     *
     * @param resource resource to remove
     * @throws IllegalArgumentException if this resource map does not contain {@code resource}
     */
    public void removeResource(Resource resource) {
        m_lock.writeLock().lock();
        try {
            // Checks the resource is contained in the map
            if (!m_children.containsValue(resource)) {
                throw new IllegalArgumentException("resource not present");
            }
            m_children.remove(resource.getPath());
            m_relationNames.remove(m_relations.remove(resource.getPath()).getName());

        } finally {
            m_lock.writeLock().unlock();
        }
    }

    /**
     * Removes the resource with the given path from this resource map.
     *
     * @param path path of the resource to remove
     * @return the removed resource
     * @throws IllegalArgumentException if this resource map does not contain a resource with the given path
     */
    public Resource removePath(Path path) {
        m_lock.writeLock().lock();
        try {
            // Checks the resource map contains the given path
            if (!m_children.containsKey(path)) {
                throw new IllegalArgumentException("path not present");
            }
            m_relationNames.remove(m_relations.remove(path).getName());
            return m_children.remove(path);
        } finally {
            m_lock.writeLock().unlock();
        }
    }

    /**
     * Removes the resource with the given path from this resource map.
     *
     * @param path path of the resource to remove
     * @return the removed resource
     * @throws IllegalArgumentException if this resource map does not contain a resource with the given path
     */
    public <R extends Resource> R removePath(Path path, Class<R> type) {
        m_lock.writeLock().lock();
        try {
            // Checks the resource map contains the given path
            if (!m_children.containsKey(path)) {
                throw new IllegalArgumentException("path not present");
            }
            Resource r = m_children.get(path);
            if (!type.isInstance(r)) {
                throw new IllegalArgumentException("resource not instance of type: " + type.getName());
            }
            m_children.remove(path);
            m_relationNames.remove(m_relations.remove(path).getName());
            return type.cast(r);
        } finally {
            m_lock.writeLock().unlock();
        }
    }

    /**
     * Return the resource with the specified path, contained in this resource map, or {@code null} if this resource map
     * contains no resource with that path.
     *
     * @param path path of the resource to return
     * @return the resource with the specified path, or {@code null} if this resource map contains no resource with that
     *         path.
     */
    public Resource getResource(Path path) {
        m_lock.readLock().lock();
        try {
            return m_children.get(path);
        } finally {
            m_lock.readLock().unlock();
        }
    }

    /**
     * Return the resource with the specified path, contained in this resource map, or {@code null} if this resource map
     * contains no resource with that path.
     *
     * @param path path of the resource to return
     * @param type the expected type of resource
     * @return the resource with the specified path, or {@code null} if this resource map contains no resource with that
     *         path.
     * @throws ClassCastException if the resource
     */
    public <R extends Resource> R getResource(Path path, Class<R> type) {
        m_lock.readLock().lock();
        try {
            Resource r = m_children.get(path);
            if (r != null && type.isInstance(r)) {
                return type.cast(r);
            } else {
                return null;
            }
        } finally {
            m_lock.readLock().unlock();
        }
    }

    /**
     * Returns the number of resources contained in this resource map.
     *
     * @return the number of resources contained in this resource map.
     */
    public int size() {
        m_lock.readLock().lock();
        try {
            return m_children.size();
        } finally {
            m_lock.readLock().unlock();
        }
    }

    /**
     * Returns {@code true} if this resource map contains no resource, {@code false} otherwise.
     *
     * @return {@code true} if this resource map contains no resource, {@code false} otherwise
     */
    public boolean isEmpty() {
        m_lock.readLock().lock();
        try {
            return m_children.isEmpty();
        } finally {
            m_lock.readLock().unlock();
        }
    }

    /**
     * Returns {@code true} if this resource map contains a resource with the specified path, {@code false} otherwise.
     *
     * @param path path of the resource whose presence is to be tested
     * @return {@code true} if this resource map contains a resource with the specified path, {@code false} otherwise
     */
    public boolean containsPath(Path path) {
        m_lock.readLock().lock();
        try {
            return m_children.containsKey(path);
        } finally {
            m_lock.readLock().unlock();
        }
    }

    /**
     * Returns {@code true} if this resource map contains the specified resource, {@code false} otherwise.
     *
     * @param resource resource whose presence is to be tested
     * @return {@code true} if this resource map contains the specified resource, {@code false} otherwise
     */
    public boolean containsResource(Resource resource) {
        m_lock.readLock().lock();
        try {
            return m_children.containsValue(resource);
        } finally {
            m_lock.readLock().unlock();
        }
    }

    /**
     * Returns a {@link Set} view of the paths of the resources contained in this resource map. The set is an
     * <em>unmodifiable snapshot</em> of this resource map, so changes to the map are not reflected in the returned set.
     *
     * @return a set view of the keys contained in this map
     */
    public Set<Path> getPaths() {
        return getSnapshot().keySet();
    }

    /**
     * {@inheritDoc}
     * <p>
     * We need to override this method because we bypass the DefaultResource's sub-resources.
     * </p>
     */
    @Override
    public List<Resource> getResources() {
        m_lock.readLock().lock();
        try {
            if (m_children.isEmpty()) {
                return Collections.emptyList();
            } else {
                return Collections.unmodifiableList(new ArrayList<Resource>(m_children.values()));
            }
        } finally {
            m_lock.readLock().unlock();
        }
    }

    /**
     * Get resources as a type-safe list.
     * Resources that are not instance of the given type are skipped.
     */
    public <R extends Resource> List<R> getResourcesTypeSafe(Class<R> type) {
        m_lock.readLock().lock();
        try {
            List<R> l = new ArrayList<R>(m_children.size());
            for (Resource r : m_children.values()) {
                if (type.isInstance(r)) {
                    l.add(type.cast(r));
                }
            }
            if (!l.isEmpty()) {
                return Collections.unmodifiableList(l);
            } else {
                return Collections.emptyList();
            }
        } finally {
            m_lock.readLock().unlock();
        }
    }

    /**
     * Get an <em>unmodifiable snapshot</em> of the current path-to-resource mappings.
     *
     * @return an unmodifiable snapshot of the current path-to-resource mappings
     */
    public Map<Path, Resource> getSnapshot() {
        m_lock.readLock().lock();
        try {
            return Collections.unmodifiableMap(new LinkedHashMap<Path, Resource>(m_children));
        } finally {
            m_lock.readLock().unlock();
        }
    }

    @Override
    public <A> A adaptTo(Class<A> clazz) {
        if (clazz == Map.class) {
            // Map => snapshot of the current <path, resource> mapping
            return clazz.cast(getSnapshot());
        } else if (clazz == Collection.class) {
            // Collection => snapshot of the current contained resources
            return clazz.cast(getSnapshot().values());
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * We append here the relations to the children.
     * </p>
     */
    @Override
    public List<Relation> getRelations() {
        // Get the relations that has been explicitly set.
        List<Relation> relations = new ArrayList<Relation>(super.getRelations());

        // Add the relations to the children.
        m_lock.readLock().lock();
        try {
            if (!m_relations.isEmpty()) {
                relations.addAll(m_relations.values());
            }
        } finally {
            m_lock.readLock().unlock();
        }
        return Collections.unmodifiableList(relations);
    }

    @Override
    public boolean isObservable() {
        return m_isObservable;
    }

    /**
     * The result of an atomic {@link #addResourceMapIfAbsent(Path, boolean, String, String)} operation.
     *
     * @param <T>
     */
    public final static class AtomicInsertionResult<T extends Resource> {

        /**
         * Flag indicating if a resource was present at the specified path.
         */
        final boolean wasPresent;

        /**
         * The existing resource if {@code wasPresent} is {@code true}, the created one otherwise.
         */
        final T resource;

        private AtomicInsertionResult(boolean wasPresent, T resource) {
            this.wasPresent = wasPresent;
            this.resource = resource;
        }
    }

}
