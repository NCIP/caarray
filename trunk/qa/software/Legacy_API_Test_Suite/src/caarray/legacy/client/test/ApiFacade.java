//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package caarray.legacy.client.test;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.data.DataRetrievalRequest;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;

import java.util.List;

/**
 * @author vaughng
 * Jul 31, 2009
 */
public interface ApiFacade
{

    public void connect() throws Exception;
    
    public <T extends AbstractCaArrayObject> List<T> searchByExample(String api, T example, boolean login) throws Exception;
    
    public Object query(String api, CQLQuery cqlQuery) throws Exception;
    
    public DataSet getDataSet(String api, DataRetrievalRequest request) throws Exception;
    
    public byte[] readFile(String api, CaArrayFile file) throws Exception;
    
    public ArrayDesignDetails getArrayDesignDetails(String api, ArrayDesign arrayDesign) throws Exception;
}
