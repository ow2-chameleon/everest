package org.apache.felix.ipojo.everest.impl;

import org.apache.felix.ipojo.everest.services.Path;
import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.apache.felix.ipojo.everest.services.Path.from;
import static org.fest.assertions.Assertions.assertThat;

/**
 * Checks the behavior of {@code Path}es.
 */
public class TestPath {

    @Test
    public void testRoot() {
        Path root = from("/");
        assertThat(root.getElements()).isEmpty();
        assertThat(root.getCount()).isEqualTo(0);
        assertThat(root.getParent()).isNull();
        assertThat(root.toString()).isEqualTo("/");
    }

    @Test
    public void testGetElements() {
        assertThat(from("/").getElements()).isEqualTo(new String[] {});
        assertThat(from("/abc").getElements()).isEqualTo(new String[] {"abc"});
        assertThat(from("/abc/def").getElements()).isEqualTo(new String[] {"abc", "def"});
        assertThat(from("/abc/def/ghi").getElements()).isEqualTo(new String[] {"abc", "def", "ghi"});
    }

    @Test
    public void testGetCount() {
        assertThat(from("/").getCount()).isEqualTo(0);
        assertThat(from("/abc").getCount()).isEqualTo(1);
        assertThat(from("/abc/def").getCount()).isEqualTo(2);
        assertThat(from("/abc/def/ghi").getCount()).isEqualTo(3);
    }

    @Test
    public void testGetElement() {
        Path path = from("/abc/def/ghi");
        assertThat(path.getElement(0)).isEqualTo("abc");
        assertThat(path.getElement(1)).isEqualTo("def");
        assertThat(path.getElement(2)).isEqualTo("ghi");
        try {
            path.getElement(3);
            Assert.fail();
        } catch (IndexOutOfBoundsException e)  {
            //Ok!
        }
    }

    @Test
    public void testIterator() {
        Iterator<String> i = from("/abc/def/ghi").iterator();
        assertThat(i.hasNext()).isTrue();
        assertThat(i.next()).isEqualTo("abc");
        assertThat(i.hasNext()).isTrue();
        assertThat(i.next()).isEqualTo("def");
        assertThat(i.hasNext()).isTrue();
        assertThat(i.next()).isEqualTo("ghi");
        assertThat(i.hasNext()).isFalse();
        try {
            i.next();
            Assert.fail();
        } catch (NoSuchElementException e)  {
            //Ok!
        }
    }

    @Test
    public void testGetParent() {
        assertThat(from("/").getParent()).isNull();
        assertThat((Object) from("/abc").getParent()).isEqualTo(from("/"));
        assertThat((Object) from("/abc/def").getParent()).isEqualTo(from("/abc"));
        assertThat((Object) from("/abc/def/ghi").getParent()).isEqualTo(from("/abc/def"));
    }

    @Test
    public void testGetFirst() {
        assertThat(from("/abc").getFirst()).isEqualTo("abc");
        assertThat(from("/def/abc").getFirst()).isEqualTo("def");
        assertThat(from("/ghi/def/abc").getFirst()).isEqualTo("ghi");
        try {
            from("/").getFirst();
            Assert.fail();
        } catch (IndexOutOfBoundsException e)  {
            //Ok!
        }
    }

    @Test
    public void testGetLast() {
        assertThat(from("/abc").getLast()).isEqualTo("abc");
        assertThat(from("/abc/def").getLast()).isEqualTo("def");
        assertThat(from("/abc/def/ghi").getLast()).isEqualTo("ghi");
        try {
            from("/").getLast();
            Assert.fail();
        } catch (IndexOutOfBoundsException e)  {
            //Ok!
        }
    }

    @Test
    public void testGetHead() {
        Path path = from("/abc/def/ghi");
        assertThat((Object) path.getHead(0)).isEqualTo(from("/"));
        assertThat((Object) path.getHead(1)).isEqualTo(from("/abc"));
        assertThat((Object) path.getHead(2)).isEqualTo(from("/abc/def"));
        assertThat((Object) path.getHead(3)).isEqualTo(from("/abc/def/ghi"));
        try {
            path.getHead(4);
            Assert.fail();
        } catch (IndexOutOfBoundsException e)  {
            //Ok!
        }
    }

    @Test
    public void testGetTail() {
        Path path = from("/abc/def/ghi");
        assertThat((Object) path.getTail(0)).isEqualTo(from("/"));
        assertThat((Object) path.getTail(1)).isEqualTo(from("/ghi"));
        assertThat((Object) path.getTail(2)).isEqualTo(from("/def/ghi"));
        assertThat((Object) path.getTail(3)).isEqualTo(from("/abc/def/ghi"));
        try {
            path.getTail(4);
            Assert.fail();
        } catch (IndexOutOfBoundsException e)  {
            //Ok!
        }
    }

    @Test
    public void testAdd() {
        Path a = from("/everest");
        Path b = from("/ipojo");
        assertThat((Object) a.add(b)).isEqualTo(from("/everest/ipojo"));
        assertThat((Object) b.add(a)).isEqualTo(from("/ipojo/everest"));
    }

    @Test
    public void testSubtract() {
        Path a = from("/abc/def/ghi");
        assertThat((Object) a.subtract(from("/"))).isEqualTo(from("/abc/def/ghi"));
        assertThat((Object) a.subtract(from("/abc"))).isEqualTo(from("/def/ghi"));
        assertThat((Object) a.subtract(from("/abc/def"))).isEqualTo(from("/ghi"));
        assertThat((Object) a.subtract(from("/abc/def/ghi"))).isEqualTo(from("/"));
        try {
            a.subtract(from("/abc/def/ghi/jkl"));
            Assert.fail();
        } catch (IllegalArgumentException e)  {
            //Ok!
        }

    }

    @Test(expected = NullPointerException.class)
    public void testNullPath() {
        Path root = from(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDoubleSlashPath() {
        Path root = from("/a//b/c");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTrailingSlashPath() {
        Path root = from("/a/b/c/");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRelativePath() {
        Path root = from("a/b/c");
    }

}
