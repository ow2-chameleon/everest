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

package org.ow2.chameleon.everest.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import org.ow2.chameleon.everest.impl.beans.AnotherBean;
import org.ow2.chameleon.everest.impl.beans.MyBean;
import org.ow2.chameleon.everest.services.Action;
import org.ow2.chameleon.everest.services.Path;
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
