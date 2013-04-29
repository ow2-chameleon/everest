package org.apache.felix.ipojo.everest.filters;

import org.apache.felix.ipojo.everest.services.*;

/**
 * A static class giving a couple of common relation filters
 */
public class RelationFilters {

    public static RelationFilter all() {
        return new RelationFilter() {
            public boolean accept(Relation relation) {
                return true;
            }
        };
    }

    public static RelationFilter none() {
        return new RelationFilter() {
            public boolean accept(Relation relation) {
                return false;
            }
        };
    }

    public static RelationFilter and(final RelationFilter... filters) {
        return new RelationFilter() {
            public boolean accept(Relation relation) {
                for (RelationFilter filter : filters) {
                    if (!filter.accept(relation)) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    public static RelationFilter or(final RelationFilter... filters) {
        return new RelationFilter() {
            public boolean accept(Relation relation) {
                for (RelationFilter filter : filters) {
                    if (filter.accept(relation)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    public static RelationFilter not(final RelationFilter filter) {
        return new RelationFilter() {
            public boolean accept(Relation relation) {
                return !filter.accept(relation);
            }
        };
    }

    public static RelationFilter hasHref(final Path href) {
        return new RelationFilter() {
            public boolean accept(Relation relation) {
                return relation.getHref().equals(href);
            }
        };
    }

    public static RelationFilter hasHref(final String href) {
        return hasHref(Path.from(href));
    }

    public static RelationFilter hasHref(final Resource href) {
        return or(hasHref(href.getCanonicalPath()) ,hasHref(href.getPath()));
    }

    public static RelationFilter hasAction(final Action action) {
        return new RelationFilter() {
            public boolean accept(Relation relation) {
                return relation.getAction().equals(action);
            }
        };
    }

    public static RelationFilter hasName(final String name) {
        return new RelationFilter() {
            public boolean accept(Relation relation) {
                return relation.getName().equals(name);
            }
        };
    }

    public static RelationFilter hasParameter(final String param) {
        return new RelationFilter() {
            public boolean accept(Relation relation) {
                return relation.getParameters().contains(param);
            }
        };
    }

    // TODO hasParameter(String), hasParameter(String, Object)

}
