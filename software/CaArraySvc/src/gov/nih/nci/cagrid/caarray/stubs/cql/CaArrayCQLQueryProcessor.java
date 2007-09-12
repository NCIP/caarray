package gov.nih.nci.cagrid.caarray.stubs.cql;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.QueryModifier;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.cql.CQLQueryProcessor;
import gov.nih.nci.cagrid.data.mapping.Mappings;
import gov.nih.nci.cagrid.data.service.ServiceConfigUtil;
import gov.nih.nci.cagrid.data.utilities.CQLResultsCreationUtil;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CaArrayCQLQueryProcessor extends CQLQueryProcessor {
    protected static Log LOG = LogFactory.getLog(CaArrayCQLQueryProcessor.class.getName());
    @Override
    public CQLQueryResults processQuery(CQLQuery cqlQuery) throws MalformedQueryException, QueryProcessingException {
        System.out.println("CaArrayCQLQueryProcessor::processQuery ...");
        CQLQueryResults results = null;
        try {
            List<Object> coreResultsList = queryCaArrayService(cqlQuery);
            String targetName = cqlQuery.getTarget().getName();

            Mappings mappings = null;
            try {
                mappings = getClassToQnameMappings();
            } catch (Exception ex) {
                throw new QueryProcessingException("Error getting class to qname mappings: " + ex.getMessage(), ex);
            }
            // decide on type of results
            QueryModifier mod = cqlQuery.getQueryModifier();
            boolean objectResults = mod == null
                    || (!mod.isCountOnly() && mod.getAttributeNames() == null && mod.getDistinctAttribute() == null);

            if (objectResults) {
                LOG.debug("invoking CQLResultsCreationUtil.createObjectResults");
                try {
                    results = CQLResultsCreationUtil.createObjectResults(coreResultsList, cqlQuery.getTarget()
                            .getName(), mappings);
                } catch (Exception ex) {
                    throw new QueryProcessingException("Error creating object results: " + ex.getMessage(), ex);
                }
            } else if (mod.isCountOnly()) {

                Long val = Long.valueOf(coreResultsList.get(0).toString());
                LOG.debug("invoking CQLResultsCreationUtil.createCountResults");
                results = CQLResultsCreationUtil.createCountResults(val.longValue(), targetName);
            } else {

                // attributes distinct or otherwise
                String[] names = null;
                if (mod.getDistinctAttribute() != null) {
                    names = new String[] { mod.getDistinctAttribute() };
                } else {
                    names = mod.getAttributeNames();
                }
                try {
                    LOG.debug("invoking CQLResultsCreationUtil.createAttributeResults");
                    results = CQLResultsCreationUtil.createAttributeResults(coreResultsList, targetName, names);
                } catch (Exception ex) {
                    throw new RuntimeException("Error creating attribute results: " + ex.getMessage(), ex);
                }
            }

            return results;
        } catch (Exception e) {
            throw new QueryProcessingException(e);
        }
    }

    private List<Object> queryCaArrayService(CQLQuery cqlQuery) {
        LOG.debug("querying ....");
        ArrayList<Object> list = new ArrayList<Object>();
        gov.nih.nci.caarray.domain.protocol.Protocol obj = new gov.nih.nci.caarray.domain.protocol.Protocol();
        obj.setName("test-protocol");
        obj.setDescription("my description");
        list.add(obj);
        return list;
    }

    private Mappings getClassToQnameMappings() throws Exception {
        // get the mapping file name
        String filename = ServiceConfigUtil.getClassToQnameMappingsFile();
        Mappings mappings = (Mappings) Utils.deserializeDocument(filename, Mappings.class);
        return mappings;
    }
}
