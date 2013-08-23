package org.ow2.chameleon.query.test;

import org.junit.Test;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.ow2.chameleon.everest.client.EverestClient;
import org.ow2.chameleon.everest.client.ParserQuery;
import org.ow2.chameleon.everest.services.IllegalActionOnResourceException;
import org.ow2.chameleon.everest.services.Resource;
import org.ow2.chameleon.everest.services.ResourceNotFoundException;
import org.ow2.chameleon.query.test.CommonTest;

import java.text.ParseException;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: colin
 * Date: 22/08/13
 * Time: 13:12
 * To change this template use File | Settings | File Templates.
 */

@ExamReactorStrategy(PerMethod.class)
public class TestQueryLanguage extends CommonTest {


    @Test
    public void testINoperation() throws ResourceNotFoundException {
        System.out.println("TEST IN STRING");
        EverestClient testAPI = new EverestClient(getContext());
        assertThat(testAPI.read("/test").retrieve("Name")).isEqualTo("test");
        String request = "{Name:{$in:[\"room1\",\"room2\"]}}";
        ParserQuery parserQuery = new ParserQuery(request);
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery.parseFilter()).retrieve();
            for (Resource resourceCurrent : resourceList){
                System.out.println(" Resource : " + resourceCurrent.getPath() + " SURFACE " + resourceCurrent.getMetadata() );
            }
        } catch (ParseException e) {
            e.printStackTrace();
            assertThat(true).isEqualTo(false);
        }  catch (Exception e1) {
            e1.printStackTrace();
            assertThat(true).isEqualTo(false);
        }

        System.out.println("TEST IN NUMBER");
        String request1 = "{Surface:{$in:[15,25,2]}}";
        ParserQuery parserQuery1 = new ParserQuery(request1);
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery1.parseFilter()).retrieve();
            for (Resource resourceCurrent : resourceList){
                System.out.println(" Resource : " + resourceCurrent.getPath() + " SURFACE " + resourceCurrent.getMetadata() );
            }
        } catch (ParseException e) {
            e.printStackTrace();
            assertThat(true).isEqualTo(false);
        }  catch (Exception e1) {
            e1.printStackTrace();
            assertThat(true).isEqualTo(false);
        }
    }


    @Test
    public void testSingleValueRequest() throws ResourceNotFoundException {

        EverestClient testAPI = new EverestClient(getContext());
        assertThat(testAPI.read("/test").retrieve("Name")).isEqualTo("test");
        String request = "{Name:\"room1\"}";
        ParserQuery parserQuery = new ParserQuery(request);
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery.parseFilter()).retrieve();
            for (Resource resourceCurrent : resourceList){
                System.out.println(" Resource : " + resourceCurrent.getPath()  );
            }
        } catch (ParseException e) {
            assertThat(false).isEqualTo(true);
        }  catch (Exception e1) {
            assertThat(false).isEqualTo(true);
        }

        request = "{Surface:15}";
        ParserQuery parserQuery1 = new ParserQuery(request);
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery1.parseFilter()).retrieve();
            for (Resource resourceCurrent : resourceList){
                System.out.println(" Resource : " + resourceCurrent.getPath()  );
            }
        } catch (ParseException e) {
            assertThat(false).isEqualTo(true);
        }  catch (Exception e1) {
            assertThat(false).isEqualTo(true);
        }

        request = "{Surface:15}}";
        ParserQuery parserQuery2 = new ParserQuery(request);
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery2.parseFilter()).retrieve();
            assertThat(false).isEqualTo(true);
        } catch (ParseException e) {

        }  catch (Exception e1) {

        }
        request = "{Surface:15]}";
        ParserQuery parserQuery3 = new ParserQuery(request);
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery3.parseFilter()).retrieve();
            assertThat(false).isEqualTo(true);
        } catch (ParseException e) {

        }  catch (Exception e1) {

        }
    }

    @Test
    public void testNumericComparaison() throws ResourceNotFoundException {
        EverestClient testAPI = new EverestClient(getContext());
        assertThat(testAPI.read("/test").retrieve("Name")).isEqualTo("test");
        String request = "{Surface:{$lt:20}}";
        ParserQuery parserQuery = new ParserQuery(request);
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery.parseFilter()).retrieve();
            for (Resource resourceCurrent : resourceList){
                System.out.println(" Resource : " + resourceCurrent.getPath() + " SURFACE " + resourceCurrent.getMetadata() );
            }
        } catch (ParseException e) {
            assertThat(false).isEqualTo(true);
        }  catch (Exception e1) {
            assertThat(false).isEqualTo(true);
        }
    }

    @Test
    public void testStringComparaison() throws ResourceNotFoundException {
        EverestClient testAPI = new EverestClient(getContext());
        assertThat(testAPI.read("/test").retrieve("Name")).isEqualTo("test");
        String request = "{Name:{$start:\"room\"}}";
        ParserQuery parserQuery = new ParserQuery(request);
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery.parseFilter()).retrieve();
            for (Resource resourceCurrent : resourceList){
                System.out.println(" Resource : " + resourceCurrent.getPath() + " SURFACE " + resourceCurrent.getMetadata() );
            }
        } catch (ParseException e) {
            assertThat(false).isEqualTo(true);
        }  catch (Exception e1) {
            assertThat(false).isEqualTo(true);
        }

        String request1 = "{Name:{$start:room}}";
        ParserQuery parserQuery1 = new ParserQuery(request1);
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery1.parseFilter()).retrieve();
            assertThat(false).isEqualTo(true);
        } catch (ParseException e) {

        }  catch (Exception e1) {

        }

        request = "{Name:{$start:{&lt:room}}}";
        ParserQuery parserQuery2 = new ParserQuery(request);
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery2.parseFilter()).retrieve();
            assertThat(false).isEqualTo(true);
        } catch (ParseException e) {

        }  catch (Exception e1) {

        }

    }

    @Test
    public void testAggregationValue() throws ResourceNotFoundException {

        System.out.println("TEST AGGREGATION");
        EverestClient testAPI = new EverestClient(getContext());
        assertThat(testAPI.read("/test").retrieve("Name")).isEqualTo("test");
        String request = "{Name:{{$start:\"room\"},{$end:\"1\"}}}";
        ParserQuery parserQuery = new ParserQuery(request);
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery.parseFilter()).retrieve();
            for (Resource resourceCurrent : resourceList){
                System.out.println(" Resource : " + resourceCurrent.getPath() + " SURFACE " + resourceCurrent.getMetadata() );
            }
        } catch (ParseException e) {
            assertThat(false).isEqualTo(true);
        }  catch (Exception e1) {
            assertThat(false).isEqualTo(true);
        }

        request = "{Name:{{$or:[{$end:\"2\"},{$end:\"1\"}]}}}";
        ParserQuery parserQuery1 = new ParserQuery(request);
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery1.parseFilter()).retrieve();
            for (Resource resourceCurrent : resourceList){
                System.out.println(" Resource : " + resourceCurrent.getPath() + " SURFACE " + resourceCurrent.getMetadata() );
            }
        } catch (ParseException e) {
            assertThat(false).isEqualTo(true);
        }  catch (Exception e1) {
            assertThat(false).isEqualTo(true);
        }


        request = "{Name:{{$or:[{or:\"2\"},{$end:\"1\"}]}}}";
        ParserQuery parserQuery2 = new ParserQuery(request);
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery2.parseFilter()).retrieve();
            assertThat(false).isEqualTo(true);
        } catch (ParseException e) {

        }  catch (Exception e1) {

        }

        request = "{Name:{{$or:[{$or:\"2\"},{$end:\"1\"}]}}}";
        ParserQuery parserQuery3 = new ParserQuery(request);
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery3.parseFilter()).retrieve();
            assertThat(false).isEqualTo(true);
        } catch (ParseException e) {

        } catch (Exception e1) {

        }


    }

    @Test
    public void testAggregationAndFilter() throws ResourceNotFoundException {
        System.out.println("TEST AGGREGATION AND FILTER");
        EverestClient testAPI = new EverestClient(getContext());
        assertThat(testAPI.read("/test").retrieve("Name")).isEqualTo("test");
        String request = "{{Name:\"room3\"},{Surface:15}}";
        ParserQuery parserQuery = new ParserQuery(request);
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery.parseFilter()).retrieve();
            for (Resource resourceCurrent : resourceList){
                System.out.println(" Resource : " + resourceCurrent.getPath() + " SURFACE " + resourceCurrent.getMetadata() );
            }
        } catch (ParseException e) {

            assertThat(true).isEqualTo(false);
        }  catch (Exception e1) {

            assertThat(true).isEqualTo(false);
        }

        request = "{{Name:\"room3\"},{Surface:25}}";
        ParserQuery parserQuery1 = new ParserQuery(request);
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery1.parseFilter()).retrieve();
            for (Resource resourceCurrent : resourceList){
                System.out.println(" Resource : " + resourceCurrent.getPath() + " SURFACE " + resourceCurrent.getMetadata() );
            }
            assertThat(true).isEqualTo(false);
        } catch (ParseException e) {
            assertThat(true).isEqualTo(false);
        }  catch (Exception e1) {

        }

        request = "{{Name:{{$or:[{$end:\"2\"},{$end:\"1\"}]}}},{Surface:{$lt:20}}}";

        ParserQuery parserQuery2 = new ParserQuery(request);
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery2.parseFilter()).retrieve();
            for (Resource resourceCurrent : resourceList){
                System.out.println(" Resource : " + resourceCurrent.getPath() + " SURFACE " + resourceCurrent.getMetadata() );
            }

        } catch (ParseException e) {
            assertThat(true).isEqualTo(false);
        }  catch (Exception e1) {
            assertThat(true).isEqualTo(false);
        }
    }

    @Test
    public void testAggregationOrFilter() throws ResourceNotFoundException {
        System.out.println("TEST AGGREGATION OR FILTER");
        EverestClient testAPI = new EverestClient(getContext());
        assertThat(testAPI.read("/test").retrieve("Name")).isEqualTo("test");
        String request = "{$or:[{Name:\"room3\"},{Surface:4}]}";
        ParserQuery parserQuery = new ParserQuery(request);
        System.out.println("JUST ROOM 3 + ROOM 2");
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery.parseFilter()).retrieve();
            for (Resource resourceCurrent : resourceList){
                System.out.println(" Resource : " + resourceCurrent.getPath() + " SURFACE " + resourceCurrent.getMetadata() );
            }
        } catch (ParseException e) {

            assertThat(true).isEqualTo(false);
        }  catch (Exception e1) {

            assertThat(true).isEqualTo(false);
        }

        request = "{{$or:[{Name:\"room3\"},{Surface:4}]},{Name:\"room3\"}}";
        ParserQuery parserQuery2 = new ParserQuery(request);
        System.out.println("\n ROOM 3");
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery2.parseFilter()).retrieve();
            for (Resource resourceCurrent : resourceList){
                System.out.println(" Resource : " + resourceCurrent.getPath() + " SURFACE " + resourceCurrent.getMetadata() );
            }
        } catch (ParseException e) {

            assertThat(true).isEqualTo(false);
        }  catch (Exception e1) {

            assertThat(true).isEqualTo(false);
        }

        request = "{{$or:[{Name:\"room3\"},{$or:[{Surface:4},{Surface:25}]}]}}";
        ParserQuery parserQuery3 = new ParserQuery(request);
        System.out.println("\n ROOM 3 + ROOM 1 + ROOM 2");
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery3.parseFilter()).retrieve();
            for (Resource resourceCurrent : resourceList){
                System.out.println(" Resource : " + resourceCurrent.getPath() + " SURFACE " + resourceCurrent.getMetadata() );
            }
        } catch (ParseException e) {

            e.printStackTrace();
        }  catch (Exception e1) {

            e1.printStackTrace();
        }
    }


    @Test
    public void testExistFilter() throws ResourceNotFoundException {
        System.out.println("TEST EXIST");
        EverestClient testAPI = new EverestClient(getContext());
        assertThat(testAPI.read("/test").retrieve("Name")).isEqualTo("test");
        String request = "{Name:$exist}";
        ParserQuery parserQuery = new ParserQuery(request);
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery.parseFilter()).retrieve();
            for (Resource resourceCurrent : resourceList){
                System.out.println(" Resource : " + resourceCurrent.getPath() + " SURFACE " + resourceCurrent.getMetadata() );
            }
        } catch (ParseException e) {

            assertThat(true).isEqualTo(false);
        }  catch (Exception e1) {

            assertThat(true).isEqualTo(false);
        }

        request = "{{$or:[{Name:$exist},{Surface:4}]}}";
        ParserQuery parserQuery1 = new ParserQuery(request);
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery1.parseFilter()).retrieve();
            for (Resource resourceCurrent : resourceList){
                System.out.println(" Resource : " + resourceCurrent.getPath() + " SURFACE " + resourceCurrent.getMetadata() );
            }
        } catch (ParseException e) {

            assertThat(true).isEqualTo(false);
        }  catch (Exception e1) {

            assertThat(true).isEqualTo(false);
        }

    }

}
