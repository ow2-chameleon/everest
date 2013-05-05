package org.apache.felix.ipojo.everest.impl.beans;

import java.util.List;

/**
 * A simple bean
 */
public class MyBean {

    private String message;

    private int count;

    private List<String> names;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }
}
