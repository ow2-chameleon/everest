package org.apache.felix.ipojo.everest.casa.device;

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


    public GenericDevice(String DEVICE_SERIAL_NUMBER) {
        this.DEVICE_SERIAL_NUMBER = DEVICE_SERIAL_NUMBER;
    }

}
