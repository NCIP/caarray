//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.upgrade;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentOntology;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.UnhandledException;

/**
 * @author dharley
 *
 */
public class FixOrganismsMigrator extends AbstractHibernateBasedCustomChange {
    
    private TermSource ncbiTermSource;
    private List<Organism> allOrganismsList;

    /**
     * {@inheritDoc}
     */
    public void doHibernateExecute(final SingleConnectionHibernateHelper hibernateHelper) {
        try {
            List<Organism> allOrganisms = getAllOrganisms(hibernateHelper);
            Map<String, List<Organism>> nameToOrganismsListMap = buildNamesToOrganismsListMap(allOrganisms);
            for (List<Organism> organismsWithSameNameList : nameToOrganismsListMap.values()) {
                if (organismsWithSameNameList.size() > 1) {
                    processOrganismsWithSameName(hibernateHelper, organismsWithSameNameList.get(0).getScientificName(),
                            organismsWithSameNameList);
                } else if (organismsWithSameNameList.size() == 1) {
                    Organism organism = organismsWithSameNameList.get(0);
                    if (null != organism.getTermSource()
                            && !ExperimentOntology.NCBI.getOntologyName().equals(organism.getTermSource().getName())) {
                        organism.setTermSource(getNcbiTermSource(hibernateHelper));
                    }
                }
            }
          } catch (Exception exception) {
              throw new UnhandledException("Cannot fix the Organisms in DB.", exception);
          }
    }
    
    private Map<String, List<Organism>> buildNamesToOrganismsListMap(final List<Organism>allOrganisms) {
        Map<String, List<Organism>> nameToOrganismsListMap = new HashMap<String, List<Organism>>();
        for (Organism organism : allOrganisms) {
            List<Organism> organismsWithThisName =
                nameToOrganismsListMap.get(organism.getScientificName().toUpperCase());
            if (null == organismsWithThisName) {
                organismsWithThisName = new ArrayList<Organism>();
                nameToOrganismsListMap.put(organism.getScientificName().toUpperCase(), organismsWithThisName);
            }
            organismsWithThisName.add(organism);
        }
        return nameToOrganismsListMap;
    }
    
    private TermSource getNcbiTermSource(final SingleConnectionHibernateHelper hibernateHelper) {
        if (null == ncbiTermSource) {
            ncbiTermSource = (TermSource) hibernateHelper.getCurrentSession().createQuery("FROM "
                    + TermSource.class.getName() + " as termSource WHERE termSource.name = '"
                    + ExperimentOntology.NCBI.getOntologyName() + "'").list().get(0); //there has to be at least one
        }
        return ncbiTermSource;
    }
    
    @SuppressWarnings({"PMD.UnusedNullCheckInEquals" })
    private Organism getNcbiSourcedOrganism(List<Organism> organismsList, final boolean removeFromList) {
        Organism ncbiOrganism = null;
        for (Organism organism : organismsList) {
            if (null != organism.getTermSource()
                    && ExperimentOntology.NCBI.getOntologyName().equals(organism.getTermSource().getName())) {
                ncbiOrganism = organism;
                if (removeFromList) {
                    organismsList.remove(organism);
                }
                break;
            }
        }
        return ncbiOrganism;
    }
    
    private void processOrganismsWithSameName(final SingleConnectionHibernateHelper hibernateHelper,
            final String nameOfOrganisms, final List<Organism> organismsWithSameNameList) {
        // the NCBI term sourced organism will be removed from the list, so the list can be deleted later
        Organism ncbiSourcedOrganismWithThisName = getNcbiSourcedOrganism(organismsWithSameNameList, true);
        if (null == ncbiSourcedOrganismWithThisName) {
            ncbiSourcedOrganismWithThisName = new Organism();
            ncbiSourcedOrganismWithThisName.setScientificName(nameOfOrganisms);
            ncbiSourcedOrganismWithThisName.setTermSource(ncbiTermSource);
            hibernateHelper.getCurrentSession().save(ncbiSourcedOrganismWithThisName);
        }
        for (Organism organism : organismsWithSameNameList) {
            fixAllExperiments(hibernateHelper, organism, ncbiSourcedOrganismWithThisName);
            fixAllArrayDesigns(hibernateHelper, organism, ncbiSourcedOrganismWithThisName);
            fixAllBiomaterials(hibernateHelper, organism, ncbiSourcedOrganismWithThisName);
            // the NCBI term sourced organism has already been removed from the list, so it is safe to delete
            hibernateHelper.getCurrentSession().delete(organism);
        }
    }
    
    private void fixAllExperiments(final SingleConnectionHibernateHelper hibernateHelper,
            final Organism organismToReplace, final Organism ncbiSourcedOrganism) {
        List<Experiment> allExperimentsWithBadOrganism = getAllExperimentsForOrganism(hibernateHelper,
                organismToReplace);
        for (Experiment experiment : allExperimentsWithBadOrganism) {
            experiment.setOrganism(ncbiSourcedOrganism);
            hibernateHelper.getCurrentSession().save(experiment);
        }
    }
    
    private void fixAllArrayDesigns(final SingleConnectionHibernateHelper hibernateHelper,
            final Organism organismToReplace, final Organism ncbiSourcedOrganism) {
        List<ArrayDesign> allArrayDesignsWithBadOrganism = getAllArrayDesignsForOrganism(hibernateHelper,
                organismToReplace);
        for (ArrayDesign arrayDesign : allArrayDesignsWithBadOrganism) {
            arrayDesign.setOrganism(ncbiSourcedOrganism);
            hibernateHelper.getCurrentSession().save(arrayDesign);
        }
    }
    
    private void fixAllBiomaterials(final SingleConnectionHibernateHelper hibernateHelper,
            final Organism organismToReplace, final Organism ncbiSourcedOrganism) {
        List<AbstractBioMaterial> allAbstractBioMaterialsWithBadOrganism = getAllAbstractBioMaterialsForOrganism(
                hibernateHelper, organismToReplace);
        for (AbstractBioMaterial biomaterial : allAbstractBioMaterialsWithBadOrganism) {
            biomaterial.setOrganism(ncbiSourcedOrganism);
            hibernateHelper.getCurrentSession().save(biomaterial);
        }
    }
    
    private List<Experiment> getAllExperimentsForOrganism(final SingleConnectionHibernateHelper hibernateHelper,
            final Organism targetOrganism) {
        return hibernateHelper.getCurrentSession().createQuery("SELECT experiment FROM "
                + Experiment.class.getName() + " experiment WHERE experiment.organism = :organism").
                setEntity("organism", targetOrganism).list();
    }
    
    private List<ArrayDesign> getAllArrayDesignsForOrganism(final SingleConnectionHibernateHelper hibernateHelper,
            final Organism targetOrganism) {
        return hibernateHelper.getCurrentSession().createQuery("SELECT arrayDesign FROM "
                + ArrayDesign.class.getName() + " arrayDesign WHERE arrayDesign.organism = :organism").
                setEntity("organism", targetOrganism).list();
    }
    
    private List<AbstractBioMaterial> getAllAbstractBioMaterialsForOrganism(
            final SingleConnectionHibernateHelper hibernateHelper, final Organism targetOrganism) {
        return hibernateHelper.getCurrentSession().createQuery("SELECT biomaterial FROM "
                + AbstractBioMaterial.class.getName() + " biomaterial WHERE biomaterial.organism = :organism").
                setEntity("organism", targetOrganism).list();
    }
    
    private List<Organism> getAllOrganisms(final SingleConnectionHibernateHelper hibernateHelper) {
        if (null == allOrganismsList) {
            allOrganismsList = hibernateHelper.getCurrentSession().createQuery("FROM "
                    + Organism.class.getName()).list();
        }        
        return allOrganismsList;
    }
    
}
