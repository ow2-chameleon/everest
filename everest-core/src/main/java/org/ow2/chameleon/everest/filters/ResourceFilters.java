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

import org.ow2.chameleon.everest.services.*;

import java.util.List;

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

    public static ResourceFilter and(final List<ResourceFilter> filters) {
        return new ResourceFilter() {
            public boolean accept(Resource resource) {
                if (filters == null || filters.isEmpty()){
                    return false;
                }
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

    public static ResourceFilter or(final List<ResourceFilter> filters) {
        return new ResourceFilter() {
            public boolean accept(Resource resource) {
                if (filters == null || filters.isEmpty()){
                    return false;
                }
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

    public static ResourceFilter isNotNull() {
        return new ResourceFilter() {
            public boolean accept(Resource resource) {
                return (resource == null);
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

    public static ResourceFilter greaterThan(final String metadataId,final float valueToCompare) {
        return new ResourceFilter() {
            public boolean accept(Resource resource) {
                try {
                    Number value = resource.getMetadata().get(metadataId,Number.class);
                    float valueToFloat = value.floatValue();
                    return (valueToFloat > valueToCompare);
                } catch (IllegalArgumentException e1){
                    return false;
                }
            }
        };
    }

    public static ResourceFilter lowerThan(final String metadataId,final float valueToCompare) {
        return new ResourceFilter() {
            public boolean accept(Resource resource) {
                try {
                    Number value = resource.getMetadata().get(metadataId,Number.class);
                    float valueToFloat = value.floatValue();
                    return (valueToFloat < valueToCompare);
                } catch (IllegalArgumentException e1){
                    return false;
                }
            }
        };
    }

    public static ResourceFilter equalsTo(final String metadataId,final float valueToCompare) {
        return new ResourceFilter() {
            public boolean accept(Resource resource) {
                try {
                    Number value = resource.getMetadata().get(metadataId,Number.class);
                    float valueToFloat = value.floatValue();
                    return (valueToFloat == valueToCompare);
                } catch (IllegalArgumentException e1){
                    return false;
                }
            }
        };
    }

    public static ResourceFilter equalsTo(final String metadataId,final String valueToCompare) {
        return new ResourceFilter() {
            public boolean accept(Resource resource) {
                try {
                    String value = resource.getMetadata().get(metadataId,String.class);
                    return (value.equals(valueToCompare));
                } catch (IllegalArgumentException e1){
                    return false;
                }
            }
        };
    }


    public static ResourceFilter keyExist(final String metadataId) {
        return keyExistWithType(metadataId,Object.class);
    }

    public static <T> ResourceFilter keyExistWithType(final String metadataId,final Class<? extends T> clazz) {
        return new ResourceFilter() {
            public boolean accept(Resource resource) {
                try{
                    resource.getMetadata().get(metadataId,clazz);
                    return true;
                }catch (IllegalArgumentException e){
                    return false;
                }
            }
        };
    }

    public static  ResourceFilter stringStartWith(final String metadataId,final String stringToCompare) {
        return new ResourceFilter() {
            public boolean accept(Resource resource) {
                try{
                    String string = resource.getMetadata().get(metadataId,String.class);
                    return  string.startsWith(stringToCompare);
                }catch (IllegalArgumentException e){
                    return false;
                }
            }
        };

    }

    public static  ResourceFilter stringEndWith(final String metadataId,final String stringToCompare) {
        return new ResourceFilter() {
            public boolean accept(Resource resource) {
                try{
                    String string = resource.getMetadata().get(metadataId,String.class);
                    return  string.endsWith(stringToCompare);
                }catch (IllegalArgumentException e){
                    return false;
                }
            }
        };
    }

    public static  ResourceFilter stringContains(final String metadataId,final String stringToCompare) {
        return new ResourceFilter() {
            public boolean accept(Resource resource) {
                try{
                    String string = resource.getMetadata().get(metadataId,String.class);
                    return  string.contains(stringToCompare);
                }catch (IllegalArgumentException e){
                    return false;
                }
            }
        };
    }


    public static  ResourceFilter hasAtLeastChildren(final int numberMinOfChildren) {
        return new ResourceFilter() {
            public boolean accept(Resource resource) {
                List<Resource> resourceList = resource.getResources();
                if (resourceList.size() >= numberMinOfChildren){
                    return true;
                }else {
                    return false;
                }
            }
        };

    }

    public static <T> ResourceFilter hasAtLeastRelations(final int numberMinOfRelations) {
        return new ResourceFilter() {
            public boolean accept(Resource resource) {
                List<Relation> resourceList = resource.getRelations();
                if (resourceList == null || resourceList.isEmpty()){
                    return false;
                }else{
                    if (resourceList.size() >= numberMinOfRelations){
                        return true;
                    }else {
                        return false;
                    }
                }
            }
        };
    }


    public static ResourceFilter NotEqualTo(final String metadataId,final float valueToCompare) {
        return not(equalsTo(metadataId,valueToCompare));
    }

    public static ResourceFilter isSubResourceOf(final Resource root) {
        return new ResourceFilter() {
            public boolean accept(Resource resource) {
                List<Resource> resourceList = root.getResources();
                if ( (resourceList == null) || (resourceList.isEmpty())){
                    return false;
                }else {
                    return resourceList.contains(resource);
                }
            }
        };
    }

    public static ResourceFilter hasAtLeastRelationFilterMactch(final RelationFilter relationFilter,final int NumberMinOfRelationMatch) {
        return new ResourceFilter() {
            public boolean accept(Resource resource) {
                List<Relation> resourceList = resource.getRelations();
                if (resourceList == null || resourceList.isEmpty()){
                    return false;
                }else{
                    int count = 0;
                    for (Relation relation : resource.getRelations()){
                        if (relationFilter.accept(relation)){
                            count ++;
                        }
                    }
                    if (count >= NumberMinOfRelationMatch){
                        return true;
                    }else{
                        return false;
                    }
                }
            }
        };
    }

    public static ResourceFilter allRelationFilterMactch(final RelationFilter relationFilter) {
        return new ResourceFilter() {
            public boolean accept(Resource resource) {
                List<Relation> resourceList = resource.getRelations();
                if (resourceList == null || resourceList.isEmpty()){
                    return false;
                }else{
                    return  hasAtLeastRelationFilterMactch(relationFilter,resourceList.size()).accept(resource);
                }
            }
        };
    }


    public static ResourceFilter isDescendentOf(final Resource root) {
        return new ResourceFilter() {
            public boolean accept(Resource resource) {
                return resource.getPath().isDescendantOf(root.getPath());
            }
        };
    }

}
