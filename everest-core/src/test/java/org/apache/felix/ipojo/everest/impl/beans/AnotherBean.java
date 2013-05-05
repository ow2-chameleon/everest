package org.apache.felix.ipojo.everest.impl.beans;

/**
 * Another bean
 */
public class AnotherBean {

    private String name;

    private long id;

    private MyBean bean;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public MyBean getBean() {
        return bean;
    }

    public void setBean(MyBean bean) {
        this.bean = bean;
    }
}
