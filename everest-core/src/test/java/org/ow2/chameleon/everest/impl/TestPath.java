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

import org.ow2.chameleon.everest.services.Path;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

import static org.ow2.chameleon.everest.services.Path.from;
import static org.ow2.chameleon.everest.services.Path.fromElements;
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
        assertThat((Object) from("/everest").add(from("/ipojo"))).isEqualTo(from("/everest/ipojo"));
        assertThat((Object) from("/ipojo").add(from("/everest"))).isEqualTo(from("/ipojo/everest"));
        assertThat((Object) from("/ipojo").add(from("/"))).isEqualTo(from("/ipojo"));
        assertThat((Object) from("/").add(from("/ipojo"))).isEqualTo(from("/ipojo"));
    }

    @Test
    public void testAddElements() {
        assertThat((Object) from("/").addElements()).isEqualTo(from("/"));
        assertThat((Object) from("/").addElements("abc")).isEqualTo(from("/abc"));
        assertThat((Object) from("/").addElements("abc", "def")).isEqualTo(from("/abc/def"));
        assertThat((Object) from("/abc").addElements()).isEqualTo(from("/abc"));
        assertThat((Object) from("/abc").addElements("def")).isEqualTo(from("/abc/def"));
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

    @Test
    public void testIsAncestorOf() {
        assertThat(from("/").isAncestorOf(from("/"))).isFalse();
        assertThat(from("/").isAncestorOf(from("/abc"))).isTrue();
        assertThat(from("/").isAncestorOf(from("/abc/def"))).isTrue();
        assertThat(from("/abc").isAncestorOf(from("/"))).isFalse();
        assertThat(from("/abc").isAncestorOf(from("/abc"))).isFalse();
        assertThat(from("/abc").isAncestorOf(from("/abc/def"))).isTrue();
        assertThat(from("/abc").isAncestorOf(from("/abc/def/ghi"))).isTrue();
        assertThat(from("/abc/def").isAncestorOf(from("/abc/def/ghi"))).isTrue();
        assertThat(from("/abc/def/ghi").isAncestorOf(from("/abc/def/ghi"))).isFalse();
    }

    @Test
    public void testIsDescendantOf() {
        assertThat(from("/").isDescendantOf(from("/"))).isFalse();
        assertThat(from("/abc").isDescendantOf(from("/"))).isTrue();
        assertThat(from("/abc/def").isDescendantOf(from("/"))).isTrue();
        assertThat(from("/").isDescendantOf(from("/abc"))).isFalse();
        assertThat(from("/abc").isDescendantOf(from("/abc"))).isFalse();
        assertThat(from("/abc/def").isDescendantOf(from("/abc"))).isTrue();
        assertThat(from("/abc/def/ghi").isDescendantOf(from("/abc"))).isTrue();
        assertThat(from("/abc/def/ghi").isDescendantOf(from("/abc/def"))).isTrue();
        assertThat(from("/abc/def/ghi").isDescendantOf(from("/abc/def/ghi"))).isFalse();
    }

    @Test
    public void testFromElements() {
        assertThat((Object) fromElements()).isEqualTo(from("/"));
        assertThat((Object) fromElements("abc")).isEqualTo(from("/abc"));
        assertThat((Object) fromElements("abc", "def")).isEqualTo(from("/abc/def"));
        assertThat((Object) fromElements("abc", "def", "ghi")).isEqualTo(from("/abc/def/ghi"));
        try {
            // Invalid element : contains '/'
            fromElements("abc", "def/ghi", "jkl");
            Assert.fail();
        } catch (IllegalArgumentException e)  {
            //Ok!
        }
        try {
            // Invalid element : contains empty string
            fromElements("abc", "", "def", "ghi");
            Assert.fail();
        } catch (IllegalArgumentException e)  {
            //Ok!
        }
    }

    @Test(expected = NullPointerException.class)
    public void testFromNullPath() {
        Path root = from((String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromDoubleSlashPath() {
        Path root = from("/a//b/c");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromTrailingSlashPath() {
        Path root = from("/a/b/c/");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromMissingLeadingSlashPath() {
        Path root = from("a/b/c");
    }

    @Test
    public void testComparable() {
        List<Path> paths = new ArrayList<Path>(5);
        paths.add(from("/"));
        paths.add(from("/a/b/c"));
        paths.add(from("/abc/def/ghi"));
        paths.add(from("/abc/xyz/ghi"));
        paths.add(from("/abc"));
        paths.add(from("/abc/def"));
        paths.add(from("/abc/xyz"));
        // Shake and sort
        Collections.shuffle(paths);
        Collections.sort(paths);
        assertThat(paths).isEqualTo(Arrays.asList(new Path[] {from("/"), from("/a/b/c"), from("/abc"), from("/abc/def"), from("/abc/def/ghi"), from("/abc/xyz"), from("/abc/xyz/ghi")}));
    }

}
