/**
 * 
 */
package caarray.legacy.client.test;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;

import java.util.List;

/**
 * @author vaughng
 * Jul 31, 2009
 */
public interface ApiFacade
{

    public void connect() throws Exception;
    
    public <T extends AbstractCaArrayObject> List<T> searchByExample(String api, T example);
}
