package org.ow2.chameleon.query.test;

import org.junit.Test;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.ow2.chameleon.everest.client.EverestClient;
import org.ow2.chameleon.everest.query.ParseException;
import org.ow2.chameleon.everest.query.QueryFilter;
import org.ow2.chameleon.everest.services.IllegalActionOnResourceException;
import org.ow2.chameleon.everest.services.Relation;
import org.ow2.chameleon.everest.services.Resource;
import org.ow2.chameleon.everest.services.ResourceNotFoundException;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: colin
 * Date: 28/08/13
 * Time: 15:22
 * To change this template use File | Settings | File Templates.
 */
@ExamReactorStrategy(PerMethod.class)
public class TestQuery extends CommonTest{

    @Test
    public void testSimpleRequestParsing(){
        EverestClient testAPI = new EverestClient(getContext());
        QueryFilter filter = new QueryFilter("{Name:\"Toto\"}",testAPI.getM_everest());
        try {
            filter.input();
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void testSimpleRequestParsingWithSpace(){
        EverestClient testAPI = new EverestClient(getContext());
        QueryFilter filter = new QueryFilter("      {  Name :\"  Toto  \"}  ",testAPI.getM_everest());
        try {
            filter.input();
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    @Test
    public void testSimpleRequestNumberParsing(){
        EverestClient testAPI = new EverestClient(getContext());
        QueryFilter filter = new QueryFilter("{Name:80}",testAPI.getM_everest());
        try {
            filter.input();
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void testSimpleRequestNumberParsingWithSpace(){
        EverestClient testAPI = new EverestClient(getContext());
        QueryFilter filter = new QueryFilter("      {  Name : 10.0  }  ",testAPI.getM_everest());
        try {
            filter.input();
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    @Test
    public void testINStringArray() throws ResourceNotFoundException {
        System.out.println("TEST IN STRING");
        EverestClient testAPI = new EverestClient(getContext());
        assertThat(testAPI.read("/test").retrieve("Name")).isEqualTo("test");
        String request = "{Name:{$in:[\"room1\",\"room2\"]}}";
        QueryFilter parserQuery = new QueryFilter(request,testAPI.getM_everest());
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery.input()).retrieve();
            assertThat(resourceList).isNotEmpty();
            assertThat(resourceList).isNotNull();
            assertThat(resourceList).contains(testAPI.read("/test/zone/room1").retrieve());
            assertThat(resourceList).contains(testAPI.read("/test/zone/room2").retrieve());
            assertThat(resourceList).excludes(testAPI.read("/test/zone/room3").retrieve());
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
    public void testINNumberArray() throws ResourceNotFoundException {
        System.out.println("TEST IN NUMBER");
        EverestClient testAPI = new EverestClient(getContext());
        String request1 = "{Surface:{$in:[15,25,2]}}";
        QueryFilter parserQuery1 = new QueryFilter(request1,testAPI.getM_everest());
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery1.input()).retrieve();
            assertThat(resourceList).isNotEmpty();
            assertThat(resourceList).isNotNull();
            assertThat(resourceList).contains(testAPI.read("/test/zone/room1").retrieve());
            assertThat(resourceList).contains(testAPI.read("/test/zone/room3").retrieve());
            assertThat(resourceList).excludes(testAPI.read("/test/zone/room2").retrieve());
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
        System.out.println("\ntestSingleValueRequest");
        EverestClient testAPI = new EverestClient(getContext());
        assertThat(testAPI.read("/test").retrieve("Name")).isEqualTo("test");
        String request = "{Name:\"room1\"}";
        QueryFilter parserQuery = new QueryFilter(request,testAPI.getM_everest());
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery.input()).retrieve();
            assertThat(resourceList).isNotEmpty();
            assertThat(resourceList).isNotNull();
            for (Resource resourceCurrent : resourceList){
                System.out.println(" Resource : " + resourceCurrent.getPath()  );
            }
        } catch (ParseException e) {
            assertThat(false).isEqualTo(true);
        }  catch (Exception e1) {
            assertThat(false).isEqualTo(true);
        }
    }
    @Test
    public void testNumberValueRequest() throws ResourceNotFoundException {
        System.out.println("\ntestNumberValueRequest");
        EverestClient testAPI = new EverestClient(getContext());
        String request = "{Surface:15}";
        QueryFilter parserQuery1 = new QueryFilter(request,testAPI.getM_everest());
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery1.input()).retrieve();
            assertThat(resourceList).isNotEmpty();
            assertThat(resourceList).isNotNull();
            for (Resource resourceCurrent : resourceList){
                System.out.println(" Resource : " + resourceCurrent.getPath()  );
            }
        } catch (ParseException e) {
            assertThat(false).isEqualTo(true);
        }  catch (Exception e1) {
            assertThat(false).isEqualTo(true);
        }
    }

    @Test
    public void testNumberValueRequestWithMalformation() throws ResourceNotFoundException {
        System.out.println("\ntestNumberValueRequestWithMalformation");
        EverestClient testAPI = new EverestClient(getContext());
        String request = "{Surface:15}}";
        QueryFilter parserQuery2 = new QueryFilter(request,testAPI.getM_everest());
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery2.input()).retrieve();
            assertThat(false).isEqualTo(true);
        } catch (ParseException e) {

        }  catch (Exception e1) {

        }

        request = "{Surface:15]}";
        QueryFilter parserQuery3 = new QueryFilter(request,testAPI.getM_everest());
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery3.input()).retrieve();
            assertThat(false).isEqualTo(true);
        } catch (ParseException e) {

        }  catch (Exception e1) {

        }
    }

    @Test
    public void testNumericComparaison() throws ResourceNotFoundException {
        System.out.println("\ntestNumericComparaison");
        EverestClient testAPI = new EverestClient(getContext());
        assertThat(testAPI.read("/test").retrieve("Name")).isEqualTo("test");
        String request = "{Surface:{$lt:20}}";
        QueryFilter parserQuery = new QueryFilter(request,testAPI.getM_everest());
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery.input()).retrieve();
            assertThat(resourceList).isNotEmpty();
            assertThat(resourceList).isNotNull();
            assertThat(resourceList).contains(testAPI.read("/test/zone/room2").retrieve());
            assertThat(resourceList).contains(testAPI.read("/test/zone/room3").retrieve());
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
    public void testStringStart() throws ResourceNotFoundException {
        System.out.println("\ntestStringStart");
        EverestClient testAPI = new EverestClient(getContext());
        assertThat(testAPI.read("/test").retrieve("Name")).isEqualTo("test");
        String request = "{Name:{$start:\"room\"}}";
        QueryFilter parserQuery = new QueryFilter(request,testAPI.getM_everest());
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery.input()).retrieve();
            assertThat(resourceList).isNotEmpty();
            assertThat(resourceList).isNotNull();
            assertThat(resourceList).contains(testAPI.read("/test/zone/room2").retrieve());
            assertThat(resourceList).contains(testAPI.read("/test/zone/room3").retrieve());
            assertThat(resourceList).contains(testAPI.read("/test/zone/room1").retrieve());
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
    public void testStringStartWithoutBrackets() throws ResourceNotFoundException {
        System.out.println("\ntestStringStartWithoutBrackets");
        EverestClient testAPI = new EverestClient(getContext());
        String request1 = "{Name:{$start:room}}";
        QueryFilter parserQuery1 = new QueryFilter(request1,testAPI.getM_everest());
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery1.input()).retrieve();
            assertThat(false).isEqualTo(true);
        } catch (ParseException e) {

        }  catch (Exception e1) {

        }
    }

    @Test
    public void testNumberLtWithNoNumberValue() throws ResourceNotFoundException {
        System.out.println("\ntestStringStartWithoutBrackets");
        EverestClient testAPI = new EverestClient(getContext());
        String request = "{Name:{$start:{&lt:room}}}";
        QueryFilter parserQuery2 = new QueryFilter(request,testAPI.getM_everest());
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery2.input()).retrieve();
            assertThat(false).isEqualTo(true);
        } catch (ParseException e) {

        }  catch (Exception e1) {

        }

    }

    @Test
    public void testAggregationNumberSubFilter() throws ResourceNotFoundException {

        System.out.println("\ntestAggregationNumberSubFilter");
        EverestClient testAPI = new EverestClient(getContext());
        assertThat(testAPI.read("/test").retrieve("Name")).isEqualTo("test");
        String request = "{Name:{{$start:\"room\"},{$end:\"1\"}}}";
        QueryFilter parserQuery = new QueryFilter(request,testAPI.getM_everest());
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery.input()).retrieve();
            assertThat(resourceList).isNotEmpty();
            assertThat(resourceList).isNotNull();
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
    public void testAggregationStringSubFilter() throws ResourceNotFoundException {
        System.out.println("\ntestAggregationStringSubFilter");
        EverestClient testAPI = new EverestClient(getContext());
        String request = "{Name:{{$or:[{$end:\"2\"},{$end:\"1\"}]}}}";
        QueryFilter parserQuery1 = new QueryFilter(request,testAPI.getM_everest());
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery1.input()).retrieve();
            assertThat(resourceList).isNotEmpty();
            assertThat(resourceList).isNotNull();
            assertThat(resourceList).contains(testAPI.read("/test/zone/room2").retrieve());
            assertThat(resourceList).contains(testAPI.read("/test/zone/room1").retrieve());
            assertThat(resourceList).excludes(testAPI.read("/test/zone/room3").retrieve());
            for (Resource resourceCurrent : resourceList){
                System.out.println(" Resource : " + resourceCurrent.getPath() + " SURFACE " + resourceCurrent.getMetadata() );
            }
        } catch (ParseException e) {
            e.printStackTrace();
            assertThat(false).isEqualTo(true);
        }  catch (Exception e1) {
            e1.printStackTrace();
            assertThat(false).isEqualTo(true);
        }
    }

    @Test
    public void testAggregationStringSubFilterWithMissing$() throws ResourceNotFoundException {
        System.out.println("\ntestAggregationStringSubFilterWithMissing$");
        EverestClient testAPI = new EverestClient(getContext());
        String request = "{Name:{{$or:[{or:\"2\"},{$end:\"1\"}]}}}";
        QueryFilter parserQuery2 = new QueryFilter(request,testAPI.getM_everest());
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery2.input()).retrieve();
            assertThat(false).isEqualTo(true);
        } catch (ParseException e) {

        }  catch (Exception e1) {

        }
    }

    @Test
    public void testAggregationStringSubFilterWithInvalidSpecialCharacterOr() throws ResourceNotFoundException {
        System.out.println("\ntestAggregationStringSubFilterWithInvalidSpecialCharacterOr");
        EverestClient testAPI = new EverestClient(getContext());
        String request = "{Name:{{$or:[{$or:\"2\"},{$end:\"1\"}]}}}";
        QueryFilter parserQuery3 = new QueryFilter(request,testAPI.getM_everest());
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery3.input()).retrieve();
            assertThat(false).isEqualTo(true);
        } catch (ParseException e) {

        } catch (Exception e1) {

        }


    }

    @Test
    public void testAggregationAndFilterSimple() throws ResourceNotFoundException {
        System.out.println("\ntestAggregationAndFilterSimple");
        EverestClient testAPI = new EverestClient(getContext());
        assertThat(testAPI.read("/test").retrieve("Name")).isEqualTo("test");
        String request = "{{Name:\"room3\"},{Surface:15}}";
        QueryFilter parserQuery = new QueryFilter(request,testAPI.getM_everest());
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery.input()).retrieve();
            assertThat(resourceList).isNotEmpty();
            assertThat(resourceList).isNotNull();
            assertThat(resourceList).excludes(testAPI.read("/test/zone/room2").retrieve());
            assertThat(resourceList).excludes(testAPI.read("/test/zone/room1").retrieve());
            assertThat(resourceList).contains(testAPI.read("/test/zone/room3").retrieve());
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
    public void testAggregationAndFilterReturnNull() throws ResourceNotFoundException {
        System.out.println("\ntestAggregationAndFilterReturnNull");
        EverestClient testAPI = new EverestClient(getContext());

        String request = "{{Name:\"room3\"},{Surface:25}}";
        QueryFilter parserQuery1 = new QueryFilter(request,testAPI.getM_everest());
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery1.input()).retrieve();
            assertThat(resourceList).isNull();
            for (Resource resourceCurrent : resourceList){
                System.out.println(" Resource : " + resourceCurrent.getPath() + " SURFACE " + resourceCurrent.getMetadata() );
            }
            assertThat(true).isEqualTo(false);
        } catch (ParseException e) {
            assertThat(true).isEqualTo(false);
        }  catch (Exception e1) {

        }
    }

    @Test
    public void testAggregationAndFilterOrSubfilter() throws ResourceNotFoundException {
        System.out.println("\ntestAggregationAndFilterOrSubfilter");
        EverestClient testAPI = new EverestClient(getContext());
        String request = "{{Name:{{$or:[{$end:\"2\"},{$end:\"1\"}]}}},{Surface:{$lt:20}}}";

        QueryFilter parserQuery2 = new QueryFilter(request,testAPI.getM_everest());
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery2.input()).retrieve();
            assertThat(resourceList).isNotEmpty();
            assertThat(resourceList).isNotNull();
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
        System.out.println("\nTEST AGGREGATION OR FILTER");
        EverestClient testAPI = new EverestClient(getContext());
        assertThat(testAPI.read("/test").retrieve("Name")).isEqualTo("test");
        String request = "{$or:[{Name:\"room3\"},{Surface:4}]}";
        QueryFilter parserQuery = new QueryFilter(request,testAPI.getM_everest());
        System.out.println("JUST ROOM 3 + ROOM 2");
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery.input()).retrieve();
            assertThat(resourceList).isNotEmpty();
            assertThat(resourceList).isNotNull();
            for (Resource resourceCurrent : resourceList){
                System.out.println(" Resource : " + resourceCurrent.getPath() + " SURFACE " + resourceCurrent.getMetadata() );
            }
        } catch (ParseException e) {

            assertThat(true).isEqualTo(false);
        }  catch (Exception e1) {

            assertThat(true).isEqualTo(false);
        }

        request = "{{$or:[{Name:\"room3\"},{Surface:4}]},{Name:\"room3\"}}";
        QueryFilter parserQuery2 = new QueryFilter(request,testAPI.getM_everest());
        System.out.println("\n ROOM 3");
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery2.input()).retrieve();
            assertThat(resourceList).isNotEmpty();
            assertThat(resourceList).isNotNull();
            for (Resource resourceCurrent : resourceList){
                System.out.println(" Resource : " + resourceCurrent.getPath() + " SURFACE " + resourceCurrent.getMetadata() );
            }
        } catch (ParseException e) {

            assertThat(true).isEqualTo(false);
        }  catch (Exception e1) {

            assertThat(true).isEqualTo(false);
        }

        request = "{{$or:[{Name:\"room3\"},{$or:[{Surface:4},{Surface:25}]}]}}";
        QueryFilter parserQuery3 = new QueryFilter(request,testAPI.getM_everest());
        System.out.println("\n ROOM 3 + ROOM 1 + ROOM 2");
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery3.input()).retrieve();
            assertThat(resourceList).isNotEmpty();
            assertThat(resourceList).isNotNull();
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
        System.out.println("\nTEST EXIST");
        EverestClient testAPI = new EverestClient(getContext());
        assertThat(testAPI.read("/test").retrieve("Name")).isEqualTo("test");
        String request = "{Name:$exist}";
        QueryFilter parserQuery = new QueryFilter(request,testAPI.getM_everest());
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery.input()).retrieve();
            assertThat(resourceList).isNotEmpty();
            assertThat(resourceList).isNotNull();
            assertThat(resourceList).contains(testAPI.read("/test/zone/room2").retrieve());
            assertThat(resourceList).contains(testAPI.read("/test/zone/room1").retrieve());
            assertThat(resourceList).contains(testAPI.read("/test/zone/room3").retrieve());
            for (Resource resourceCurrent : resourceList){
                System.out.println(" Resource : " + resourceCurrent.getPath() + " SURFACE " + resourceCurrent.getMetadata() );
            }
        } catch (ParseException e) {

            assertThat(true).isEqualTo(false);
        }  catch (Exception e1) {

            assertThat(true).isEqualTo(false);
        }

        request = "{{$or:[{Name:$exist},{Surface:4}]}}";
        QueryFilter parserQuery1 = new QueryFilter(request,testAPI.getM_everest());
        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery1.input()).retrieve();
            assertThat(resourceList).isNotEmpty();
            assertThat(resourceList).isNotNull();
            assertThat(resourceList).contains(testAPI.read("/test/zone/room2").retrieve());
            assertThat(resourceList).contains(testAPI.read("/test/zone/room3").retrieve());
            assertThat(resourceList).contains(testAPI.read("/test/zone/room1").retrieve());
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
    public void testRelation() throws ResourceNotFoundException {
        System.out.println("\ntestRelation");
        EverestClient testAPI = new EverestClient(getContext());
        String request = "{$relation:{$resource:{SerialNumber:\"1\"}}}";
        QueryFilter parserQuery1 = new QueryFilter(request,testAPI.getM_everest());
        try {
            testAPI.read("/test/devices").children().update().with("zone","room1").doIt();
        } catch (IllegalActionOnResourceException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        try {
            List<Resource> resourceList = testAPI.read("/test/zone").children().filter(parserQuery1.input()).retrieve();
            assertThat(resourceList).isNotEmpty();
            assertThat(resourceList).isNotNull();
            for (Resource resourceCurrent : resourceList){
                System.out.println(" Resource : " + resourceCurrent.getPath()  );
            }
        } catch (ParseException e) {
            e.printStackTrace();
            assertThat(false).isEqualTo(true);
        }  catch (Exception e1) {
            e1.printStackTrace();
            assertThat(false).isEqualTo(true);
        }
    }
}
