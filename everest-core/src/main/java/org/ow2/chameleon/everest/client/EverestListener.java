package org.ow2.chameleon.everest.client;

import org.ow2.chameleon.everest.services.Resource;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: colin
 * Date: 30/08/13
 * Time: 16:14
 * To change this template use File | Settings | File Templates.
 */
public interface EverestListener {

    public void getNewResult(List<Resource> resource);

}
