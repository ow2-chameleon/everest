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
