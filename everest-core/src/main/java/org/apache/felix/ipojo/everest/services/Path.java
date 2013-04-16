package org.apache.felix.ipojo.everest.services;

import java.util.*;

/**
 * An object that may be used to locate a resource.
 */
public class Path implements  Iterable<String> {

    /**
     * The Everest path separator
     */
    public static final String SEPARATOR = "/";

    /**
     * Flag indicating if this path is absolute (i.e. begins with '/')
     */
    private final boolean m_isAbsolute;

    /**
     * The elements of this path.
     */
    private final String[] m_elements;

    /**
     * The string representation of this path.
     */
    private final String m_string;

    /**
     * The root path.
     */
    public static final Path ROOT = new Path(true, new String[0], "/");


    private Path(boolean isAbsolute, String[] elements, String string) {
        m_isAbsolute = isAbsolute;
        m_elements = elements;
        m_string = string;
    }

    private Path(boolean isAbsolute, String[] elements) {
        this(isAbsolute, elements, toString(isAbsolute, elements));
    }

    /**
     * Returns an element of this path.
     * @param index the index of the element
     * @return the number of elements in the path
     * @throws IndexOutOfBoundsException if {@code index} is negative, or {@code index}  is greater than or equal to the number of elements
     */
    public String getElement(int index) throws IndexOutOfBoundsException {
        return m_elements[index];
    }

    /**
     * Returns the parent path, or {@code null} if this path does not have a parent.
     * @return a path representing the path's parent
     */
    public Path getParent() {
        if (m_elements.length == 0) {
            return null;
        }
        String[] elements = new String[m_elements.length -1];
        System.arraycopy(m_elements, 0, elements, 0, m_elements.length -1);
        return new Path(m_isAbsolute, elements);
    }

    /**
     * @return {@code true} if and only if this path is absolute
     */
    public boolean isAbsolute() {
        return m_isAbsolute;
    }

    /**
     * @return the number of elements in the path
     */
    public int getElementCount() {
        return m_elements.length;
    }

    /**
     * @return the elements of this path
     */
    public String[] getElements() {
        return m_elements.clone();
    }

    @Override
    public Iterator<String> iterator() {
        return Collections.unmodifiableList(Arrays.asList(m_elements)).iterator();
    }

    @Override
    public String toString() {
        return m_string;
    }

    private static String toString(boolean isAbsolute, String[] elements) {
        StringBuilder sb = new StringBuilder();
        if (isAbsolute) {
            sb.append(SEPARATOR);
        }
        if (elements.length != 0) {
            for (int i = 0; i < elements.length; i++) {
                sb.append(elements[i]);
                sb.append(SEPARATOR);
            }
            // Delete trailing separator
            sb.deleteCharAt(sb.length() -1);
        }
        return sb.toString();
    }

    public static Path from(String pathName) {
        if (pathName == null) {
            throw new NullPointerException("null pathName");
        }

        // A path is absolute iff it begins with a slash
        boolean isAbsolute = pathName.startsWith(SEPARATOR);
        if (isAbsolute) {
            // Remove the leading slash so it won't disturb the path analysis.
            pathName = pathName.substring(1);
            if (pathName.isEmpty()) {
                // This is the root.
                return ROOT;
            }
        }

        // Cut the path into elements.
        String[] elements = pathName.split(SEPARATOR);

        // Check that there are no empty element (caused by double slash) and that there is no trailing slash
        if (pathName.endsWith(SEPARATOR) || Arrays.asList(elements).contains("")) {
            throw new IllegalArgumentException("invalid pathName: " + pathName);
        }

        return new Path(isAbsolute, elements, pathName);
    }

}
