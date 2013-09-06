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

import org.ow2.chameleon.everest.client.EverestClient;
import org.ow2.chameleon.everest.services.*;

import java.util.List;

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

    public static RelationFilter and(final List<RelationFilter> filters) {
        return new RelationFilter() {
            public boolean accept(Relation relation) {
                if (filters == null || filters.isEmpty()){
                    return false;
                }
                for (RelationFilter filter : filters) {
                    if (!filter.accept(relation)) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    public static RelationFilter or(final List<RelationFilter> filters) {
        return new RelationFilter() {
            public boolean accept(Relation relation) {
                if (filters == null || filters.isEmpty()){
                    return false;
                }
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

    public static RelationFilter containsHref(final String href) {
        return new RelationFilter() {
            public boolean accept(Relation relation) {
                return relation.getHref().toString().contains(href);
            }
        };
    }


    public static RelationFilter startWithHref(final String href) {
        return new RelationFilter() {
            public boolean accept(Relation relation) {
                return relation.getHref().toString().startsWith(href);
            }
        };
    }
    public static RelationFilter endWithHref(final String href) {
        return new RelationFilter() {
            public boolean accept(Relation relation) {
                return relation.getHref().toString().endsWith(href);
            }
        };
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

    public static RelationFilter hasAction(final String action) {
        return new RelationFilter() {
            public boolean accept(Relation relation) {
                if (action.equalsIgnoreCase("read")){
                    return hasAction(Action.READ).accept(relation);
                }else if (action.equalsIgnoreCase("update")){
                    return hasAction(Action.UPDATE).accept(relation);
                }else if (action.equalsIgnoreCase("delete")){
                    return hasAction(Action.DELETE).accept(relation);
                }else if (action.equalsIgnoreCase("create")){
                    return hasAction(Action.CREATE).accept(relation);
                }
                return false;
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

    public static RelationFilter containsName(final String name) {
        return new RelationFilter() {
            public boolean accept(Relation relation) {
                return relation.getName().contains(name);
            }
        };
    }


    public static RelationFilter startWithName(final String name) {
        return new RelationFilter() {
            public boolean accept(Relation relation) {
                return relation.getName().startsWith(name);
            }
        };
    }
    public static RelationFilter endWithName(final String name) {
        return new RelationFilter() {
            public boolean accept(Relation relation) {
                return relation.getName().endsWith(name);
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

    public static <T> RelationFilter hasParameter(final String paramName, final Class<? extends T> clazz) {
        return new RelationFilter() {
            public boolean accept(Relation relation) {
                if ((relation.getParameters() == null) || (relation.getParameters().isEmpty())){
                    return false;
                }else{
                    for (Parameter parameter : relation.getParameters()){
                        if(parameter.name().equals(paramName)){
                            if (parameter.type().equals(clazz)){
                                return true;
                            }
                        }
                    }
                    return false;
                }
            }
        };
    }

    public static RelationFilter targetChild(final EverestService everestService, final ResourceFilter filter) {
        return new RelationFilter() {
            public boolean accept(Relation relation) {
                EverestClient everestClient = new EverestClient(everestService);
              try {
                    Resource resource = everestClient.read(relation.getHref().toString()).retrieve();
                    return  filter.accept(resource);
                } catch (ResourceNotFoundException e) {
                    return false;
                }
            }
        };
    }
}