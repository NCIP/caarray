/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray2
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray2 Software License (the License) is between NCI and You. You (or
 * Your) shall mean a person or an entity, and all other entities that control,
 * are controlled by, or are under common control with the entity. Control for
 * purposes of this definition means (i) the direct or indirect power to cause
 * the direction or management of such entity, whether by contract or otherwise,
 * or (ii) ownership of fifty percent (50%) or more of the outstanding shares,
 * or (iii) beneficial ownership of such entity.
 *
 * This License is granted provided that You agree to the conditions described
 * below. NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up,
 * no-charge, irrevocable, transferable and royalty-free right and license in
 * its rights in the caArray2 Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray2 Software; (ii) distribute and
 * have distributed to and by third parties the caArray2 Software and any
 * modifications and derivative works thereof; and (iii) sublicense the
 * foregoing rights set out in (i) and (ii) to third parties, including the
 * right to license such rights to further third parties. For sake of clarity,
 * and not by way of limitation, NCI shall have no right of accounting or right
 * of payment from You or Your sub-licensees for the rights granted under this
 * License. This License is granted at no charge to You.
 *
 * Your redistributions of the source code for the Software must retain the
 * above copyright notice, this list of conditions and the disclaimer and
 * limitation of liability of Article 6, below. Your redistributions in object
 * code form must reproduce the above copyright notice, this list of conditions
 * and the disclaimer of Article 6 in the documentation and/or other materials
 * provided with the distribution, if any.
 *
 * Your end-user documentation included with the redistribution, if any, must
 * include the following acknowledgment: This product includes software
 * developed by 5AM and the National Cancer Institute. If You do not include
 * such end-user documentation, You shall include this acknowledgment in the
 * Software itself, wherever such third-party acknowledgments normally appear.
 *
 * You may not use the names "The National Cancer Institute", "NCI", or "5AM"
 * to endorse or promote products derived from this Software. This License does
 * not authorize You to use any trademarks, service marks, trade names, logos or
 * product names of either NCI or 5AM, except as required to comply with the
 * terms of this License.
 *
 * For sake of clarity, and not by way of limitation, You may incorporate this
 * Software into Your proprietary programs and into any third party proprietary
 * programs. However, if You incorporate the Software into third party
 * proprietary programs, You agree that You are solely responsible for obtaining
 * any permission from such third parties required to incorporate the Software
 * into such third party proprietary programs and for informing Your
 * sub-licensees, including without limitation Your end-users, of their
 * obligation to secure any required permissions from such third parties before
 * incorporating the Software into such third party proprietary software
 * programs. In the event that You fail to obtain such permissions, You agree
 * to indemnify NCI for any claims against NCI by such third parties, except to
 * the extent prohibited by law, resulting from Your failure to obtain such
 * permissions.
 *
 * For sake of clarity, and not by way of limitation, You may add Your own
 * copyright statement to Your modifications and to the derivative works, and
 * You may provide additional or different license terms and conditions in Your
 * sublicenses of modifications of the Software, or any derivative works of the
 * Software as a whole, provided Your use, reproduction, and distribution of the
 * Work otherwise complies with the conditions stated in this License.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, 5AM SOLUTIONS, INC. OR THEIR
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
