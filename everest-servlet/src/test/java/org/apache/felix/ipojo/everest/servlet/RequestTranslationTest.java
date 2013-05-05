package org.apache.felix.ipojo.everest.servlet;

import com.google.common.collect.ImmutableMap;
import org.apache.felix.ipojo.everest.impl.DefaultRequest;
import org.apache.felix.ipojo.everest.services.Action;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test the mapping between the HTTP Request and the Everest request.
 */
public class RequestTranslationTest {

    @Test
    public void testGETTranslation() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getPathInfo()).thenReturn("/resource");

        DefaultRequest translated = EverestServlet.translate(request);
        assertThat(translated.action()).isEqualTo(Action.READ);
    }

    @Test
    public void testPOSTTranslation() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("POST");
        when(request.getPathInfo()).thenReturn("/resource");

        DefaultRequest translated = EverestServlet.translate(request);
        assertThat(translated.action()).isEqualTo(Action.UPDATE);
    }

    @Test
    public void testPUTTranslation() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("PUT");
        when(request.getPathInfo()).thenReturn("/resource");

        DefaultRequest translated = EverestServlet.translate(request);
        assertThat(translated.action()).isEqualTo(Action.CREATE);
    }

    @Test
    public void testHEADTranslation() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("HEAD");
        when(request.getPathInfo()).thenReturn("/resource");

        DefaultRequest translated = EverestServlet.translate(request);
        assertThat(translated.action()).isEqualTo(Action.READ);
    }

    @Test
    public void testDeleteTranslation() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("DELETE");
        when(request.getPathInfo()).thenReturn("/resource");

        DefaultRequest translated = EverestServlet.translate(request);
        assertThat(translated.action()).isEqualTo(Action.DELETE);
    }

    /**
     * Reproducing everest #2 : Conversion problem with request parameters in everest-servlet
     * {@link https://github.com/bourretp/everest/issues/2}
     */
    @Test
    public void testParameterFlatteningForIssue2() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("PUT");
        when(request.getPathInfo()).thenReturn("/ipojo/factory/Foo/1.2.3.foo");
        when(request.getParameterMap()).thenReturn(
                new ImmutableMap.Builder<String, String[]>().put("instance.name", new String[] {"FooBarBaz"}).build()
        );

        DefaultRequest translated = EverestServlet.translate(request);
        assertThat(translated.get("instance.name", String.class)).isEqualTo("FooBarBaz");

    }

    @Test
    public void testParameterFlattening() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("PUT");
        when(request.getPathInfo()).thenReturn("/ipojo/factory/Foo/1.2.3.foo");
        when(request.getParameterMap()).thenReturn(
                new ImmutableMap.Builder<String, String[]>()
                        .put("instance.name", new String[]{"FooBarBaz"})
                        .put("flag", new String[0])
                        .put("properties", new String[]{"p1", "p2"})
                        .build()
        );

        DefaultRequest translated = EverestServlet.translate(request);
        assertThat(translated.get("instance.name", String.class)).isEqualTo("FooBarBaz");
        assertThat(translated.get("properties", List.class)).contains("p1").contains("p2");
        assertThat(translated.get("null", String.class)).isNull();
        assertThat(translated.get("flag", String.class)).isEqualTo("true");

    }

}
