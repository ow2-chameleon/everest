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

package org.ow2.chameleon.everest.servlet;

import com.google.common.collect.ImmutableMap;
import org.ow2.chameleon.everest.impl.DefaultResource;
import org.ow2.chameleon.everest.impl.ImmutableResourceMetadata;
import org.ow2.chameleon.everest.services.IllegalResourceException;
import org.ow2.chameleon.everest.services.Resource;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Test the behavior of the toJson methods
 */
public class ToJsonTest {


    private EverestServlet servlet;

    @Before
    public void setUp() {
        servlet = new EverestServlet();
    }

    @Test
    public void serializationOfString() throws IllegalResourceException, IOException {
         String s = "this is a string";
        Resource resource = new DefaultResource.Builder()
                .fromPath("/foo")
                .with(new ImmutableResourceMetadata.Builder().set("data", s).build())
                .build();

        String result = servlet.toJSON(resource).toString();
        assertThat(result).isEqualToIgnoringCase("{\"data\":\"" + s + "\"}");
    }

    @Test
    public void serializationOfNull() throws IllegalResourceException, IOException {
        Resource resource = new DefaultResource.Builder()
                .fromPath("/foo")
                .with(new ImmutableResourceMetadata.Builder().set("data", null).build())
                .build();

        String result = servlet.toJSON(resource).toString();
        assertThat(result).isEqualToIgnoringCase("{\"data\":null}");
    }

    @Test
    public void serializationOfNumbers() throws IllegalResourceException, IOException {
        Resource resource = new DefaultResource.Builder()
                .fromPath("/foo")
                .with(new ImmutableResourceMetadata.Builder()
                        .set("int", 1)
                        .set("double", 1d)
                        .build())
                .build();

        String result = servlet.toJSON(resource).toString();
        assertThat(result).contains("\"int\":" + 1);
        assertThat(result).contains("\"double\":" + 1.0);
    }

    @Test
    public void serializationOfBooleans() throws IllegalResourceException, IOException {
        Resource resource = new DefaultResource.Builder()
                .fromPath("/foo")
                .with(new ImmutableResourceMetadata.Builder()
                        .set("right", true)
                        .set("wrong", false)
                        .build())
                .build();

        String result = servlet.toJSON(resource).toString();
        assertThat(result).contains("\"right\":" + true);
        assertThat(result).contains("\"wrong\":" + false);
    }

    @Test
    public void serializationOfSimpleLists() throws IllegalResourceException, IOException {
        List<String> l1 = Arrays.asList("a", "b", "c");
        List<Integer> l2 = Arrays.asList(1, 2, 3);
        Resource resource = new DefaultResource.Builder()
                .fromPath("/foo")
                .with(new ImmutableResourceMetadata.Builder()
                        .set("l1", l1)
                        .set("l2", l2)
                        .build())
                .build();

        String result = servlet.toJSON(resource).toString();
        assertThat(result).contains("\"l1\":[\"a\",\"b\",\"c\"]");
        assertThat(result).contains("\"l2\":[1,2,3]");
    }

    @Test
    public void serializationOfArrays() throws IllegalResourceException, IOException {
        String[] a1 = new String[]{"a", "b", "c"};
        int[] a2 = new int[]{1, 2, 3};
        Resource resource = new DefaultResource.Builder()
                .fromPath("/foo")
                .with(new ImmutableResourceMetadata.Builder()
                        .set("l1", a1)
                        .set("l2", a2)
                        .build())
                .build();

        String result = servlet.toJSON(resource).toString();
        assertThat(result).contains("\"l1\":[\"a\",\"b\",\"c\"]");
        assertThat(result).contains("\"l2\":[1,2,3]");
    }

    @Test
    public void serializationOfSimpleSets() throws IllegalResourceException, IOException {
        List<String> l1 = Arrays.asList("a", "b", "c");
        List<Integer> l2 = Arrays.asList(1, 2, 3);
        Resource resource = new DefaultResource.Builder()
                .fromPath("/foo")
                .with(new ImmutableResourceMetadata.Builder()
                        .set("l1", new LinkedHashSet(l1))
                        .set("l2", new LinkedHashSet(l2))
                        .build())
                .build();

        String result = servlet.toJSON(resource).toString();
        assertThat(result).contains("\"l1\":[\"a\",\"b\",\"c\"]");
        assertThat(result).contains("\"l2\":[1,2,3]");
    }

    @Test
    public void serializationOfMaps() throws IllegalResourceException, IOException {
        ImmutableMap<String, String> mapOfString = new ImmutableMap.Builder<String, String>()
                .put("one", "1")
                .put("two", "2")
                .build();
        ImmutableMap<String, Integer> mapOfInt = new ImmutableMap.Builder<String, Integer>()
                .put("one", 1)
                .put("two", 2)
                .build();

        Resource resource = new DefaultResource.Builder()
                .fromPath("/foo")
                .with(new ImmutableResourceMetadata.Builder()
                        .set("m1", mapOfString)
                        .set("m2", mapOfInt)
                        .build())
                .build();

        String result = servlet.toJSON(resource).toString();
        assertThat(result).contains("\"m1\":{\"one\":\"1\",\"two\":\"2\"}");
        assertThat(result).contains("\"m2\":{\"one\":1,\"two\":2}");
    }

    @Test
    public void serializationOfComplexList() throws IllegalResourceException, IOException {
        List<ImmutableMap<String, List<Integer>>> l1 = Arrays.asList(
                new ImmutableMap.Builder<String, List<Integer>>()
                        .put("a1", Arrays.asList(1, 2, 3))
                        .put("a2", Arrays.asList(4, 5, 6)).build(),
                new ImmutableMap.Builder<String, List<Integer>>()
                        .put("b1", Arrays.asList(9, 8, 7))
                        .put("b2", Arrays.asList(6, 5, 4)).build()
        );

        Resource resource = new DefaultResource.Builder()
                .fromPath("/foo")
                .with(new ImmutableResourceMetadata.Builder()
                        .set("object", l1)
                        .build())
                .build();

        String result = servlet.toJSON(resource).toString();
        assertThat(result).isEqualToIgnoringCase(
                "{\"object\":[" +
                        "{\"a1\":[1,2,3],\"a2\":[4,5,6]}," +
                        "{\"b1\":[9,8,7],\"b2\":[6,5,4]}" +
                        "]}");
    }
}
