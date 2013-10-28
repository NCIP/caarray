//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.util.audit;

import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.permissions.SampleSecurityLevel;
import gov.nih.nci.caarray.domain.permissions.SecurityLevel;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.util.CaArrayAuditLogProcessor;

import java.util.Map;

import com.fiveamsolutions.nci.commons.audit.AuditLogDetail;
import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;

/**
 * @author wcheng
 *
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class AccessProfileHandler extends AbstractAuditEntityHandler<AccessProfile> {

    /**
     * @param processor audit log processor
     */
    public AccessProfileHandler(CaArrayAuditLogProcessor processor) {
        super(processor);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({"unchecked", "PMD.NPathComplexity", "PMD.ExcessiveParameterList" })
    protected boolean logAddOrUpdate(AuditLogRecord record, AccessProfile entity, String property, String columnName,
            Object oldVal, Object newVal) {
        boolean addedEntry = false;
        if ("securityLevelInternal".equals(property)) {
            if (oldVal == null && newVal == SecurityLevel.NONE) {
                return false;
            }
            String valueMsg = oldVal == null ? String.format("set to %s", newVal)
                    : String.format("changed from %s to %s", oldVal, newVal);
            String groupMsg = entity.isGroupProfile()
                    ? String.format(" to group %s", entity.getGroup().getGroup().getGroupName()) : "";
            String message = String.format("%s Access Profile for experiment %s%s %s",
                    entity.isPublicProfile() ? "Public" : "Group", entity.getProject().getExperiment().getTitle(),
                    groupMsg, valueMsg);
            getProcessor().addDetail(record, columnName, message, oldVal, newVal);
            getProcessor().addProjectSecurity(record, entity.getProject(), PERMISSIONS_PRIV_ID);
            addedEntry = true;
        } else if ("sampleSecurityLevels".equals(property)) {
            addedEntry = logAccessProfileSamples(record, entity, columnName, (Map<Sample, SampleSecurityLevel>) oldVal,
                    (Map<Sample, SampleSecurityLevel>) newVal);
        }
        return addedEntry;
        
    }

    private boolean logAccessProfileSamples(AuditLogRecord record, AccessProfile entity, String columnName,
            Map<Sample, SampleSecurityLevel> o, Map<Sample, SampleSecurityLevel> n) {

        // header message.
        String profileType = entity.isPublicProfile() ? "Public Access Profile"
                : "Access Profile to Group " + entity.getGroup().getGroup().getGroupName();
        String headerMsg = String.format("Access to samples on experiment %s's %s",
                entity.getProject().getExperiment().getTitle(), profileType);
        AuditLogDetail detail = getProcessor().addDetail(record, columnName, headerMsg);

        boolean updatedSample = updatedSampleAccess(record, columnName, o, n);
        boolean addedSample = addedSampleAccess(record, columnName, o, n);

        if (updatedSample || addedSample) {
            getProcessor().addProjectSecurity(record, entity.getProject(), PERMISSIONS_PRIV_ID);
            return true;
        } else {
            // no need for a header is nothing was added.
            record.getDetails().remove(detail);
            return false;
        }
    }

    private boolean addedSampleAccess(AuditLogRecord record, String columnName,
            Map<Sample, SampleSecurityLevel> oldVal, Map<Sample, SampleSecurityLevel> newVal) {
        boolean added1Sample = false;
        for (Map.Entry<Sample, SampleSecurityLevel> e : newVal.entrySet()) {
            // dont log transitions from null to NONE, as they occure when transitioning to a SELECTIVE state.
            if ((oldVal == null || !oldVal.containsKey(e.getKey())) && e.getValue() != SampleSecurityLevel.NONE) {
                String msg = String.format(" - Access on sample %s set to %s", e.getKey().getName(), e.getValue());
                getProcessor().addDetail(record, columnName, msg, null, e.getValue());
                added1Sample = true;
            }
        }
        return added1Sample;
    }

    private boolean updatedSampleAccess(AuditLogRecord record, String columnName,
            Map<Sample, SampleSecurityLevel> oldVal, Map<Sample, SampleSecurityLevel> newVal) {
        boolean added1Sample = false;
        if (oldVal != null) {
            for (Map.Entry<Sample, SampleSecurityLevel> e : oldVal.entrySet()) {
                SampleSecurityLevel v = newVal.get(e.getKey());
                if (v != null && v != e.getValue()) {
                    String msg = String.format(" - Access to sample %s changed from %s to %s",
                            e.getKey().getName(), e.getValue(), v);
                    getProcessor().addDetail(record, columnName, msg, e.getValue(), v);
                    added1Sample = true;
                }
            }
        }
        return added1Sample;
    }
}
