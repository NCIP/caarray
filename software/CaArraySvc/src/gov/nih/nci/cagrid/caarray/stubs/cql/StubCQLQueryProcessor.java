package gov.nih.nci.cagrid.caarray.stubs.cql;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.cql.CQLQueryProcessor;

/**
 *  StubCQLQueryProcessor
 *  This CQL Query Processor is provided as a stub to begin implementing CQL against your
 *  backend data source.  If another CQL query processor implementation is used,
 *  this file may be safely ignored.
 *  If this class is deleted, the introduce service synchronization process
 *  will recreate it.
 *
 * @deprecated The stub CQL query processor is a placeholder to provide a starting point for
 * implementation of CQL against a backend data source.
 */
@Deprecated
public class StubCQLQueryProcessor extends CQLQueryProcessor {

    @Override
    public CQLQueryResults processQuery(final CQLQuery cqlQuery) throws MalformedQueryException, QueryProcessingException {
        // Delegate to actual implementation.  For some reason, I can't convince Introduce to use this
        // class directly
        return new CaArrayCQLQueryProcessor().processQuery(cqlQuery);
    }
}
