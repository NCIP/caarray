/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-war
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-war Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-war Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-war Software; (ii) distribute and
 * have distributed to and by third parties the caarray-war Software and any
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
package gov.nih.nci.caarray.web.upgrade;

import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.util.HibernateUtil;

import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.w3c.dom.Element;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * Migrator to find biomaterials and hybridizations with the same name and merge them together.
 *
 * @author Steve Lustbader
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.CyclomaticComplexity" })
public class MergeDuplicateBiomaterialsMigrator extends AbstractMigrator {
    private static final Logger LOG = Logger.getLogger(MergeDuplicateBiomaterialsMigrator.class);
    private Element element;

    /**
     * {@inheritDoc}
     */
    public void migrate() throws MigrationStepFailedException {
        mergeBioMaterial(Source.class, "experimentsource", "source_id", "SO");
        mergeBioMaterial(Sample.class, "experimentsample", "sample_id", "SA");
        mergeBioMaterial(Extract.class, "experimentextract", "extract_id", "EX");
        mergeBioMaterial(LabeledExtract.class, "experimentlabeledextract", "labeled_extract_id", "LA");

        mergeHybs();
    }

    private <T extends AbstractBioMaterial> void mergeBioMaterial(Class<T> bioMaterialClass, String joinTable,
            String fkName, String discriminator) {
        List<T> bmDupList = findDuplicateBioMaterials(bioMaterialClass, joinTable, fkName, discriminator);
        LOG.debug(bmDupList.size() + " matching " + bioMaterialClass);

        T currentBm = null;
        for (T bm : bmDupList) {
            if (currentBm == null || !bm.getName().equals(currentBm.getName())
                    || !bm.getExperiment().equals(currentBm.getExperiment())) {
                if (currentBm != null) {
                    save(currentBm);
                }
                currentBm = bm;
                continue;
            }

            currentBm.merge(bm);
            remove(bm);
        }
        if (currentBm != null) {
            save(currentBm);
        }
    }

    private void mergeHybs() {
        boolean renameHybs = BooleanUtils.toBoolean(element.getAttribute("renameHybridizations"));
        List<Hybridization> hybs = findDuplicateHybs();
        LOG.debug(hybs.size() + " matching hybs");
        Hybridization currentHyb = null;
        int suffix = 1;
        for (Hybridization hyb : hybs) {
            if (currentHyb == null || !hyb.getName().equals(currentHyb.getName())
                    || !hyb.getExperiment().equals(currentHyb.getExperiment())) {
                if (currentHyb != null) {
                    save(currentHyb);
                }
                currentHyb = hyb;
                suffix = 1;
                continue;
            }

            if (renameHybs) {
                Experiment e = hyb.getExperiment();
                while (e.getHybridizationByName(hyb.getName() + "-" + suffix) != null) {
                    suffix++;
                }
                hyb.setName(hyb.getName() + "-" + suffix);
                save(hyb);
            } else {
                currentHyb.merge(hyb);
                remove(hyb);
            }
        }
        if (currentHyb != null && !renameHybs) {
            save(currentHyb);
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends AbstractBioMaterial> List<T> findDuplicateBioMaterials(Class<T> bioMaterialClass,
            String joinTable, String fkName, String discriminator) {
        String queryString = "select * from biomaterial b join " + joinTable + " jt" + " on b.id = jt." + fkName
                + "  and b.discriminator = :discriminator where (b.name, jt.experiment_id)"
                + " in (select temp_table.name, temp_table.experiment_id "
                + "from (select b2.name, jt2.experiment_id from biomaterial b2 join " + joinTable + " jt2"
                + " on b2.id = jt2." + fkName
                + " group by jt2.experiment_id, b2.name having count(*) > 1) as temp_table)"
                + " order by jt.experiment_id, b.name, b.id desc";
        Query query = HibernateUtil.getCurrentSession().createSQLQuery(queryString).addEntity(bioMaterialClass);
        query.setString("discriminator", discriminator);

        return (List<T>) query.list();
    }

    @SuppressWarnings("unchecked")
    private List<Hybridization> findDuplicateHybs() {
        String queryString = "select * from hybridization h where (h.name, h.experiment) in"
                + " (select temp_table.name, temp_table.experiment"
                + " from (select h2.name, h2.experiment from hybridization h2  group by h2.experiment,"
                + " h2.name having count(*) > 1) as temp_table) order by h.experiment, h.name, h.id desc";
        Query query = HibernateUtil.getCurrentSession().createSQLQuery(queryString).addEntity(Hybridization.class);

        return (List<Hybridization>) query.list();
    }

    private void save(PersistentObject persistentObject) {
        getDaoFactory().getSampleDao().save(persistentObject);
    }

    private void remove(PersistentObject persistentObject) {
        getDaoFactory().getSampleDao().remove(persistentObject);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setElement(Element element) {
        this.element = element;
    }

}
