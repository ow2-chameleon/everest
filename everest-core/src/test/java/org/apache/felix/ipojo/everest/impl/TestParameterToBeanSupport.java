package org.apache.felix.ipojo.everest.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import org.apache.felix.ipojo.everest.impl.beans.AnotherBean;
import org.apache.felix.ipojo.everest.impl.beans.MyBean;
import org.apache.felix.ipojo.everest.services.Action;
import org.apache.felix.ipojo.everest.services.Path;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Test the ability to transform a json string to a bean
 */
public class TestParameterToBeanSupport {


    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testBeaninifcation() {
        ObjectNode node = objectMapper
                .createObjectNode()
                .put("message", "a message")
                .put("count", 1);
        node.putArray("names").add("a").add("b").add("c");

        DefaultRequest request = new DefaultRequest(
                Action.READ,
                Path.from("/foo"),
                new ImmutableMap.Builder<String, String>()
                        .put("test",
                                node.toString()).build()
        );

        MyBean bean = request.get("test", MyBean.class);
        assertThat(bean.getCount()).isEqualTo(1);
        assertThat(bean.getMessage()).isEqualTo("a message");
        assertThat(bean.getNames()).contains("a").contains("b").contains("c");
    }

    @Test
    public void testBeaninificationWithNestedBean() {
        ObjectNode nested = objectMapper
                .createObjectNode()
                .put("message", "a message")
                .put("count", 1);
        nested.putArray("names").add("a").add("b").add("c");

        ObjectNode node = objectMapper
                .createObjectNode()
                .put("name", "clement")
                .put("id", 1);
        node.put("bean", nested);

        System.out.println(node.toString());

        DefaultRequest request = new DefaultRequest(
                Action.READ,
                Path.from("/foo"),
                new ImmutableMap.Builder<String, String>()
                        .put("test",
                                node.toString()).build()
        );

        AnotherBean bean = request.get("test", AnotherBean.class);
        assertThat(bean.getId()).isEqualTo(1);
        assertThat(bean.getName()).isEqualTo("clement");
        assertThat(bean.getBean().getCount()).isEqualTo(1);
        assertThat(bean.getBean().getMessage()).isEqualTo("a message");
        assertThat(bean.getBean().getNames()).contains("a").contains("b").contains("c");

    }
}
