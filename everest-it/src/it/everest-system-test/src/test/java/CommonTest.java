/*
 * Copyright 2013 OW2 Chameleon
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.ow2.chameleon.everest.impl.DefaultRequest;
import org.ow2.chameleon.everest.services.*;
import org.junit.Test;
import org.ops4j.pax.exam.Option;
import org.ow2.chameleon.testing.helpers.BaseTest;

import javax.inject.Inject;

import static org.fest.assertions.Assertions.assertThat;
import static org.ops4j.pax.exam.CoreOptions.*;

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 16/07/13
 * Time: 17:37
 * To change this template use File | Settings | File Templates.
 */
public class CommonTest extends BaseTest {

    /**
     * The everest services.
     */
    @Inject
    EverestService everest;

    /**
     * Common test options.
     */
    @Override
    protected Option[] getCustomOptions() {

        return options(  // everest bundles
                mavenBundle("org.ow2.chameleon.everest", "everest-core").versionAsInProject(),
                mavenBundle("org.ow2.chameleon.everest", "everest-system").versionAsInProject(),
                // Fest assert JARs wrapped as bundles
                wrappedBundle(mavenBundle("org.easytesting", "fest-util").versionAsInProject()),
                wrappedBundle(mavenBundle("org.easytesting", "fest-assert").versionAsInProject()),
                mavenBundle("org.apache.felix", "org.apache.felix.eventadmin").versionAsInProject()
        );
    }


    @Override
    public boolean deployTestBundle() {
        return false;
    }

    @Test
    public void True() {
        assertThat("true").isEqualTo("true");
    }

    /**
     * Shortcut method to process a READ request on an everest resource.
     *
     * @param path the path of the resource to read
     * @return the read resource
     * @throws org.ow2.chameleon.everest.services.ResourceNotFoundException
     *          if the resource cannot be found
     * @throws org.ow2.chameleon.everest.services.IllegalActionOnResourceException
     *          if READ is not a valid operation on the targeted resource
     */
    public Resource read(String path) throws ResourceNotFoundException, IllegalActionOnResourceException {
        return everest.process(new DefaultRequest(Action.READ, Path.from(path), null));
    }
}
