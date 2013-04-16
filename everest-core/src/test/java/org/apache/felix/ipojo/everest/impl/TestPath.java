package org.apache.felix.ipojo.everest.impl;

import org.apache.felix.ipojo.everest.services.Path;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Checks the behavior of {@code Path}es.
 */
public class TestPath {

    @Test
    public void testRoot() {
        Path root = Path.from("/");
        assertThat(root.getParent()).isNull();
        assertThat(root.isAbsolute()).isTrue();
        assertThat(root.getElementCount()).isEqualTo(0);
    }

    @Test
    public void testAbsoluteABC() {
        Path root = Path.from("/a/b/c");
        assertThat(root.getParent().toString()).isEqualTo("/a/b");
        assertThat(root.isAbsolute()).isTrue();
        assertThat(root.getElementCount()).isEqualTo(3);
    }

    @Test
    public void testRelativeABC() {
        Path root = Path.from("a/b/c");
        assertThat(root.getParent().toString()).isEqualTo("a/b");
        assertThat(root.isAbsolute()).isFalse();
        assertThat(root.getElementCount()).isEqualTo(3);
    }

    @Test(expected = NullPointerException.class)
    public void testNullPath() {
        Path root = Path.from(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDoubleSlashPath() {
        Path root = Path.from("/a//b/c");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTrailingSlashPath() {
        Path root = Path.from("/a/b/c/");
    }

}
