//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.util.audit;

import java.util.List;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.util.CaArrayAuditLogProcessor;

import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;
import com.fiveamsolutions.nci.commons.audit.AuditType;
import com.google.common.collect.ImmutableList;

/**
 * @author wcheng
 *
 */
public class ArrayDesignHandler extends AbstractAuditEntityHandler<ArrayDesign> {

    private static final List<String> AUDITABLE_PROPERTIES = ImmutableList.of("description", "assayTypes", "provider",
            "version", "technologyType", "organism", "geoAccession", "designFiles");
    
    /**
     * @param processor audit log processor
     */
    public ArrayDesignHandler(CaArrayAuditLogProcessor processor) {
        super(processor);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.ExcessiveParameterList")
    protected boolean logAddOrUpdate(AuditLogRecord record, ArrayDesign entity, String property, String columnName,
            Object oldVal, Object newVal) {
        boolean addedEntry = false;
        if (organismUnchanged(property, oldVal, newVal)) {
            return false;
        }
        if (AUDITABLE_PROPERTIES.contains(property)) {
            String addUpdate = record.getType() == AuditType.INSERT ? "added" : "updated";
            if (record.getDetails().size() == 0) {
                String msg = String.format("Array Design '%s' %s", entity.getName(), addUpdate);
                getProcessor().addDetail(record, "Array Design", msg);
            }
            String msg = String.format(" - %s %s", property, addUpdate);
            getProcessor().addDetail(record, columnName, msg, oldVal, newVal);
            addedEntry = true;
        } else if ("numberOfFeatures".equals(property)) {
            String msg = String.format("Array Design '%s' imported", entity.getName());
            getProcessor().addDetail(record, columnName, msg, oldVal, newVal);
            addedEntry = true;
        }
        return addedEntry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.ExcessiveParameterList")
    protected boolean logDelete(AuditLogRecord record, ArrayDesign entity, String property, String columnName,
            Object oldVal, Object newVal) {
        if ("name".equals(property)) {
            String msg = String.format("Array Design '%s' deleted", entity.getName());
            getProcessor().addDetail(record, columnName, msg, oldVal, newVal);
            return true;
        }
        return false;
    }

    private boolean organismUnchanged(String property, Object oldVal, Object newVal) {
        return "organism".equals(property) && oldVal != null && newVal != null
                && ((Organism) oldVal).getId().equals(((Organism) newVal).getId());
    }
}
