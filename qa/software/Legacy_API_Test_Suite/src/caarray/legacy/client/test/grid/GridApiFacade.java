//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package caarray.legacy.client.test.grid;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.cagrid.transfer.context.client.TransferServiceContextClient;
import org.cagrid.transfer.context.client.helper.TransferClientHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.data.DataRetrievalRequest;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.cagrid.caarray.client.CaArraySvcClient;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import caarray.legacy.client.test.ApiFacade;
import caarray.legacy.client.test.TestProperties;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.cqlresultset.CQLObjectResult;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import java.util.Iterator;

/**
 * @author vaughng
 * Jul 31, 2009
 */
public class GridApiFacade implements ApiFacade
{

    private CaArraySvcClient gridClient = null;
    
    public GridApiFacade(){}
    
    public void connect() throws Exception
    {
        String gridUrl = TestProperties.getGridServiceUrl();
        System.out.println("Connecting to grid service: " + gridUrl);
        gridClient = new CaArraySvcClient(gridUrl);   
    }

    /* this is a very limited search that converts the example into cql.
     * @see caarray.legacy.client.test.ApiFacade#searchByExample(java.lang.String, gov.nih.nci.caarray.domain.AbstractCaArrayObject)
     */
    public <T extends AbstractCaArrayObject> List<T> searchByExample(
            String api, T example, boolean login) throws Exception
    {
        CQLQuery cqlQuery = new CQLQuery();
        gov.nih.nci.cagrid.cqlquery.Object target = new gov.nih.nci.cagrid.cqlquery.Object();
        Attribute attribute = new Attribute();
        attribute.setName("name");
        attribute.setPredicate(Predicate.EQUAL_TO);
        target.setAttribute(attribute);
        cqlQuery.setTarget(target);
        if (example instanceof CaArrayFile) {
            target.setName(CaArrayFile.class.getName());
            attribute.setValue(((CaArrayFile)example).getName());
        } else if (example instanceof ArrayDesign) {
            target.setName(ArrayDesign.class.getName());
            attribute.setValue(((ArrayDesign)example).getName());
        } else {
            throw new UnsupportedOperationException("not yet implemented");
        }
        ArrayList<T> list = new ArrayList<T>();
        CQLQueryResults r = gridClient.query(cqlQuery);
        Iterator iter = new CQLQueryResultsIterator(r, CaArraySvcClient.class.getResourceAsStream("client-config.wsdd"));
        while (iter.hasNext()) {
            @SuppressWarnings("unchecked")
            T o = (T) iter.next();
            list.add(o);
        }
        return list;
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.ApiFacade#query(java.lang.String, gov.nih.nci.cagrid.cqlquery.CQLQuery)
     */
    public Object query(String api, CQLQuery cqlQuery)
            throws Exception
    {
        return gridClient.query(cqlQuery);
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.ApiFacade#getDataSet(java.lang.String, gov.nih.nci.caarray.domain.data.DataRetrievalRequest)
     */
    public DataSet getDataSet(String api, DataRetrievalRequest request)
            throws Exception
    {
        return gridClient.getDataSet(request);
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.ApiFacade#getArrayDesignDetails(java.lang.String, gov.nih.nci.caarray.domain.array.ArrayDesign)
     */
    public ArrayDesignDetails getArrayDesignDetails(String api,
            ArrayDesign arrayDesign) throws Exception
    {
        return gridClient.getDesignDetails(arrayDesign);
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.ApiFacade#readFile(java.lang.String, gov.nih.nci.caarray.domain.file.CaArrayFile)
     */
    public byte[] readFile(String api, CaArrayFile file) throws Exception
    {
        TransferServiceContextReference serviceContextRef = gridClient.createFileTransfer(file);
        TransferServiceContextClient transferClient = new TransferServiceContextClient(serviceContextRef.getEndpointReference());
        InputStream stream = TransferClientHelper.getData(transferClient.getDataTransferDescriptor());
        return IOUtils.toByteArray(stream);
     }
}
