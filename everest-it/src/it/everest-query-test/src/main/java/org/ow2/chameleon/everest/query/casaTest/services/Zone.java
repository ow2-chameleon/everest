package org.ow2.chameleon.everest.query.casaTest.services;

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



    float m_Temperature;



    int   m_Surface ;

    public Zone(String name) {
        this.name = name;
        m_luminosity = "0";
        m_Temperature = (float) 10.65;
        m_Surface = 15;
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

    public float getM_Temperature() {
        return m_Temperature;
    }

    public int getM_Surface() {
        return m_Surface;
    }

    public void setM_Surface(int m_Surface) {
        this.m_Surface = m_Surface;
    }
}
