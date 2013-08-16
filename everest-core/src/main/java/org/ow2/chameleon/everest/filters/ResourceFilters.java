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

package org.ow2.chameleon.everest.filters;

import org.ow2.chameleon.everest.services.Path;
import org.ow2.chameleon.everest.services.Resource;
import org.ow2.chameleon.everest.services.ResourceFilter;

/**
 * A static class giving a couple of common resource filters
 */
public class ResourceFilters {

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

    public static ResourceFilter hasCanonicalPath(final Path path) {
        return new ResourceFilter() {
            public boolean accept(Resource resource) {
                return resource.getCanonicalPath().equals(path);
            }
        };
    }

    public static ResourceFilter hasCanonicalPath(final String path) {
        return hasCanonicalPath(Path.from(path));
    }

    public static ResourceFilter hasPath(final Path path) {
        return new ResourceFilter() {
            public boolean accept(Resource resource) {
                return resource.getPath().equals(path)
                        || resource.getCanonicalPath().equals(path);
            }
        };
    }

    public static ResourceFilter hasPath(final String path) {
        return hasPath(Path.from(path));
    }

    public static ResourceFilter isSubResourceOf(final Resource root) {
        return new ResourceFilter() {
            public boolean accept(Resource resource) {
                // TODO Change this....
                return resource.getPath().toString().startsWith(root.getPath().toString())
                        && !resource.getPath().equals(root.getPath());
            }
        };
    }

}
