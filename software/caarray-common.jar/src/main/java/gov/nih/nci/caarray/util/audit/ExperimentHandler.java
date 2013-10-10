//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.util.audit;

import gov.nih.nci.caarray.domain.project.AbstractExperimentDesignNode;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentContact;
import gov.nih.nci.caarray.domain.project.Factor;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.publication.Publication;
import gov.nih.nci.caarray.util.CaArrayAuditLogProcessor;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

/**
 * @author wcheng
 *
 */
public class ExperimentHandler extends AbstractAuditEntityHandler<Experiment> {

    private static final Map<String, String> EXP_PROPERTIES =
            ImmutableMap.<String, String>builder()
            .put("title", "Title")
            .put("description", "Description")
            .put("assayTypes", "Assay Types")
            .put("manufacturer", "Provider")
            .put("arrayDesigns", "Array Designs")
            .put("organism", "Organism")
            .put("experimentDesignTypes", "Experiment Design Types")
            .put("designDescription", "Experiment Design Description")
            .put("qualityControlTypes", "Quality Control Types")
            .put("qualityControlDescription", "Quality Control Description")
            .put("replicateTypes", "Replicate Types")
            .put("replicateDescription", "Replicate Description")
            .build();

    private static final Map<String, String> EXP_COLLECTIONS = 
            ImmutableMap.<String, String>builder()
            .put("experimentContacts", "Contact")
            .put("publications", "Publication")
            .put("factors", "Factor")
            .put("sources", "Source")
            .put("samples", "Sample")
            .put("extracts", "Extract")
            .put("labeledExtracts", "Labeled Extract")
            .put("hybridizations", "Hybridization")
            .build();

    /**
     * @param processor audit log processor
     */
    public ExperimentHandler(CaArrayAuditLogProcessor processor) {
        super(processor);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.ExcessiveParameterList")
    protected boolean logAddOrUpdate(AuditLogRecord record, Experiment experiment, String property, 
            String columnName, Object oldVal, Object newVal) {
        boolean addedEntry = false;
        Project project = experiment.getProject();
        if (project != null) {
            if (EXP_COLLECTIONS.keySet().contains(property)) {
                getProcessor().addExperimentDetail(record, columnName, project);
                Set<?> addedEntries = Sets.difference(makeSet(newVal), makeSet(oldVal));
                for (Object node : addedEntries) {
                    String msg = String.format(" - %s %s added", EXP_COLLECTIONS.get(property), getObjectName(node));
                    getProcessor().addDetail(record, columnName, msg, null, node);
                }
                Set<?> deletedEntries = Sets.difference(makeSet(oldVal), makeSet(newVal));
                for (Object node : deletedEntries) {
                    String msg = String.format(" - %s %s deleted", EXP_COLLECTIONS.get(property), getObjectName(node));
                    getProcessor().addDetail(record, columnName, msg, node, null);
                }
                getProcessor().addProjectSecurity(record, project, READ_PRIV_ID);
                addedEntry = true;
            } else if (EXP_PROPERTIES.keySet().contains(property)) {
                getProcessor().addExperimentDetail(record, columnName, project);
                String msg = String.format(" - %s updated", EXP_PROPERTIES.get(property));
                getProcessor().addDetail(record, columnName, msg, oldVal, newVal);
                getProcessor().addProjectSecurity(record, project, READ_PRIV_ID);
                addedEntry = true;
            }
        }
        return addedEntry;
    }

    @SuppressWarnings({"unchecked", "rawtypes" })
    private Set<?> makeSet(Object o) {
        if (o instanceof Collection) {
            Set<?> result = Sets.newHashSet();
            result.addAll((Collection) o);
            return result;
        } else {
            return Sets.newHashSet(o);
        }
    }
    
    private String getObjectName(Object o) {
        if (o instanceof AbstractExperimentDesignNode) {
            return ((AbstractExperimentDesignNode) o).getName();
        } else if (o instanceof Factor) {
            return ((Factor) o).getName();
        } else if (o instanceof ExperimentContact) {
            return ((ExperimentContact) o).getContact().getName();
        } else if (o instanceof Publication) {
            return ((Publication) o).getTitle();
        }
        return "";
    }

}
