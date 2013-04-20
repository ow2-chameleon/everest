package org.apache.felix.ipojo.everest.servlet;

import com.google.common.collect.ImmutableMap;
import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.impl.ImmutableResourceMetadata;
import org.apache.felix.ipojo.everest.services.IllegalResourceException;
import org.apache.felix.ipojo.everest.services.Resource;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
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
        StringWriter writer = new StringWriter();
        String s = "this is a string";
        Resource resource = new DefaultResource.Builder()
                .fromPath("/foo")
                .with(new ImmutableResourceMetadata.Builder().set("data", s).build())
                .build();

        servlet.toJSON(resource, writer);
        assertThat(writer.toString()).isEqualToIgnoringCase("{\"data\":\"" + s + "\"}");
    }

    @Test
    public void serializationOfNumbers() throws IllegalResourceException, IOException {
        StringWriter writer = new StringWriter();
        Resource resource = new DefaultResource.Builder()
                .fromPath("/foo")
                .with(new ImmutableResourceMetadata.Builder()
                        .set("int", 1)
                        .set("double", 1d)
                        .build())
                .build();

        servlet.toJSON(resource, writer);
        assertThat(writer.toString()).contains("\"int\":" + 1);
        assertThat(writer.toString()).contains("\"double\":" + 1.0);
    }

    @Test
    public void serializationOfBooleans() throws IllegalResourceException, IOException {
        StringWriter writer = new StringWriter();
        Resource resource = new DefaultResource.Builder()
                .fromPath("/foo")
                .with(new ImmutableResourceMetadata.Builder()
                        .set("right", true)
                        .set("wrong", false)
                        .build())
                .build();

        servlet.toJSON(resource, writer);
        assertThat(writer.toString()).contains("\"right\":" + true);
        assertThat(writer.toString()).contains("\"wrong\":" + false);
    }

    @Test
    public void serializationOfSimpleLists() throws IllegalResourceException, IOException {
        StringWriter writer = new StringWriter();
        List<String> l1 = Arrays.asList("a", "b", "c");
        List<Integer> l2 = Arrays.asList(1, 2, 3);
        Resource resource = new DefaultResource.Builder()
                .fromPath("/foo")
                .with(new ImmutableResourceMetadata.Builder()
                        .set("l1", l1)
                        .set("l2", l2)
                        .build())
                .build();

        servlet.toJSON(resource, writer);
        assertThat(writer.toString()).contains("\"l1\":[\"a\",\"b\",\"c\"]");
        assertThat(writer.toString()).contains("\"l2\":[1,2,3]");
    }

    @Test
    public void serializationOfArrays() throws IllegalResourceException, IOException {
        StringWriter writer = new StringWriter();
        String[] a1 = new String[]{"a", "b", "c"};
        int[] a2 = new int[]{1, 2, 3};
        Resource resource = new DefaultResource.Builder()
                .fromPath("/foo")
                .with(new ImmutableResourceMetadata.Builder()
                        .set("l1", a1)
                        .set("l2", a2)
                        .build())
                .build();

        servlet.toJSON(resource, writer);
        assertThat(writer.toString()).contains("\"l1\":[\"a\",\"b\",\"c\"]");
        assertThat(writer.toString()).contains("\"l2\":[1,2,3]");
    }

    @Test
    public void serializationOfSimpleSets() throws IllegalResourceException, IOException {
        StringWriter writer = new StringWriter();
        List<String> l1 = Arrays.asList("a", "b", "c");
        List<Integer> l2 = Arrays.asList(1, 2, 3);
        Resource resource = new DefaultResource.Builder()
                .fromPath("/foo")
                .with(new ImmutableResourceMetadata.Builder()
                        .set("l1", new LinkedHashSet(l1))
                        .set("l2", new LinkedHashSet(l2))
                        .build())
                .build();

        servlet.toJSON(resource, writer);
        assertThat(writer.toString()).contains("\"l1\":[\"a\",\"b\",\"c\"]");
        assertThat(writer.toString()).contains("\"l2\":[1,2,3]");
    }

    @Test
    public void serializationOfMaps() throws IllegalResourceException, IOException {
        StringWriter writer = new StringWriter();
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

        servlet.toJSON(resource, writer);
        assertThat(writer.toString()).contains("\"m1\":{\"one\":\"1\",\"two\":\"2\"}");
        assertThat(writer.toString()).contains("\"m2\":{\"one\":1,\"two\":2}");
    }

    @Test
    public void serializationOfComplexList() throws IllegalResourceException, IOException {
        StringWriter writer = new StringWriter();
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

        servlet.toJSON(resource, writer);
        assertThat(writer.toString()).isEqualToIgnoringCase(
                "{\"object\":[" +
                        "{\"a1\":[1,2,3],\"a2\":[4,5,6]}," +
                        "{\"b1\":[9,8,7],\"b2\":[6,5,4]}" +
                        "]}");
    }
}
