package org.apache.felix.ipojo.everest.ipojo;

import org.apache.felix.ipojo.metadata.Attribute;
import org.apache.felix.ipojo.metadata.Element;

import java.util.*;

/**
 * everest iPOJO utility class.
 */
public class IpojoUtil {

    /**
     * Avoid instantiation.
     */
    private IpojoUtil() {
    }

    /**
     * Convert the given iPOJO element to an everest resource metadata.
     *
     * @param element the element to convert.
     * @return the resource metadata representing the given element.
     */
    public static Map<String, Object> elementToMap(Element element) {

        Map<String, Object> map = new LinkedHashMap<String, Object>();

        // Store the element's attributes in the map.
        if (element.getAttributes() != null) {
            for (Attribute attr : element.getAttributes()) {
                String name = attr.getName();
                if (attr.getNameSpace() != null) {
                    name = attr.getNameSpace() + ':' + name;
                }
                map.put(name, attr.getValue());
            }
        }

        // For each child of the element, grouped by qualified name.
        for (Element e : element.getElements()) {
            String qName = getQualifiedName(e);
            if (map.containsKey(qName)) {
                // Child already processed!
                continue;
            }
            List<Map<String, Object>> group = new ArrayList<Map<String, Object>>();
            for (Element child : element.getElements(qName)) {
                group.add(elementToMap(child));
            }
            map.put(qName, Collections.unmodifiableList(group));
        }

        return Collections.unmodifiableMap(map);
    }

    private static String getQualifiedName(Element e) {
        if (e.getNameSpace() == null) {
            return e.getName();
        } else {
            return e.getNameSpace() + ':' + e.getName();
        }
    }

}
