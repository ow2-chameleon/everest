import org.apache.felix.ipojo.everest.services.IllegalActionOnResourceException;
import org.apache.felix.ipojo.everest.services.Resource;
import org.apache.felix.ipojo.everest.services.ResourceMetadata;
import org.apache.felix.ipojo.everest.services.ResourceNotFoundException;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 16/07/13
 * Time: 17:36
 * To change this template use File | Settings | File Templates.
 */
public class TestEnvironment extends CommonTest {


    @Test
    public void testCommonMetadataPresent() throws ResourceNotFoundException, IllegalActionOnResourceException {
        Resource r = read("/system/environment");
        // Resource should not be observable
        assertThat(r.isObservable()).isFalse();
        ResourceMetadata m = r.getMetadata();
     /*   assertThat(m.get("TERM")).isNotEqualTo(null);
        assertThat(m.get("SHLVL")).isNotEqualTo(null);
        assertThat(m.get("LESSCLOSE")).isNotEqualTo(null);
        assertThat(m.get("XFILESEARCHPATH")).isNotEqualTo(null);
        assertThat(m.get("SUDO_UID")).isNotEqualTo(null);
        assertThat(m.get("COLORTERM")).isNotEqualTo(null);
        assertThat(m.get("MAIL")).isNotEqualTo(null);
        assertThat(m.get("XDG_SESSION_COOKIE")).isNotEqualTo(null);
        assertThat(m.get("PWD")).isNotEqualTo(null);
        assertThat(m.get("LOGNAME")).isNotEqualTo(null);
        assertThat(m.get("SUDO_USER")).isNotEqualTo(null);
        assertThat(m.get("_")).isNotEqualTo(null);
        assertThat(m.get("NLSPATH")).isNotEqualTo(null);
        assertThat(m.get("OLDPWD")).isNotEqualTo(null);
        assertThat(m.get("SHELL")).isNotEqualTo(null);
        assertThat(m.get("SUDO_GID")).isNotEqualTo(null);
        assertThat(m.get("PATH")).isNotEqualTo(null);
        assertThat(m.get("DISPLAY")).isNotEqualTo(null);
        assertThat(m.get("USER")).isNotEqualTo(null);
        assertThat(m.get("HOME")).isNotEqualTo(null);
        assertThat(m.get("LESSOPEN")).isNotEqualTo(null);
        assertThat(m.get("XAUTHORITY")).isNotEqualTo(null);
        assertThat(m.get("SUDO_COMMAND")).isNotEqualTo(null);
        assertThat(m.get("LS_COLORS")).isNotEqualTo(null);
        assertThat(m.get("USERNAME")).isNotEqualTo(null);
        assertThat(m.get("XDG_RUNTIME_DIR")).isNotEqualTo(null);
        assertThat(m.get("LANG")).isNotEqualTo(null);  */
    }
}
