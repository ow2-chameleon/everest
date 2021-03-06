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

import org.ow2.chameleon.everest.services.Action;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;

/**
 * Checks the behavior of the ImmutableResourceMetadata
 */
public class TestImmutableResourceMetadata {


    @Test
    public void testBuilder() {
        ImmutableResourceMetadata irm =
                new ImmutableResourceMetadata.Builder()
                        .set("string", "hello")
                        .set("number", 1)
                        .set("array", new String[]{"a", "r", "r", "a", "y"})
                        .set("list", Arrays.asList("l", "i", "s", "t"))
                        .build();

        assertThat(irm.get("string")).isEqualTo("hello");
        assertThat(irm.get("list", List.class)).contains("l");
    }

    @Test
    public void testOf() {
        ImmutableResourceMetadata irm =
                new ImmutableResourceMetadata.Builder()
                        .set("string", "hello")
                        .set("number", 1)
                        .set("array", new String[]{"a", "r", "r", "a", "y"})
                        .set("list", Arrays.asList("l", "i", "s", "t"))
                        .build();

        ImmutableResourceMetadata irm2 = ImmutableResourceMetadata.of(irm);

        assertThat(irm2.get("string")).isEqualTo("hello");
        assertThat(irm2.get("list", List.class)).contains("l");
    }

    @Test
    public void testImmutability() {
        ImmutableResourceMetadata irm =
                new ImmutableResourceMetadata.Builder()
                        .set("string", "hello")
                        .set("number", 1)
                        .set("array", new String[]{"a", "r", "r", "a", "y"})
                        .set("list", Arrays.asList("l", "i", "s", "t"))
                        .build();

        try {
            irm.clear();
            fail("Cannot clear");
        } catch (UnsupportedOperationException e) { }

        try {
            irm.put("foo", "bar");
            fail("Cannot populate");
        } catch (UnsupportedOperationException e) { }

        try {
            irm.remove("string");
            fail("Cannot remove");
        } catch (UnsupportedOperationException e) { }

        int orig = irm.size();
        try {
            irm.entrySet().add(new Map.Entry<String, Object>() {
                public String getKey() { return "foo"; }

                public Object getValue() { return "bar"; }

                public Object setValue(Object value) { return null;  }
            });
            fail("Cannot populate");
        } catch (UnsupportedOperationException e) {}
        assertThat(orig).isEqualTo(irm.size());
    }

    @Test
    public void testGet() {
        ImmutableResourceMetadata irm =
                new ImmutableResourceMetadata.Builder()
                        .set("string", "hello")
                        .set("number", 1)
                        .set("action", Action.READ)
                        .set("array", new String[]{"a", "r", "r", "a", "y"})
                        .set("list", Arrays.asList("l", "i", "s", "t"))
                        .build();

        assertThat(irm.get("string")).isEqualTo("hello");
        assertThat(irm.get("number", Integer.class)).isEqualTo(1);
        assertThat(irm.get("action", Action.class)).isEqualTo(Action.READ);
        assertThat(irm.get("array", (new String[0]).getClass())).contains("y");
        assertThat(irm.get("list", List.class)).contains("l");

        assertThat(irm.get("not-in-map")).isNull();
        // Wrong type.
        try {
            irm.get("string", List.class);
            fail("string is not a list");
        } catch (IllegalArgumentException e) { }

        // Uber type
        assertThat(irm.get("string", Object.class)).isEqualTo("hello");

    }

}
