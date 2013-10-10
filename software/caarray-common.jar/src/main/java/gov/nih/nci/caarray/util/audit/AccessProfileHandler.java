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
            String message = (entity.isPublicProfile() ? "Public" : "Group")
                    + " Access Profile for experiment " + entity.getProject().getExperiment().getTitle()
                    + (entity.isGroupProfile() ? " to group " + entity.getGroup().getGroup().getGroupName() : "")
                    + (oldVal == null ? " set" : " changed from " + oldVal) + " to " + newVal;
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
        String pname = entity.getProject().getExperiment().getTitle();
        StringBuffer sb = new StringBuffer("Access to samples on experiment ");
        sb.append(pname);
        if (entity.isPublicProfile()) {
            sb.append("'s Public Access Profile");
        } else {
            sb.append("'s Access Profile to Group ")
                    .append(entity.getGroup().getGroup().getGroupName());
        }
        AuditLogDetail detail = getProcessor().addDetail(record, columnName, sb.toString());
        boolean added1Sample = false;

        if (o != null) {
            for (Map.Entry<Sample, SampleSecurityLevel> e : o.entrySet()) {
                SampleSecurityLevel v = n.get(e.getKey());
                if (v != null && v != e.getValue()) {
                    StringBuffer m = new StringBuffer(" - Access to sample ");
                    m.append(e.getKey().getName()).append(" changed from ");
                    m.append(e.getValue()).append(" to ").append(v);
                    detail = getProcessor().addDetail(record, columnName, m.toString(), e.getValue(), v);
                    added1Sample = true;
                }
            }
        }

        for (Map.Entry<Sample, SampleSecurityLevel> e : n.entrySet()) {
            // dont log transitions from null to NONE, as they occure when transitioning to a SELECTIVE state.
            if ((o == null || !o.containsKey(e.getKey())) && e.getValue() != SampleSecurityLevel.NONE) {
                String msg = " - Access on sample " + e.getKey().getName() + " set to " + e.getValue();
                detail = getProcessor().addDetail(record, columnName, msg, null, e.getValue());
                added1Sample = true;
            }
        }
        if (added1Sample) {
            getProcessor().addProjectSecurity(record, entity.getProject(), PERMISSIONS_PRIV_ID);
        } else {
            // no need for a header is nothing was added.
            record.getDetails().remove(detail);
        }
        return added1Sample;
    }
}
