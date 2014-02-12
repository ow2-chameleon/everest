package org.ow2.chameleon.everest.ipojo.test;

import org.ow2.chameleon.everest.osgi.log.LogEntryResource;
import org.ow2.chameleon.everest.services.IllegalActionOnResourceException;
import org.ow2.chameleon.everest.services.Resource;
import org.ow2.chameleon.everest.services.ResourceNotFoundException;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 5/30/13
 * Time: 12:44 AM
 */
public class TestLog extends EverestOsgiTest {


    @Test
    public void testLogResource() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource<?> logs = get("/osgi/logs");
        assertThat(logs).isNotNull();
        assertThat(logs.getResources()).isNotEmpty();
        for (Resource<?> log : logs.getResources()) {
            LogEntryResource logEntryResource = log.adaptTo(LogEntryResource.class);
            assertThat(logEntryResource).isNotNull();
        }

    }

}
