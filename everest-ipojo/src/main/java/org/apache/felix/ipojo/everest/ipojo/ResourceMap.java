package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.services.Action;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Relation;
import org.apache.felix.ipojo.everest.services.Resource;

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
 * @param <R> the type of sub resources
 * @ThreadSafe
 */
public class ResourceMap<R extends Resource> extends DefaultReadOnlyResource {

    /**
     * The lock regulating concurrent accesses to this resource map.
     */
    protected final ReadWriteLock m_lock = new ReentrantReadWriteLock(true);

    /**
     * The underlying map of children resources.
     *
     * @GuardedBy m_lock
     */
    private final Map<Path, R> m_children = new LinkedHashMap<Path, R>();

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
     * Creates a new resource map with the given path and child relation factory.
     *
     * @param path         path of this resource m_map
     * @param isObservable the flag that indicates if the resource map to be created is observable.
     */
    public ResourceMap(Path path, boolean isObservable) {
        super(path);
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
    public void addResource(R child, String relationName, String relationDescription) {
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

    /**
     * Removes the given resource from this resource map.
     *
     * @param resource resource to remove
     * @throws IllegalArgumentException if this resource map does not contain {@code resource}
     */
    public void removeResource(R resource) {
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
     * @throws IllegalArgumentException if this resource map does not contain a resource with the given path
     */
    public void removePath(Path path) {
        m_lock.writeLock().lock();
        try {
            // Checks the resource map contains the given path
            if (!m_children.containsKey(path)) {
                throw new IllegalArgumentException("path not present");
            }
            m_children.remove(path);
            m_relationNames.remove(m_relations.remove(path).getName());
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
    public R getResource(Path path) {
        m_lock.readLock().lock();
        try {
            return m_children.get(path);
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
    public boolean containsResource(R resource) {
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
     * Get an <em>unmodifiable snapshot</em> of the current path-to-resource mappings.
     *
     * @return an unmodifiable snapshot of the current path-to-resource mappings
     */
    public Map<Path, R> getSnapshot() {
        m_lock.readLock().lock();
        try {
            return Collections.unmodifiableMap(new LinkedHashMap<Path, R>(m_children));
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
}
