package org.ow2.everest.client.test;

import com.google.inject.Inject;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.ow2.chameleon.everest.client.EverestClient;
import org.ow2.chameleon.everest.client.EverestListener;
import org.ow2.chameleon.everest.client.ListResourceContainer;
import org.ow2.chameleon.everest.core.Everest;
import org.ow2.chameleon.everest.services.*;
import org.junit.Test;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

@ExamReactorStrategy(PerMethod.class)
public class TestFunctionality extends CommonTest {

    @Inject
    EventAdmin eventAdmin;

    @Test
    public void testRetrieve() throws ResourceNotFoundException, IllegalActionOnResourceException {
        EverestClient testAPI = new EverestClient(getContext());
        try{
            System.out.println( "Surface INT" + testAPI.read("/test/zone/room1").retrieve("Surface",Integer.class));
        }catch(Exception e ){
            assertThat(true).isEqualTo(false);
        }
        try{
            System.out.println( "Surface FLOAT" + testAPI.read("/test/zone/room1").retrieve("Surface",Float.class));
            assertThat(true).isEqualTo(false);
        }catch(Exception e ){
        }

        try{
            System.out.println( "Surface NUMBER" + testAPI.read("/test/zone/room1").retrieve("Surface",Number.class));
            System.out.println( "Surface VALUE TO DOUBLE"  + testAPI.read("/test/zone/room1").retrieve("Surface",Number.class).doubleValue());
        }catch(Exception e ){
            assertThat(true).isEqualTo(false);
        }


    }

    @Test
    public void testBundleContext() throws ResourceNotFoundException, IllegalActionOnResourceException {

        EverestClient testAPI = new EverestClient(getContext());
        assertThat(testAPI.read("/test").retrieve("Name")).isEqualTo("test");

    }

    @Test
    public void testRead() throws ResourceNotFoundException, IllegalActionOnResourceException {

        EverestClient testAPI = new EverestClient(getContext(),everest);
        assertThat(testAPI.read("/test").retrieve("Name")).isEqualTo("test");

    }

    @Test
    public void testCreate() throws ResourceNotFoundException, IllegalActionOnResourceException {

        EverestClient testAPI = new EverestClient(getContext(),everest);

        assertThat(testAPI.create("/test/devices").with("serialNumber", "1100").doIt().retrieve("Serial Number")).isEqualTo("1100");

        assertThat(testAPI.read("/test/devices").create().with("serialNumber", "1111").doIt().retrieve("Serial Number")).isEqualTo("1111");

        Resource resource = testAPI.read("/test/devices").retrieve();

        assertThat(testAPI.create(resource).with("serialNumber", "1101").doIt().retrieve("Serial Number")).isEqualTo("1101");

        try{
            testAPI.read("/test").children().create().with("serialNumber", "2000").doIt().retrieve("Serial Number");
        } catch (IllegalActionOnResourceException e){
            assertThat(e.getResource()).isEqualTo(testAPI.read("/test/person").retrieve());
        }
    }

    @Test
    public void testChildren() throws ResourceNotFoundException, IllegalActionOnResourceException {

        EverestClient testAPI = new EverestClient(getContext(),everest);

        List<Resource> resourceList = testAPI.read("/test").children().retrieve();
        for (Resource current : resourceList) {
            System.out.println(current.getPath().getLast());
        }

        System.out.println("Son of Son");

        resourceList = testAPI.read("/test").children().children().retrieve();

        for (Resource current : resourceList) {
            System.out.println(current.getPath().getLast());
            System.out.println(current.getPath());
        }
        Resource resource = testAPI.read("/test/devices").child("1").retrieve();

        resourceList = testAPI.read("/test").children().child("room1").retrieve();


        List<String> temp = new ArrayList<String>();
        temp = testAPI.read("/test").children().child("room1").retrieve("Name");
        assertThat(temp.get(0)).isEqualTo("room1");

        temp = testAPI.read("/test").children().child("room1").retrieve("Name",String.class);
        assertThat(temp.get(0)).isEqualTo("room1");

        assertThat(resourceList.size()).isEqualTo(1);
        resourceList = testAPI.read("/test").children().child("device1").retrieve();
        assertThat(resourceList).isEqualTo(null);

        resourceList = testAPI.read("/test/devices/1").children().retrieve();
        assertThat(resourceList).isEqualTo(null);

        resourceList = testAPI.read("/test/devices/1").children().child("device1").retrieve();
        assertThat(resourceList).isEqualTo(null);
    }


    @Test
    public void testUpdate() throws ResourceNotFoundException, IllegalActionOnResourceException {
        System.out.println("TEST UPDATE");
        EverestClient testAPI = new EverestClient(getContext(),everest);

        List<Resource> resourceList = testAPI.read("/test/devices").children().update().with("STATE_DEACTIVATED", "TRUE").doIt().retrieve();


        assertThat(testAPI.read("/test/devices/1").retrieve("State Deactivated")).isEqualTo("TRUE");
        assertThat(testAPI.read("/test/devices/2").retrieve("State Deactivated")).isEqualTo("TRUE");
        assertThat(testAPI.read("/test/devices/3").retrieve("State Deactivated")).isEqualTo("TRUE");

        Resource resource = testAPI.read("/test/devices/3").update().with("STATE_DEACTIVATED", "FALSE").doIt().retrieve();
        System.out.println(resource.getPath());
        System.out.println(resource.getMetadata());
        assertThat(testAPI.read("/test/devices/3").retrieve("State Deactivated")).isEqualTo("FALSE");
        resource = testAPI.read("/test/devices/3").update().with("STATE_DEACTIVATED", "FALSE").with("State Unknown", "TRUE").with("State Activated", "ACTIVATED").doIt().retrieve();
        System.out.println(resource.getPath());
        System.out.println(resource.getMetadata());
        resource = testAPI.update().with("STATE_DEACTIVATED", "TRUE").with("State Unknown", "UNKNOWN").with("State Activated", "TRUE").doIt().retrieve();
        System.out.println(resource.getPath());
        System.out.println(resource.getMetadata());
        assertThat(testAPI.read("/test/devices/3").retrieve("State Deactivated")).isEqualTo("TRUE");
        assertThat(testAPI.read("/test/devices/3").retrieve("State Unknown")).isEqualTo("UNKNOWN");
        assertThat(testAPI.read("/test/devices/3").retrieve("State Activated")).isEqualTo("TRUE");
        resource = testAPI.update(resource).with("STATE_DEACTIVATED", "FALSE").with("State Unknown", "FALSE").with("State Activated", "FALSE").doIt().retrieve();
        assertThat(testAPI.read("/test/devices/3").retrieve("State Deactivated")).isEqualTo("FALSE");
        assertThat(testAPI.read("/test/devices/3").retrieve("State Unknown")).isEqualTo("FALSE");
        assertThat(testAPI.read("/test/devices/3").retrieve("State Activated")).isEqualTo("FALSE");

        testAPI.update("/test/devices/3").with("STATE_DEACTIVATED", "TRUE").doIt();
        assertThat(testAPI.read("/test/devices/3").retrieve("State Deactivated")).isEqualTo("TRUE");
    }

    @Test
    public void testDelete() throws ResourceNotFoundException, IllegalActionOnResourceException {
        System.out.println("TEST DELETE");
        EverestClient testAPI = new EverestClient(getContext(),everest);

        testAPI.read("/test/zone/room1").delete().doIt();


        assertThat(testAPI.read("/test/zone").child("room1").retrieve()).isEqualTo(null);

        testAPI.delete(testAPI.read("/test/zone/room2").retrieve()).doIt();
        assertThat(testAPI.read("/test/zone").child("room2").retrieve()).isEqualTo(null);
        testAPI.read("/test/zone").children().delete().doIt();


        assertThat(testAPI.read("/test/zone").children().retrieve()).isEqualTo(null);

    }

    @Test
    public void testDeleteClient() throws ResourceNotFoundException, IllegalActionOnResourceException {
        System.out.println("TEST DELETE");
        EverestClient testAPI = new EverestClient(getContext(),everest);

        testAPI.delete("/test/zone/room2").doIt();
        assertThat(testAPI.read("/test/zone").child("room2").retrieve()).isEqualTo(null);
    }


    @Test
    public void testRelation() throws ResourceNotFoundException, IllegalActionOnResourceException {
        System.out.println("TEST Relation");
        EverestClient testAPI = new EverestClient(getContext(),everest);

        Resource resource = testAPI.read("/test/devices").relation("create").retrieve();
        System.out.println(resource.getPath());

        List<Resource> listResource = testAPI.read("/test/devices").relations().retrieve();

        listResource = testAPI.read("/test").children().relations().retrieve();

        System.out.println("RESULT");
        for (Resource current : listResource) {
            System.out.println(current.getPath().getLast());
        }

        testAPI.read("/test/devices");

        listResource = testAPI.relations().retrieve();
        listResource = testAPI.read("/test").children().relation("create").retrieve();

        assertThat(listResource.size()).isEqualTo(2);

    }

    @Test
    public void testParent() throws ResourceNotFoundException, IllegalActionOnResourceException {
        System.out.println("TEST Parent");
        EverestClient testAPI = new EverestClient(getContext(),everest);

        Resource resource = testAPI.read("/test/zone").parent().retrieve();
        assertThat(resource.getPath().getLast()).isEqualTo("test");
        System.out.println(resource.getPath().getLast());

        resource = testAPI.parent().retrieve();

        assertThat(resource.getPath().getLast()).isEqualTo("test");
        System.out.println(resource.getPath().getLast());

        List<Resource> listResource = testAPI.read("/test").children().parent().retrieve();

        for (Resource current : listResource) {
            assertThat(current.getPath().getLast()).isEqualTo("test");
            System.out.println(current.getPath().getLast());
        }
    }

    @Test
    public void testAssertResource() throws ResourceNotFoundException, IllegalActionOnResourceException {
        EverestClient testAPI = new EverestClient(getContext(),everest);

        assertThat(testAPI.assertThat(testAPI.read("/test/zone").retrieve()).exist()).isEqualTo(true);

        assertThat(testAPI.assertThat(testAPI.read("/test/zone").retrieve()).isEqualTo(testAPI.read("/test/zone").retrieve())).isEqualTo(true);
        assertThat(testAPI.assertThat(testAPI.read("/test/zone").retrieve()).isEqualTo(testAPI.read("/test/zone/room1").retrieve())).isEqualTo(false);

        assertThat(testAPI.assertThat(testAPI.read("/test/zone").retrieve("Name")).isEqualTo("zone")).isEqualTo(true);
        assertThat(testAPI.assertThat(testAPI.read("/test/zone").retrieve("Name")).isEqualTo("canard")).isEqualTo(false);
    }

    @Test
    public void testGetter() throws ResourceNotFoundException, IllegalActionOnResourceException {
        EverestClient testAPI = new EverestClient(getContext(),everest);

        testAPI.read("/test/devices").create();
        assertThat( testAPI.read("/test/devices").getM_currentAction()).isEqualTo(Action.READ);
        assertThat( testAPI.read("/test/devices").create().getM_currentAction()).isEqualTo(Action.CREATE);

        assertThat( testAPI.read("/test/devices").with("Serial Number","1010").getM_currentParams().containsKey("Serial Number")).isEqualTo(true);
        assertThat( testAPI.read("/test/devices").with("Serial Number","1010").getM_currentParams().get("Serial Number")).isEqualTo("1010");

    }

    @Test
    public void testgetAll() throws ResourceNotFoundException, IllegalActionOnResourceException {
        EverestClient testAPI = new EverestClient(getContext(),everest);
        List<Resource> resourceList = testAPI.getAllResource();

    }

    @Test
    public void testEventHandler() throws ResourceNotFoundException, IllegalActionOnResourceException {
        System.out.println("TEST Event Handler");
        EverestClient testAPI = new EverestClient(getContext(),everest);
        String request = "sggsdd" ;
        String[] topics = new String[]{"everest/*"};


        testAPI.subscribe(new TestListener(), request, topics);
//        EventAdmin e = getContext().getService(getContext().getServiceReference(EventAdmin.class));
//        e.sendEvent(new Event("everest/tata", new HashMap<String, Object>()));

        Everest.postResource(ResourceEvent.UPDATED, testAPI.read("/test/devices").retrieve());

        testAPI.read("/test/devices").children().update().with("STATE_DEACTIVATED", "TRUE").doIt().retrieve();
    }


    public class TestListener implements EverestListener{

        public TestListener(){

        }

        public void getNewResult(ListResourceContainer resourceContainer) {
            System.out.println("GET NEW RESULT ");
        }
    }
}