package org.apache.felix.ipojo.everest.servlet;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.felix.ipojo.everest.impl.DefaultParameter;
import org.apache.felix.ipojo.everest.impl.DefaultRelation;
import org.apache.felix.ipojo.everest.impl.DefaultResource;
import org.apache.felix.ipojo.everest.services.*;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test the serialization of relations
 */
public class RelationSerializationTest {

    @Test
    public void testRelationWithoutParameter() throws IllegalResourceException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getScheme()).thenReturn("http");
        when(request.getServerName()).thenReturn("server");
        when(request.getServerPort()).thenReturn(1234);

        Relation relation = new DefaultRelation(Path.from("/foo/bar"), Action.READ, "test", "description");
        Resource resource = new DefaultResource.Builder().fromPath(
                Path.from("/foo")).with(relation).build();
        EverestServlet servlet = new EverestServlet();
        JsonNode node = servlet.toJSON(request, resource);

        System.out.println(node);
        assertThat(node.get("__relations")).isNotNull();
        final JsonNode rel = node.get("__relations").get("test");
        assertThat(rel).isNotNull();
        assertThat(rel.get("href").asText()).isEqualTo
                ("http://server:1234/everest/foo/bar");
        assertThat(rel.get("action").asText()).isEqualTo
                ("READ");
        assertThat(rel.get("name").asText()).isEqualTo
                ("test");
        assertThat(rel.get("description").asText()).isEqualTo
                ("description");

    }

    @Test
    public void testRelationWithOneParameter() throws IllegalResourceException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getScheme()).thenReturn("http");
        when(request.getServerName()).thenReturn("server");
        when(request.getServerPort()).thenReturn(1234);

        Relation relation = new DefaultRelation(Path.from("/foo/bar"), Action.READ, "test", "description",
                new DefaultParameter().name("param").description("my param").type(String.class));
        Resource resource = new DefaultResource.Builder().fromPath(
                Path.from("/foo")).with(relation).build();
        EverestServlet servlet = new EverestServlet();
        JsonNode node = servlet.toJSON(request, resource);

        System.out.println(node);
        assertThat(node.get("__relations")).isNotNull();

        final JsonNode rel = node.get("__relations").get("test");
        assertThat(rel).isNotNull();
        assertThat(rel.get("href").asText()).isEqualTo
                ("http://server:1234/everest/foo/bar");
        assertThat(rel.get("action").asText()).isEqualTo
                ("READ");
        assertThat(rel.get("name").asText()).isEqualTo
                ("test");
        assertThat(rel.get("description").asText()).isEqualTo
                ("description");

        final JsonNode param0 = rel.get("parameters").get(0);
        assertThat(param0.get("name").asText()).isEqualTo("param");
        assertThat(param0.get("description").asText()).isEqualTo("my param");
        assertThat(param0.get("type").asText()).isEqualTo(String.class.getName());
        assertThat(param0.get("optional").asBoolean()).isFalse();
    }


    @Test
    public void testRelationWithTwoParameters() throws IllegalResourceException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getScheme()).thenReturn("http");
        when(request.getServerName()).thenReturn("server");
        when(request.getServerPort()).thenReturn(1234);

        Relation relation = new DefaultRelation(Path.from("/foo/bar"), Action.READ, "test", "description",
                new DefaultParameter().name("param").description("my param").type(String.class),
                new DefaultParameter().name("param2").description("my second param").type(Integer.TYPE).optional
                        (true));
        Resource resource = new DefaultResource.Builder().fromPath(
                Path.from("/foo")).with(relation).build();
        EverestServlet servlet = new EverestServlet();
        JsonNode node = servlet.toJSON(request, resource);

        System.out.println(node);
        assertThat(node.get("__relations")).isNotNull();

        final JsonNode rel = node.get("__relations").get("test");
        assertThat(rel).isNotNull();
        assertThat(rel.get("href").asText()).isEqualTo
                ("http://server:1234/everest/foo/bar");
        assertThat(rel.get("action").asText()).isEqualTo
                ("READ");
        assertThat(rel.get("name").asText()).isEqualTo
                ("test");
        assertThat(rel.get("description").asText()).isEqualTo
                ("description");

        final JsonNode param0 = rel.get("parameters").get(0);
        assertThat(param0.get("name").asText()).isEqualTo("param");
        assertThat(param0.get("description").asText()).isEqualTo("my param");
        assertThat(param0.get("type").asText()).isEqualTo(String.class.getName());
        assertThat(param0.get("optional").asBoolean()).isFalse();

        final JsonNode param1 = rel.get("parameters").get(1);
        assertThat(param1.get("name").asText()).isEqualTo("param2");
        assertThat(param1.get("description").asText()).isEqualTo("my second param");
        assertThat(param1.get("type").asText()).isEqualTo(Integer.TYPE.getName());
        assertThat(param1.get("optional").asBoolean()).isTrue();
    }

}
