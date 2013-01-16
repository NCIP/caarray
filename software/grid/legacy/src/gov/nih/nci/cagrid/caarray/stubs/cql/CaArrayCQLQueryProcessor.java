//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.cagrid.caarray.stubs.cql;

import gov.nih.nci.caarray.services.search.CaArraySearchService;
import gov.nih.nci.cagrid.caarray.service.CaArraySvcImpl;
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

import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The actual CaArray CQL processing implementation.  Handles remote EJB integration and
 * converting the remote EJB API to the api expected by grid clients.
 */
public class CaArrayCQLQueryProcessor extends CQLQueryProcessor {
    protected static Log LOG = LogFactory.getLog(CaArrayCQLQueryProcessor.class.getName());
    private static CaArraySearchService searchService;

    private static synchronized CaArraySearchService getSearchService() {
        try {
            final Properties jndiProp = new Properties();
            jndiProp.load(CaArraySvcImpl.class.getResourceAsStream("/gov/nih/nci/cagrid/caarray/jndi.properties"));

            if (jndiProp.getProperty("java.naming.factory.initial") == null
                    || jndiProp.getProperty("java.naming.factory.url.pkgs") == null
                    || jndiProp.getProperty("java.naming.provider.url") == null) {
                throw new IllegalArgumentException("Unable to find all required properties in jndi.properties file.");
            }

            final Context context = new InitialContext(jndiProp);
            searchService = (CaArraySearchService) context.lookup(CaArraySearchService.JNDI_NAME);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        return searchService;
    }

    @Override
    public CQLQueryResults processQuery(final CQLQuery cqlQuery) throws MalformedQueryException, QueryProcessingException {
        LOG.debug("CaArrayCQLQueryProcessor::processQuery ...");
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

    /**
     * Call out to the remote EJB that actually processes the query.
     * @param cqlQuery query to run
     * @return list of domain objects that match the query criteria
     */
    protected List<?> queryCaArrayService(final CQLQuery cqlQuery) {
        LOG.debug("querying ....");
        return getSearchService().search(cqlQuery);
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
