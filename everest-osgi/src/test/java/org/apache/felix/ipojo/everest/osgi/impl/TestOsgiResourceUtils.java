package org.apache.felix.ipojo.everest.osgi.impl;

import org.junit.Test;
import org.osgi.framework.Bundle;

import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.bundleStateToString;
import static org.apache.felix.ipojo.everest.osgi.OsgiResourceUtils.toBundleState;
import static org.fest.assertions.Assertions.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 5/24/13
 * Time: 10:14 AM
 */
public class TestOsgiResourceUtils {

    @Test
    public void testBundleStateSerialization() {
        assertThat(toBundleState(bundleStateToString(Bundle.ACTIVE))).isEqualTo(Bundle.ACTIVE);
        assertThat(toBundleState(bundleStateToString(Bundle.INSTALLED))).isEqualTo(Bundle.INSTALLED);
        assertThat(toBundleState(bundleStateToString(Bundle.RESOLVED))).isEqualTo(Bundle.RESOLVED);
        assertThat(toBundleState(bundleStateToString(Bundle.STARTING))).isEqualTo(Bundle.STARTING);
        assertThat(toBundleState(bundleStateToString(Bundle.STOPPING))).isEqualTo(Bundle.STOPPING);
        assertThat(toBundleState(bundleStateToString(Bundle.UNINSTALLED))).isEqualTo(Bundle.UNINSTALLED);
        try {
            assertThat(toBundleState("1234"));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            assertThat(e).isNotNull();
        }
        try {
            assertThat(bundleStateToString(1234));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            assertThat(e).isNotNull();
        }

    }

    @Test
    public void testLogLevelToString() {

    }


}
