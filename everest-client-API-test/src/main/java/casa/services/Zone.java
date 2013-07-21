package casa.services;

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
