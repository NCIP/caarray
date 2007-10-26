package gov.nih.nci.cagrid.caarray.stubs.cql;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.services.search.CaArraySearchService;
import gov.nih.nci.cagrid.caarray.service.CaArraySvcImpl;
import gov.nih.nci.cagrid.caarray.util.CQL2CQL;
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The actual CaArray CQL processing implementation.  Handles remote EJB integration and
 * converting the remote EJB API to the api expected by grid clients.
 */
public class CaArrayCQLQueryProcessor extends CQLQueryProcessor {
    protected static Log LOG = LogFactory.getLog(CaArrayCQLQueryProcessor.class.getName());
    private static final CaArraySearchService searchService;

    static {
        CaArraySearchService svc = null;
        try {
            final Properties jndiProp = new Properties();
            jndiProp.load(CaArraySvcImpl.class.getResourceAsStream("/gov/nih/nci/cagrid/caarray/jndi.properties"));

            if (jndiProp.getProperty("java.naming.factory.initial") == null
                    || jndiProp.getProperty("java.naming.factory.url.pkgs") == null
                    || jndiProp.getProperty("java.naming.provider.url") == null) {
                throw new IllegalArgumentException("Unable to find all required properties in jndi.properties file.");
            }

            final Context context = new InitialContext(jndiProp);
            svc = (CaArraySearchService) context.lookup(CaArraySearchService.JNDI_NAME);
        } catch (final Exception e) {
            LOG.error("Unable to init: " + e, e);
        }
        searchService = svc;
    }

    @Override
    public CQLQueryResults processQuery(final CQLQuery cqlQuery) throws MalformedQueryException, QueryProcessingException {
        LOG.debug("CaArrayCQLQueryProcessor::processQuery ...");
        CQLQueryResults results = null;
        try {
            List<? extends AbstractCaArrayObject> coreResultsList = queryCaArrayService(cqlQuery);
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
                results = CQLResultsCreationUtil.createCountResults(coreResultsList.size(), targetName);
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
                    List<Object[]> attributeList = convert(coreResultsList, names, distinct);
                    results = CQLResultsCreationUtil.createAttributeResults(attributeList, targetName, names);
                } catch (Exception ex) {
                    throw new RuntimeException("Error creating attribute results: " + ex.getMessage(), ex);
                }
            }

            return results;
        } catch (Exception e) {
            throw new QueryProcessingException(e);
        }
    }

    private List<Object[]> convert(List<? extends AbstractCaArrayObject> objs, String[] names, boolean distinct)
        throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
               InvocationTargetException {
        String[] upcaseNames = new String[names.length];
        for (int i = 0; i < names.length; ++i) {
            upcaseNames[i] = StringUtils.capitalize(names[i]);
        }

        Set<Object> seenObjs = new HashSet<Object>();

        List<Object[]> result = new ArrayList<Object[]>();
        for (AbstractCaArrayObject curObj : objs) {
            Object[] row = new Object[names.length];
            for (int i = 0; i < names.length; ++i) {
                Method m = curObj.getClass().getMethod("get" + upcaseNames[i]);
                row[i] = m.invoke(curObj);
            }
            if (distinct) {
                if (!seenObjs.contains(row[0])) {
                    result.add(row);
                }
                seenObjs.add(row[0]);
            } else {
                result.add(row);
            }
        }
        return result;
    }

    /**
     * Call out to the remote EJB that actually processes the query.
     * @param cqlQuery query to run
     * @return list of domain objects that match the query criteria
     */
    protected List<? extends AbstractCaArrayObject> queryCaArrayService(final CQLQuery cqlQuery) {
        LOG.debug("querying ....");
        return searchService.search(CQL2CQL.convert(cqlQuery));
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
