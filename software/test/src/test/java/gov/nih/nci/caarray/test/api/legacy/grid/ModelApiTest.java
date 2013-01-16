//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.test.api.legacy.grid;

import gov.nih.nci.caarray.domain.array.Accession;
import gov.nih.nci.caarray.domain.array.ExpressionProbeAnnotation;
import gov.nih.nci.caarray.domain.array.Feature;
import gov.nih.nci.caarray.domain.array.Gene;
import gov.nih.nci.cagrid.caarray.client.CaArraySvcClient;
import gov.nih.nci.cagrid.cqlquery.Association;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Group;
import gov.nih.nci.cagrid.cqlquery.LogicalOperator;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.cqlquery.QueryModifier;
import gov.nih.nci.cagrid.cqlresultset.CQLObjectResult;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author gax
 */
public class ModelApiTest extends AbstractLegacyGridApiTest {

    @Test
    public void testAbstractDesignElement_Feature() throws RemoteException {
        CQLQuery cqlQuery = new CQLQuery();
        gov.nih.nci.cagrid.cqlquery.Object target = new gov.nih.nci.cagrid.cqlquery.Object();
        target.setName("gov.nih.nci.caarray.domain.array.Feature");
        Association assoc = new Association("coordinateUnits");
        assoc.setName("gov.nih.nci.caarray.domain.vocabulary.Term");
        Attribute att = new Attribute("id", Predicate.EQUAL_TO, "321");
        assoc.setAttribute(att);
        Association[] assocs = { assoc };
        Attribute[] atts = {
            new Attribute("featureNumber", Predicate.LESS_THAN, "200"),
            new Attribute("x_Coordinate", Predicate.LESS_THAN, "3"),
            new Attribute("y_Coordinate", Predicate.GREATER_THAN, "0.03")
        };
        Group g = new Group(assocs, atts, null, LogicalOperator.AND);
        target.setGroup(g);
        cqlQuery.setTarget(target);

        QueryModifier qm = new QueryModifier();
        qm.setCountOnly(false);
        cqlQuery.setQueryModifier(qm);

        CQLQueryResults cqlResults = gridClient.query(cqlQuery);
//        assertEquals("this will fail due to http://gforge.nci.nih.gov/tracker/index.php?func=detail&aid=28685", 48, cqlResults.getObjectResult().length);
        Iterator iter = new CQLQueryResultsIterator(cqlResults, CaArraySvcClient.class.getResourceAsStream("client-config.wsdd"));
        while (iter.hasNext()) {
            Feature f = (Feature) (iter.next());
            assertEquals(321L, f.getCoordinateUnits().getId().longValue());
//            assertTrue(f.getFeatureNumber().intValue() < 200);
//            assertTrue(f.getX_Coordinate().doubleValue() < 3.0);
//            assertTrue(f.getY_Coordinate().doubleValue() > 0.03);
        }
    }

    @Test
    public void testExpressionProbeAnnotation() throws RemoteException {
        CQLQuery cqlQuery = new CQLQuery();
        gov.nih.nci.cagrid.cqlquery.Object target = new gov.nih.nci.cagrid.cqlquery.Object();
        target.setName("gov.nih.nci.caarray.domain.array.ExpressionProbeAnnotation");
        cqlQuery.setTarget(target);
        Attribute[] atts = {
//            new Attribute("chromosomeName", Predicate.EQUAL_TO, "foo"),
//            new Attribute("chromosomeStartPosition", Predicate.LESS_THAN, "1"),
//            new Attribute("chromosomeEndPosition", Predicate.GREATER_THAN, "2")
        };
//        Group g = new Group(null, atts, null, LogicalOperator.AND);
//        target.setGroup(g);
        CQLQueryResults cqlResults = gridClient.query(cqlQuery);
//        assertEquals(1071, cqlResults.getObjectResult().length);
        Iterator iter = new CQLQueryResultsIterator(cqlResults, CaArraySvcClient.class.getResourceAsStream("client-config.wsdd"));
        while (iter.hasNext()) {
            ExpressionProbeAnnotation f = (ExpressionProbeAnnotation) (iter.next());
//            assertEquals("foo", f.getChromosomeName());
//            assertTrue(f.getChromosomeStartPosition().longValue() < 1L);
//            assertTrue(f.getChromosomeEndPosition().longValue() > 200L);
        }
    }

    @Test
    public void testGene() throws RemoteException {
        CQLQuery cqlQuery = new CQLQuery();
        gov.nih.nci.cagrid.cqlquery.Object target = new gov.nih.nci.cagrid.cqlquery.Object();
        target.setName("gov.nih.nci.caarray.domain.array.Gene");
        cqlQuery.setTarget(target);
        target.setAttribute(new Attribute("id", Predicate.EQUAL_TO, "1"));
        CQLQueryResults cqlResults = gridClient.query(cqlQuery);
        assertEquals(1, cqlResults.getObjectResult().length);
        Iterator iter = new CQLQueryResultsIterator(cqlResults, CaArraySvcClient.class.getResourceAsStream("client-config.wsdd"));
        while (iter.hasNext()) {
            Gene f = (Gene) (iter.next());
            List<String> l = f.getAccessionNumbers("RefSeq");
            assertTrue(l.contains("XM_872275"));
        }
    }
}
