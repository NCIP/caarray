//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
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
