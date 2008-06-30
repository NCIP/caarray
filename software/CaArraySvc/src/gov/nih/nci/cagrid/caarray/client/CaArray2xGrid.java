/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The GridSvc2
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This GridSvc2 Software License (the License) is between NCI and You. You (or
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
 * its rights in the GridSvc2 Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the GridSvc2 Software; (ii) distribute and
 * have distributed to and by third parties the GridSvc2 Software and any
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
package gov.nih.nci.cagrid.caarray.client;

import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.client.DataServiceClient;

import java.io.File;
import java.io.FileWriter;

import javax.xml.namespace.QName;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CaArray2xGrid extends TestCase {

    private static Log LOG = LogFactory.getLog(CaArray2xGrid.class);

    public CaArray2xGrid() {
        super();
    }

    public CaArray2xGrid(String string) {
        super(string);
    }

    @Override
    public void setUp() {

        /*QA URL*/
//        System.setProperty("test.serviceUrl", "http://cbvapp-q1001.nci.nih.gov:59580/wsrf/services/cagrid/CaArraySvc");
        /*DEV URL*/
//        System.setProperty("test.serviceUrl", "http://cbvapp-d1002.nci.nih.gov:59580/wsrf/services/cagrid/CaArraySvc");
        /*DEV URL*/
//        System.setProperty("test.serviceUrl", "http://cbvapp-d1002.nci.nih.gov:59580/wsrf/services/cagrid/CaArraySvc");
        /*Local URL*/
        System.setProperty("test.serviceUrl", "http://localhost:8080/wsrf/services/cagrid/CaArraySvc");
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new CaArray2xGrid("testFindAllFiles"));
        suite.addTest(new CaArray2xGrid("testFindAllFactors"));
        suite.addTest(new CaArray2xGrid("testFindAllOrganizations"));
        suite.addTest(new CaArray2xGrid("testFindAllAddresses"));
        suite.addTest(new CaArray2xGrid("testFindAllCategorys"));
        suite.addTest(new CaArray2xGrid("testFindAllExperiments"));
        suite.addTest(new CaArray2xGrid("testFindAllProjects"));
        suite.addTest(new CaArray2xGrid("testFindAllAbstractBioMaterials"));
        suite.addTest(new CaArray2xGrid("testFindAllHybridizations"));
        suite.addTest(new CaArray2xGrid("testFindAllRawArrayData"));
        suite.addTest(new CaArray2xGrid("testFindAllArrayDesigns"));
        return suite;
    }

    public void testFindAllOrganizations() throws Exception {
        findAllHelper("gov.nih.nci.caarray.domain.contact.Organization");
    }

    public void testFindAllFiles() throws Exception {
        findAllHelper("gov.nih.nci.caarray.domain.file.CaArrayFile");
    }

    public void testFindAllAddresses() throws Exception {
        findAllHelper("gov.nih.nci.caarray.domain.contact.Address");
    }

    public void testFindAllFactors() throws Exception {
        findAllHelper("gov.nih.nci.caarray.domain.project.Factor");
    }

    public void testFindAllCategorys() throws Exception {
        findAllHelper("gov.nih.nci.caarray.domain.vocabulary.Category");
    }

    public void testFindAllProjects() throws Exception {
        findAllHelper("gov.nih.nci.caarray.domain.project.Project");
    }

    public void testFindAllExperiments() throws Exception {
        findAllHelper("gov.nih.nci.caarray.domain.project.Experiment");
    }

    public void testFindAllAbstractBioMaterials() throws Exception {
        findAllHelper("gov.nih.nci.caarray.domain.sample.AbstractBioMaterial");
    }

    public void testFindAllHybridizations() throws Exception {
        findAllHelper(Hybridization.class.getName());
    }

    public void testFindAllRawArrayData() throws Exception {
        findAllHelper(RawArrayData.class.getName());
    }

    public void testFindAllArrayDesigns() throws Exception {
        findAllHelper(ArrayDesign.class.getName());
    }

    private void findAllHelper(String target) throws Exception {
        CQLQuery query = new CQLQuery();
        query.setTarget(new gov.nih.nci.cagrid.cqlquery.Object());
        query.getTarget().setName(target);
        CQLQueryResults results = executeCQLQuery(query);
        printResults(results, "findAll." + target + ".xml");
    }

    public void testDomainModel() throws Exception {

    }

    public static CQLQueryResults executeCQLQuery(CQLQuery query) {
        try {
            DataServiceClient client = new DataServiceClient(System.getProperty("test.serviceUrl"));

            CQLQueryResults cqlQueryResult = client.query(query);
            return cqlQueryResult;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected synchronized long printResults(CQLQueryResults results, String outFileName) {
        try {
            // StringWriter w = new StringWriter();
            String fileName = "test/logs/" + outFileName;
            new File(fileName).delete();
            FileWriter w = new FileWriter(fileName);
            Utils.serializeObject(results, new QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLResultSet",
                    "CQLResultSet"), w);
            w.flush();
            w.close();
            long fileSize = new File(fileName).length();

            LOG.debug("... done printing results to : " + outFileName + " size=" + fileSize + " bytes");
            return fileSize;
        } catch (Exception ex) {
            throw new RuntimeException("Error printing results: " + ex.getMessage(), ex);
        }
    }
}
