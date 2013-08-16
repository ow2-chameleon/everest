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

package org.ow2.chameleon.everest.casa.zone;

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 12/07/13
 * Time: 16:47
 * To change this template use File | Settings | File Templates.
 */
public class Zone {

    String name;


    String m_luminosity;

    public Zone(String name) {
        this.name = name;
        m_luminosity = "0";
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getM_luminosity() {
        return m_luminosity;
    }

    public void setM_luminosity(String m_luminosity) {
        this.m_luminosity = m_luminosity;
    }


}
