package org.apache.felix.ipojo.everest.impl;

import java.util.Arrays;
import java.util.List;

/**
 * Utility method to parse and build path
 */
public class Paths {

    public static final String PATH_SEPARATOR = "/";

    public static List<String> getSegments(String path) {
        String[] segments = path.split(PATH_SEPARATOR);
        return Arrays.asList(segments);
    }

    public static String build(List<String> list, boolean endingSlash) {
        StringBuilder builder = new StringBuilder();
        for (String l : list) {
            builder.append(PATH_SEPARATOR).append(l);
        }
        if (endingSlash) {
            builder.append(PATH_SEPARATOR);
        }
        return builder.toString();
    }

    public static String build(List<String> list) {
        return build(list, false);
    }

    public static int getCount(String path) {
        return getSegments(path).size();
    }

    public static String removeFirstSegment(String path) {
         List<String> list = getSegments(path);
        if (list.size() != 0) {
            list.remove(0);
            return build(list);
        } else {
            return PATH_SEPARATOR;
        }
    }

    public static String removeLastSegment(String path) {
        List<String> list = getSegments(path);
        if (list.size() != 0) {
            list.remove(list.size() - 1);
            return build(list);
        } else {
            return PATH_SEPARATOR;
        }
    }

    public static String appendSegment(String path, String element) {
        String copy = path;
        if (! copy.endsWith(PATH_SEPARATOR)) {
            copy += PATH_SEPARATOR;
        }
        copy += element;
        return copy;
    }

    public static String preprendSegment(String path, String element) {
        String copy = path;
        if (! copy.startsWith(PATH_SEPARATOR)) {
            copy  = PATH_SEPARATOR + copy;
        }
        copy = element + copy;
        return copy;
    }
}
