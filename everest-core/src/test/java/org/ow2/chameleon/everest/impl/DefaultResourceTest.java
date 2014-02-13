package org.ow2.chameleon.everest.impl;

import org.junit.Test;
import org.ow2.chameleon.everest.services.Path;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by clement on 13/02/2014.
 */
public class DefaultResourceTest {


    @Test
    public void testDefaultResourceCreation() {
        DefaultResource res = new DefaultResource(Path.from("/"));
        assertThat(res).isNotNull();
        assertThat(res.getPath()).isEqualTo(Path.from("/"));
        assertThat(res.getMetadata()).isEmpty();
        assertThat(res.adaptTo(Object.class)).isNull();
        assertThat(res.getResources()).isEmpty();
    }
}
