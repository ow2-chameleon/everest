import org.apache.felix.ipojo.everest.impl.DefaultRequest;
import org.apache.felix.ipojo.everest.services.*;
import org.junit.Test;
import org.ops4j.pax.exam.Option;

import java.util.HashMap;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 16/07/13
 * Time: 14:12
 * To change this template use File | Settings | File Templates.
 */
public class TestSystemProperties extends CommonTest {


    /**
     * Common test options.
     */
    @Override
    protected Option[] getCustomOptions() {
        System.setProperty("Test1", "42");
        return super.getCustomOptions();
    }

    @Test
    public void testAddProperties() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource r = read("/system/properties");
        // Resource should not be observable
        assertThat(r.isObservable()).isFalse();
        ResourceMetadata m = r.getMetadata();
        assertThat(m.get("Test1")).isEqualTo("42");
        assertThat(m.get("Test2")).isEqualTo(null);
    }

    @Test
    public void testCommonMetadataPresent() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource r = read("/system/properties");
        // Resource should not be observable
        assertThat(r.isObservable()).isFalse();
        ResourceMetadata m = r.getMetadata();
    /*    assertThat(m.get("java.version")).isNotEqualTo(null);
        assertThat(m.get("java.vendor")).isNotEqualTo(null);
        assertThat(m.get("java.vendor.url")).isNotEqualTo(null);
        assertThat(m.get("java.home")).isNotEqualTo(null);
        assertThat(m.get("java.vm.specification.version")).isNotEqualTo(null);
        assertThat(m.get("java.vm.specification.vendor")).isNotEqualTo(null);
        assertThat(m.get("java.vm.specification.name")).isNotEqualTo(null);
        assertThat(m.get("java.vm.version")).isNotEqualTo(null);
        assertThat(m.get("java.vm.vendor")).isNotEqualTo(null);
        assertThat(m.get("java.vm.name")).isNotEqualTo(null);
        assertThat(m.get("java.specification.version")).isNotEqualTo(null);
        assertThat(m.get("java.specification.vendor")).isNotEqualTo(null);
        assertThat(m.get("java.specification.name")).isNotEqualTo(null);
        assertThat(m.get("java.class.version")).isNotEqualTo(null);
        assertThat(m.get("java.class.path")).isNotEqualTo(null);
        assertThat(m.get("java.library.path")).isNotEqualTo(null);
        assertThat(m.get("java.io.tmpdir")).isNotEqualTo(null);
        assertThat(m.get("java.ext.dirs")).isNotEqualTo(null);
        assertThat(m.get("os.name")).isNotEqualTo(null);
        assertThat(m.get("os.arch")).isNotEqualTo(null);
        assertThat(m.get("os.version")).isNotEqualTo(null);
        assertThat(m.get("file.separator")).isNotEqualTo(null);
        assertThat(m.get("path.separator")).isNotEqualTo(null);
        assertThat(m.get("line.separator")).isNotEqualTo(null);
        assertThat(m.get("user.name")).isNotEqualTo(null);
        assertThat(m.get("user.home")).isNotEqualTo(null);  */
    }

    @Test
    public void testUpdateRelation() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("Test3", "Foo");
        params.put("Test4", 152);
        Resource r = everest.process(
                new DefaultRequest(Action.UPDATE, Path.from("/system/properties"), params));
        ResourceMetadata m = r.getMetadata();
        assertThat(m.get("Test3")).isEqualTo("Foo");
        assertThat(m.get("Test4")).isEqualTo("152");

    }


}
