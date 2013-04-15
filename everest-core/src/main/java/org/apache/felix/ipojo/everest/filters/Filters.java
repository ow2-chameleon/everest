package org.apache.felix.ipojo.everest.filters;

import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.services.ResourceFilter;

/**
 * A static class giving a couple of common resource filters
 */
public class Filters {

    public static ResourceFilter all() {
        return new ResourceFilter() {
            public boolean accept(Resource resource) {
                return true;
            }
        };
    }

    public static ResourceFilter none() {
        return new ResourceFilter() {
            public boolean accept(Resource resource) {
                return false;
            }
        };
    }

    public static ResourceFilter and(final ResourceFilter... filters) {
        return new ResourceFilter() {
            public boolean accept(Resource resource) {
                for (ResourceFilter filter : filters) {
                    if (!filter.accept(resource)) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    public static ResourceFilter or(final ResourceFilter... filters) {
        return new ResourceFilter() {
            public boolean accept(Resource resource) {
                for (ResourceFilter filter : filters) {
                    if (filter.accept(resource)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    public static ResourceFilter not(final ResourceFilter filter) {
        return new ResourceFilter() {
            public boolean accept(Resource resource) {
                return !filter.accept(resource);
            }
        };
    }

    public static ResourceFilter isSubResourceOf(final Resource root) {
        return new ResourceFilter() {
            public boolean accept(Resource resource) {
                return resource.getPath().startsWith(root.getPath())
                        && !resource.getPath().equals(root.getPath());
            }
        };
    }

}
