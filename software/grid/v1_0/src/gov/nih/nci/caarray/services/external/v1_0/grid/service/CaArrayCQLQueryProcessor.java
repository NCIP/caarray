package gov.nih.nci.caarray.services.external.v1_0.grid.service;

import gov.nih.nci.caarray.services.ServerConnectionException;
import gov.nih.nci.caarray.services.external.v1_0.CaArrayServer;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Object;
import gov.nih.nci.cagrid.cqlquery.QueryModifier;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.cql.LazyCQLQueryProcessor;
import gov.nih.nci.cagrid.data.mapping.Mappings;
import gov.nih.nci.cagrid.data.service.ServiceConfigUtil;
import gov.nih.nci.cagrid.data.utilities.CQLResultsCreationUtil;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The actual CaArray CQL processing implementation.  Handles remote EJB integration and
 * converting the remote EJB API to the api expected by grid clients.
 */
public class CaArrayCQLQueryProcessor extends LazyCQLQueryProcessor {
    protected static Log LOG = LogFactory.getLog(CaArrayCQLQueryProcessor.class.getName());
    private final CaArrayServer caArrayServer;

    public CaArrayCQLQueryProcessor() {
        try {
            String jndiUrl = CaArraySvc_v1_0Impl.getJndiUrl();
            if (jndiUrl == null) {
                throw new RuntimeException("Could not connect to server: invalid JNDI configuration");
            }
            caArrayServer = new CaArrayServer(jndiUrl);
            caArrayServer.connect();
        } catch (ServerConnectionException e) {
            throw new RuntimeException("Could not connect to server", e);
        }        
    }
    
    @Override
    public CQLQueryResults processQuery(final CQLQuery cqlQuery) throws MalformedQueryException, QueryProcessingException {
        LOG.debug("CaArrayCQLQueryProcessor::processQuery ...");
        
        // first, validate 
        validateQuery(cqlQuery);
        CQLQueryResults results = null;
        try {
            List<?> coreResultsList = queryCaArrayService(cqlQuery);
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
                LOG.debug("invoking CQLResultsCreationUtil.createCountResults");
                results = CQLResultsCreationUtil.createCountResults(((Number) coreResultsList.get(0)).longValue(),
                                                                    targetName);
            } else {

                // attributes distinct or otherwise
                String[] names = null;
                boolean distinct = mod.getDistinctAttribute() != null;
                if (distinct) {
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
    
    private void validateQuery(CQLQuery cqlQuery) throws MalformedQueryException {
        validateNoAssociations(cqlQuery.getTarget());
    }
    
    private void validateNoAssociations(Object target) throws MalformedQueryException {
        if (target.getAssociation() != null || target.getGroup() != null
                && !ArrayUtils.isEmpty(target.getGroup().getAssociation())) {
            throw new MalformedQueryException("Associations are not supported");
        }
    }

    @Override
    public Iterator processQueryLazy(CQLQuery cqlQuery) throws MalformedQueryException, QueryProcessingException {
        return queryCaArrayService(cqlQuery).iterator();
    }

    /**
     * Call out to the remote EJB that actually processes the query.
     * @param cqlQuery query to run
     * @return list of domain objects that match the query criteria
     */
    protected List<?> queryCaArrayService(final CQLQuery cqlQuery) {
        LOG.debug("querying ....");
        return caArrayServer.getSearchService().search(cqlQuery, null);
    }

    /**
     * Get xml mappings.
     * @return xml mappings
     * @throws Exception on error
     */
    protected Mappings getClassToQnameMappings() throws Exception {
        // get the mapping file name
        String filename = ServiceConfigUtil.getClassToQnameMappingsFile();
        Mappings mappings = (Mappings) Utils.deserializeDocument(filename, Mappings.class);
        return mappings;
    }
}
