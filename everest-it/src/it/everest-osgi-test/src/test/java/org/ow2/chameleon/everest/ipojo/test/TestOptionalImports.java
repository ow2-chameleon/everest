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

package org.ow2.chameleon.everest.ipojo.test;

import org.junit.Test;
import org.osgi.framework.Bundle;
import org.ow2.chameleon.everest.services.Resource;
import org.ow2.chameleon.everest.services.ResourceNotFoundException;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: ozan
 * Date: 2/18/14
 * Time: 5:48 PM
 */
public class TestOptionalImports extends EverestOsgiTest {


    @Override
    public boolean deployConfigAdmin() {
        return false;
    }

    @Test
    public void testConfigAdmin() throws Exception {

        osgiHelper.getBundle("osgi.cmpn").uninstall();

        Resource resource = get("/osgi");
        assertThat(resource).isNotNull();
        Resource configs = null;
        try{
            configs = get("/osgi/configurations");
        } catch (ResourceNotFoundException e){

        }
        assertThat(configs).isNull();
    }

}
