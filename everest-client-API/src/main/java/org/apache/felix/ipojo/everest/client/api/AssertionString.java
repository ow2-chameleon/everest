package org.apache.felix.ipojo.everest.client.api;/*
 * User: Colin
 * Date: 22/07/13
 * Time: 13:40
 * 
 */

public class AssertionString {

    private String param;


    public AssertionString(String param) {
        this.param = param;
    }


    public synchronized boolean isEqualTo(String value) {
        return value.equalsIgnoreCase(param);
    }


}
