package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.everest.impl.DefaultReadOnlyResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.Path;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;

import java.util.*;

import static java.util.Map.Entry;

/**
 * A builder for resources that just holds for sub-resources.
 * <p/>
 * <p>
 * By default, it has :
 * <ul>
 * <li>no metadata</li>
 * <li>no relation</li>
 * </ul>
 * </p>
 *
 * @param <R> the type of sub resources
 * @NotThreadSafe Concurrent accesses must be synchronized externally
 */
public class ResourceMap<R extends Resource> extends DefaultReadOnlyResource {

    /**
     * The underlying map of resources.
     */
    private final Map<Path, R> m_map = new LinkedHashMap<Path, R>();

    /**
     * Creates a new resource map with the given path.
     *
     * @param path path of this resource m_map
     */
    public ResourceMap(Path path) {
        super(path);
    }

    /**
     * Adds the given resource to this resource map.
     *
     * @param resource resource to add
     * @throws NullPointerException     if {@code resource} is {@code null}
     * @throws IllegalArgumentException if {@code resource}'s path is not a direct child of this resource map's path, or
     *                                  if this resource map already contains a resource with the same path.
     */
    public void addResource(R resource) {
        if (resource == null) {
            throw new NullPointerException("resource is null");
        }
        // Checks path of the resource is a direct sub-path of this resource map
        Path path = resource.getPath();
        if (path.subtract(getPath()).getCount() != 1) {
            throw new IllegalArgumentException("resource is not a direct child: " + path);
        }
        if (m_map.containsKey(path)) {
            throw new IllegalArgumentException("resource with same path already present: " + path);
        }
        m_map.put(resource.getPath(), resource);
    }

    /**
     * Removes the given resource from this resource map.
     *
     * @param resource resource to remove
     * @throws IllegalArgumentException if this resource map does not contain {@code resource}
     */
    public void removeResource(R resource) {
        // Checks the resource is contained in the map
        if (!m_map.containsValue(resource)) {
            throw new IllegalArgumentException("resource not present");
        }
        m_map.remove(resource.getPath());
    }

    /**
     * Removes the resource with the given path from this resource map.
     *
     * @param path path of the resource to remove
     * @throws IllegalArgumentException if this resource map does not contain a resource with the given path
     */
    public void removePath(Path path) {
        // Checks the resource map contains the given path
        if (!m_map.containsKey(path)) {
            throw new IllegalArgumentException("path not present");
        }
        m_map.remove(path);
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
        return m_map.get(path);
    }

    /**
     * {@inheritDoc}
     * <p>
     * We need to override this method because we bypass the DefaultResource's sub-resources.
     * </p>
     */
    @Override
    public List<Resource> getResources() {
        return new ArrayList<Resource>(m_map.values());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Here we build an aggregation of the sub-resources' metadata, prefixed by their last path element.
     * </p>
     *
     * @return
     */
    @Override
    public ResourceMetadata getMetadata() {
        ImmutableResourceMetadata.Builder b = new ImmutableResourceMetadata.Builder();
        for (Entry<Path, R> entry : m_map.entrySet()) {
            b.set(entry.getKey().getLast(), entry.getValue().getMetadata());
        }
        return b.build();
    }

    /**
     * Returns the number of resources contained in this resource map.
     *
     * @return the number of resources contained in this resource map.
     */
    public int size() {
        return m_map.size();
    }

    /**
     * Returns {@code true} if this resource map contains no resource, {@code false} otherwise.
     *
     * @return {@code true} if this resource map contains no resource, {@code false} otherwise
     */
    public boolean isEmpty() {
        return m_map.isEmpty();
    }

    /**
     * Returns {@code true} if this resource map contains a resource with the specified path, {@code false} otherwise.
     *
     * @param path path of the resource whose presence is to be tested
     * @return {@code true} if this resource map contains a resource with the specified path, {@code false} otherwise
     */
    public boolean containsPath(Path path) {
        return m_map.containsKey(path);
    }

    /**
     * Returns {@code true} if this resource map contains the specified resource, {@code false} otherwise.
     *
     * @param resource resource whose presence is to be tested
     * @return {@code true} if this resource map contains the specified resource, {@code false} otherwise
     */
    public boolean containsResource(R resource) {
        return m_map.containsValue(resource);
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
     * Get an <em>unmodifiable snapshot</em> of the current path-to-resource mappings.
     *
     * @return an unmodifiable snapshot of the current path-to-resource mappings
     */
    public Map<Path, R> getSnapshot() {
        return Collections.unmodifiableMap(new LinkedHashMap<Path, R>(m_map));
    }

    @Override
    public <A> A adaptTo(Class<A> clazz) {
        if (clazz == Map.class) {
            return (A) getSnapshot();
        }
        return null;
    }

}
