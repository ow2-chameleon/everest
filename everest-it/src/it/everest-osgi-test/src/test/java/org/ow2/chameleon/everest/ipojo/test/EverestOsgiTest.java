package org.ow2.chameleon.everest.ipojo.test;

import org.ow2.chameleon.everest.impl.DefaultRequest;
import org.ow2.chameleon.everest.services.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.CompositeOption;
import org.ops4j.pax.exam.options.DefaultCompositeOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.ow2.chameleon.testing.helpers.BaseTest;

import javax.inject.Inject;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import static junit.framework.Assert.assertNotNull;
import static org.fest.assertions.Assertions.assertThat;
import static org.ops4j.pax.exam.CoreOptions.*;

/**
 * Bootstrap the test from this project
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class EverestOsgiTest extends BaseTest {

    // Event lists
    LinkedList<Event> createdEvents = new LinkedList<Event>();
    LinkedList<Event> updatedEvents = new LinkedList<Event>();
    LinkedList<Event> deletedEvents = new LinkedList<Event>();

    @Inject
    EverestService everest;

    @Inject
    EventAdmin eventAdmin;

    @Override
    protected Option[] getCustomOptions() {
        return options(
                systemProperty("ipojo.processing.synchronous").value("true"),
                systemProperty("everest.processing.synchronous").value("true"),
                everestBundles(),
                osgiBundles(),
                festBundles()
        );
    }

    @Override
    public boolean deployConfigAdmin() {
        return true;
    }

    @Override
    public boolean deployTestBundle() {
        return true;
    }

    @Before
    public void commonSetUp() {
        super.commonSetUp();
        registerEventHandler();
        // create bundles
    }

    @After
    public void commonTearDown() {
        ipojoHelper.dispose();
        osgiHelper.dispose();
    }

    public CompositeOption everestBundles() {
        return new DefaultCompositeOption(
                mavenBundle("org.ow2.chameleon.everest", "everest-core").versionAsInProject(),
                mavenBundle("org.ow2.chameleon.everest", "everest-ipojo").versionAsInProject(),
                mavenBundle("org.ow2.chameleon.everest", "everest-osgi").versionAsInProject(),
                mavenBundle("org.ow2.chameleon.everest", "everest-system").versionAsInProject()
        );
    }

    public CompositeOption osgiBundles() {
        return new DefaultCompositeOption(
                mavenBundle("org.apache.felix", "org.apache.felix.eventadmin").versionAsInProject(),
                mavenBundle("org.apache.felix", "org.apache.felix.log").versionAsInProject()
        );
    }

    // Wrap fest-util and fest-assert into a bundle so we can test with happiness.
    public CompositeOption festBundles() {
        return new DefaultCompositeOption(
                wrappedBundle(mavenBundle("org.easytesting", "fest-util").versionAsInProject()),
                wrappedBundle(mavenBundle("org.easytesting", "fest-assert").versionAsInProject())
        );
    }

    public Resource get(String path) throws ResourceNotFoundException, IllegalActionOnResourceException {
        return everest.process(new DefaultRequest(Action.READ, Path.from(path), null));
    }

    public Resource update(Path path, Map<String, Object> params) throws ResourceNotFoundException, IllegalActionOnResourceException {
        return everest.process(new DefaultRequest(Action.UPDATE, path, params));
    }

    public Resource create(Path path, Map<String, Object> params) throws ResourceNotFoundException, IllegalActionOnResourceException {
        return everest.process(new DefaultRequest(Action.CREATE, path, params));
    }

    public Resource delete(Path path) throws ResourceNotFoundException, IllegalActionOnResourceException {
        return everest.process(new DefaultRequest(Action.DELETE, path, null));
    }

    /**
     * Check that the EverestService service is present.
     * <p>
     * This test also avoids the test container to complain with a "no test method" error.
     * </p>
     */
    @Test
    public void testEverestServiceIsPresent() {
        assertNotNull(everest);
    }

    protected void registerEventHandler() {
        Hashtable<String, Object> props = new Hashtable<String, Object>();
        props.put(EventConstants.EVENT_TOPIC, new String[]{"everest/osgi/*", "everest/osgi"});
        osgiHelper.getContext().registerService(EventHandler.class.getName(), new EventHandler() {
            public void handleEvent(Event event) {
                Object eventType = event.getProperty("eventType");
                //System.out.println(eventType + " " + event.getProperty("canonicalPath"));
                if (ResourceEvent.CREATED.toString().equals(eventType)) {
                    createdEvents.add(event);
                } else if (ResourceEvent.DELETED.toString().equals(eventType)) {
                    deletedEvents.add(event);
                } else if (ResourceEvent.UPDATED.toString().equals(eventType)) {
                    updatedEvents.add(event);
                }
            }
        }, props);

    }

    public void testUpdatedEventFrom(String resourcePath) throws ResourceNotFoundException, IllegalActionOnResourceException {
        Iterator<Event> iterator = updatedEvents.descendingIterator();
        boolean fromResource = false;
        while (iterator.hasNext() && !fromResource) {
            Event next = iterator.next();
            String canonicalPath = (String) next.getProperty("canonicalPath");
            if (canonicalPath.equals(resourcePath)) {
                fromResource = true;
            }
        }
        assertThat(fromResource).isTrue();
    }

    public void testCreatedEventFrom(String resourcePath) throws ResourceNotFoundException, IllegalActionOnResourceException {
        Iterator<Event> iterator = createdEvents.descendingIterator();
        boolean fromResource = false;
        while (iterator.hasNext() && !fromResource) {
            Event next = iterator.next();
            String canonicalPath = (String) next.getProperty("canonicalPath");
            if (canonicalPath.equals(resourcePath)) {
                fromResource = true;
            }
        }
        assertThat(fromResource).isTrue();
    }

    public void testDeletedEventFrom(String resourcePath) throws ResourceNotFoundException, IllegalActionOnResourceException {
        Iterator<Event> iterator = deletedEvents.descendingIterator();
        boolean fromResource = false;
        while (iterator.hasNext() && !fromResource) {
            Event next = iterator.next();
            String canonicalPath = (String) next.getProperty("canonicalPath");
            System.out.println(next.getTopic() + " " + canonicalPath);
            if (canonicalPath.equals(resourcePath)) {
                fromResource = true;
            }
        }
        assertThat(fromResource).isTrue();
    }

}
