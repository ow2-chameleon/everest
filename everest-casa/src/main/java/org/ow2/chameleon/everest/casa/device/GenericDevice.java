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

package org.ow2.chameleon.everest.casa.device;

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 09/07/13
 * Time: 16:11
 * To change this template use File | Settings | File Templates.
 */
public class GenericDevice {


    String DEVICE_SERIAL_NUMBER = "device.serialNumber";

    public String STATE_PROPERTY_NAME = "state";
    public String STATE_ACTIVATED = "activated";
    public String STATE_DEACTIVATED = "deactivated";
    public String STATE_UNKNOWN = "unknown";
    public String zone = "null";

    public String getSTATE_DEACTIVATED() {
        return STATE_DEACTIVATED;
    }

    public void setSTATE_DEACTIVATED(String STATE_DEACTIVATED) {
        this.STATE_DEACTIVATED = STATE_DEACTIVATED;
    }

    public String getSTATE_PROPERTY_NAME() {
        return STATE_PROPERTY_NAME;
    }

    public void setSTATE_PROPERTY_NAME(String STATE_PROPERTY_NAME) {
        this.STATE_PROPERTY_NAME = STATE_PROPERTY_NAME;
    }

    public String getSTATE_ACTIVATED() {
        return STATE_ACTIVATED;
    }

    public void setSTATE_ACTIVATED(String STATE_ACTIVATED) {
        this.STATE_ACTIVATED = STATE_ACTIVATED;
    }

    public String getSTATE_UNKNOWN() {
        return STATE_UNKNOWN;
    }

    public void setSTATE_UNKNOWN(String STATE_UNKNOWN) {
        this.STATE_UNKNOWN = STATE_UNKNOWN;
    }


    public GenericDevice(String DEVICE_SERIAL_NUMBER) {
        this.DEVICE_SERIAL_NUMBER = DEVICE_SERIAL_NUMBER;
    }


}
