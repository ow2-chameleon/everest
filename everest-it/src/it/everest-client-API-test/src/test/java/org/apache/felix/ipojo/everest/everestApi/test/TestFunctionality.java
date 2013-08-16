package org.apache.felix.ipojo.everest.everestApi.test;

import org.apache.felix.ipojo.everest.client.api.EverestClient;
import org.apache.felix.ipojo.everest.services.IllegalActionOnResourceException;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.services.ResourceNotFoundException;
import org.junit.Test;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

@ExamReactorStrategy(PerMethod.class)
public class TestFunctionality extends CommonTest {


    @Test
    public void testRead() throws ResourceNotFoundException, IllegalActionOnResourceException {

        EverestClient testAPI = new EverestClient(everest);
        assertThat(testAPI.read("/test").retrieve("Name")).isEqualTo("test");

    }

    @Test
    public void testCreate() throws ResourceNotFoundException, IllegalActionOnResourceException {

        EverestClient testAPI = new EverestClient(everest);

        assertThat(testAPI.create("/test/devices").with("serialNumber", "1100").doIt().retrieve("Serial Number")).isEqualTo("1100");

        assertThat(testAPI.read("/test/devices").create().with("serialNumber", "1111").doIt().retrieve("Serial Number")).isEqualTo("1111");

        Resource resource = testAPI.read("/test/devices").retrieve();

        assertThat(testAPI.create(resource).with("serialNumber", "1101").doIt().retrieve("Serial Number")).isEqualTo("1101");
    }

    @Test
    public void testChildren() throws ResourceNotFoundException, IllegalActionOnResourceException {

        EverestClient testAPI = new EverestClient(everest);

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

    }


    @Test
    public void testUpdate() throws ResourceNotFoundException, IllegalActionOnResourceException {
        System.out.println("TEST UPDATE");
        EverestClient testAPI = new EverestClient(everest);

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
    }

    @Test
    public void testDelete() throws ResourceNotFoundException, IllegalActionOnResourceException {
        System.out.println("TEST DELETE");
        EverestClient testAPI = new EverestClient(everest);

        testAPI.read("/test/zone/room1").delete().doIt();


        assertThat(testAPI.read("/test/zone").child("room1").retrieve()).isEqualTo(null);


        testAPI.read("/test/zone").children().delete().doIt();


        assertThat(testAPI.read("/test/zone").children().retrieve()).isEqualTo(null);

    }

    @Test
    public void testRelation() throws ResourceNotFoundException, IllegalActionOnResourceException {
        System.out.println("TEST Relation");
        EverestClient testAPI = new EverestClient(everest);

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
        for (Resource current : listResource) {
            System.out.println(current.getPath().getLast());
        }

    }

    @Test
    public void testParent() throws ResourceNotFoundException, IllegalActionOnResourceException {
        System.out.println("TEST Parent");
        EverestClient testAPI = new EverestClient(everest);

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

}