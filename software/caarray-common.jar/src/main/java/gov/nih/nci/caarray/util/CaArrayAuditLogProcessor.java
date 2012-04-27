/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray Software License (the License) is between NCI and You. You (or
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
 * its rights in the caArray Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray Software; (ii) distribute and
 * have distributed to and by third parties the caArray Software and any
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
package gov.nih.nci.caarray.util;

import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.domain.permissions.SampleSecurityLevel;
import gov.nih.nci.caarray.domain.permissions.SecurityLevel;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.fiveamsolutions.nci.commons.audit.AuditLogDetail;
import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;
import com.fiveamsolutions.nci.commons.audit.AuditType;
import com.fiveamsolutions.nci.commons.audit.DefaultProcessor;

/**
 *
 * @author gax
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class CaArrayAuditLogProcessor extends DefaultProcessor {
    
    private static final Class<?>[] AUDITED_CLASSES = {
        Project.class,
        AccessProfile.class,
        CollaboratorGroup.class,
        Group.class,
        Experiment.class,
        AbstractArrayData.class
    };

    private static final Logger LOG = Logger.getLogger(CaArrayAuditLogProcessor.class);
    /**
     * default constructor, logs security classes.
     */
    CaArrayAuditLogProcessor() {
        super(AUDITED_CLASSES);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAuditableEntity(Object o) {
        boolean isAuditable = super.isAuditableEntity(o);
        if (!isAuditable && CaArrayFile.class.isAssignableFrom(o.getClass())) {
            return FileStatus.SUPPLEMENTAL.equals(((CaArrayFile) o).getFileStatus());
        }
        return isAuditable;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public void processDetail(AuditLogRecord record, Object entity, Serializable key, String property,
            String columnName, Object oldVal, Object newVal) {
        
        if (entity instanceof AccessProfile) {
            logAccessProfile(record, (AccessProfile) entity, property, columnName, oldVal, newVal);
        } else if (entity instanceof Project) {
            logProject(record, (Project) entity, property, columnName, oldVal, newVal);
        } else if (entity instanceof Group) {
            logGroup(record, (Group) entity, property, columnName, oldVal, newVal);
        } else if (entity instanceof CollaboratorGroup) {
            logCollaboratorGroup(record, (CollaboratorGroup) entity, property, columnName, oldVal, newVal);
        } else if (entity instanceof Experiment) {
            logExperiment(record, (Experiment) entity, property, columnName, oldVal, newVal);
        } else if (entity instanceof AbstractArrayData) {
            logArrayData(record, (AbstractArrayData) entity, property, columnName, oldVal, newVal);
        } else if (entity instanceof CaArrayFile) {
            logCaArrayFile(record, (CaArrayFile) entity, property, columnName, oldVal, newVal);
        } else {
            LOG.debug("ignoring property " + record.getEntityName() + "." + property);
        }
    }

    @SuppressWarnings({"PMD.ExcessiveParameterList", "unchecked", "PMD.NPathComplexity" })
    private void logAccessProfile(AuditLogRecord record, AccessProfile entity, String property, String columnName,
            Object oldVal, Object newVal) {
        if ("securityLevelInternal".equals(property)) {
            if (oldVal == null && newVal == SecurityLevel.NONE) {
                return;
            }
            AuditLogDetail detail = new AuditLogDetail(record, columnName, null, null);
            super.processDetail(detail, oldVal, newVal);
            String type = entity.isPublicProfile() ? "Public" : "Group";
            detail.setMessage(type + " Access Profile for experiment " + entity.getProject().getExperiment().getTitle()
                    + (entity.isGroupProfile() ? " to group " + entity.getGroup().getGroup().getGroupName() : "")
                    + (oldVal == null ? " set" : " changed from " + oldVal) + " to " + newVal);
            record.getDetails().add(detail);

        } else if ("sampleSecurityLevels".equals(property)) {
            logAccessProfileSamples(record, entity, columnName, (Map<Sample, SampleSecurityLevel>) oldVal,
                    (Map<Sample, SampleSecurityLevel>) newVal);
        } else {
            LOG.debug("ignoring property " + record.getEntityName() + "." + property);
        }
    }

    @SuppressWarnings({"PMD.ExcessiveParameterList", "unchecked" })
    private void logProject(AuditLogRecord record, Project project, String property, String columnName,
            Object oldVal, Object newVal) {
        if ("locked".equals(property)) {
            AuditLogDetail detail = new AuditLogDetail(record, columnName, null, null);
            super.processDetail(detail, oldVal, newVal);
            String newS = Boolean.TRUE.equals(newVal) ? "Locked" : "Unlocked";
            detail.setMessage("Experiment " + project.getExperiment().getTitle() + " " + newS
                    + (record.getType() == AuditType.INSERT ?  " when created" : ""));
            record.getDetails().add(detail);
        } else if ("files".equals(property)) {
            Set<Long> newIds = new HashSet<Long>();
            for (CaArrayFile newFile : (Set<CaArrayFile>) newVal) {
                newIds.add(newFile.getId());
            }
            for (CaArrayFile oldFile : (Set<CaArrayFile>) oldVal) {
                if (!newIds.contains(oldFile.getId()) && auditFileDeletion(oldFile)) {
                    addExperimentDetail(record, columnName, project.getExperiment());
                    AuditLogDetail detail = new AuditLogDetail(record, columnName, null, null);
                    super.processDetail(detail, oldFile, null);
                    detail.setMessage(" - File " + oldFile.getName() + " deleted");
                    record.getDetails().add(detail);
                }
            }
        } else {
            LOG.debug("ignoring property " + record.getEntityName() + "." + property);
        }
    }
    
    private boolean auditFileDeletion(CaArrayFile file) {
        FileStatus fileStatus = file.getFileStatus();
        if (FileStatus.SUPPLEMENTAL.equals(fileStatus)) {
            return true;
        } else if (FileStatus.IMPORTED.equals(fileStatus) || FileStatus.IMPORTED_NOT_PARSED.equals(fileStatus)) {
            FileType fileType = file.getFileType();
            FileCategory fileCat = fileType == null ? null : fileType.getCategory();
            return FileCategory.DERIVED_DATA.equals(fileCat) || FileCategory.RAW_DATA.equals(fileCat);
        }
        return false;
    }

    @SuppressWarnings({"PMD.ExcessiveParameterList", "unchecked" })
    private void logGroup(AuditLogRecord record, Group entity, String property, String columnName,
            Object oldVal, Object newVal) {
        if ("groupName".equals(property)) {
            // there is no group.users event on insert so we'll log new users here.
            if (record.getType() == AuditType.INSERT) {
                AuditLogDetail detail = new AuditLogDetail(record, columnName, null, null);
                super.processDetail(detail, oldVal, newVal);
                detail.setMessage("Group " + newVal + " created");
                record.getDetails().add(detail);
                if (entity.getUsers() != null) {
                    for (Object u : entity.getUsers()) {
                        detail = new AuditLogDetail(record, columnName, null, null);
                        super.processDetail(detail, null, u);
                        detail.setMessage("User " + ((User) u).getLoginName() + " added to group "
                                + entity.getGroupName());
                        record.getDetails().add(detail);
                    }
                }
            } else if (record.getType() == AuditType.UPDATE) {
                AuditLogDetail detail = new AuditLogDetail(record, columnName, null, null);
                super.processDetail(detail, oldVal, newVal);
                detail.setMessage("Group name changed from " + oldVal + " to " + newVal);
                record.getDetails().add(detail);
            }
        } else if ("users".equals(property)) {
            Collection<User> tmp = CollectionUtils.subtract((Set) oldVal, (Set) newVal);
            for (User u : tmp) {
                AuditLogDetail detail = new AuditLogDetail(record, columnName, null, null);
                super.processDetail(detail, u, null);
                detail.setMessage("User " + u.getLoginName() + " removed from group " + entity.getGroupName());
                record.getDetails().add(detail);
            }
            tmp = CollectionUtils.subtract((Set) newVal, (Set) oldVal);
            for (User u : tmp) {
                AuditLogDetail detail = new AuditLogDetail(record, columnName, null, null);
                super.processDetail(detail, null, u);
                detail.setMessage("User " + u.getLoginName() + " added to group " + entity.getGroupName());
                record.getDetails().add(detail);
            }
        } else {
            LOG.debug("ignoring property " + record.getEntityName() + "." + property);
        }
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    private void logCollaboratorGroup(AuditLogRecord record, CollaboratorGroup entity, String property, 
            String columnName, Object oldVal, Object newVal) {
        if ("ownerId".equals(property)) {
            AuditLogDetail detail = new AuditLogDetail(record, columnName, null, null);
            super.processDetail(detail, oldVal, newVal);
            detail.setMessage("Collaborator Group " + entity.getGroup().getGroupName() + " owner set to "
                    + entity.getOwner().getLoginName());
            record.getDetails().add(detail);
        } else {
            LOG.debug("ignoring property " + record.getEntityName() + "." + property);
        }
    }
    
    @SuppressWarnings({"PMD.ExcessiveParameterList", "unchecked" })
    private void logExperiment(AuditLogRecord record, Experiment experiment, String property, 
            String columnName, Object oldVal, Object newVal) {
        if ("samples".equals(property)) {
            Set<Long> oldIds = new HashSet<Long>();
            Set<Long> newIds = new HashSet<Long>();
            for (Sample s : (Set<Sample>) newVal) {
                newIds.add(s.getId());
            }
            for (Sample s : (Set<Sample>) oldVal) {
                oldIds.add(s.getId());
                if (!newIds.contains(s.getId())) {
                    addExperimentDetail(record, columnName, experiment);
                    AuditLogDetail detail = new AuditLogDetail(record, columnName, null, null);
                    super.processDetail(detail, s, null);
                    detail.setMessage(" - Sample " + s.getName() + " deleted");
                    record.getDetails().add(detail);
                }
            }
            for (Sample s : (Set<Sample>) newVal) {
                if (!oldIds.contains(s.getId())) {
                    addExperimentDetail(record, columnName, experiment);
                    AuditLogDetail detail = new AuditLogDetail(record, columnName, null, null);
                    super.processDetail(detail, null, s);
                    detail.setMessage(" - Sample " + s.getName() + " added");
                    record.getDetails().add(detail);
                }
            }
        }
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    private void logCaArrayFile(AuditLogRecord record, CaArrayFile file, String property, 
            String columnName, Object oldVal, Object newVal) {
        if ("status".equals(property)
                && FileStatus.valueOf((String) newVal) == FileStatus.SUPPLEMENTAL) {
            addExperimentDetail(record, columnName, file.getProject().getExperiment());
            AuditLogDetail detail = new AuditLogDetail(record, columnName, (String) oldVal, (String) newVal);
            detail.setMessage(" - Supplementail File " + file.getName() + " added");
            record.getDetails().add(detail);
        }
    }

    @SuppressWarnings({"PMD.ExcessiveParameterList", "unchecked" })
    private void logArrayData(AuditLogRecord record, AbstractArrayData ad, String property, 
            String columnName, Object oldVal, Object newVal) {
        if ("hybridizations".equals(property)) {
            Set<Hybridization> oldHybs = oldVal == null ? new HashSet<Hybridization>() : (Set<Hybridization>) oldVal;
            Set<Hybridization> newHybs = newVal == null ? new HashSet<Hybridization>() : (Set<Hybridization>) newVal;
            if (newHybs.size() > oldHybs.size()) {
                CaArrayFile file = ad.getDataFile();
                addExperimentDetail(record, columnName, file.getProject().getExperiment());
                AuditLogDetail detail = new AuditLogDetail(record, columnName, null, null);
                super.processDetail(detail, null, newVal);
                detail.setMessage(" - Data File " + file.getName() + " added");
                record.getDetails().add(detail);
            }
        }
    }
    
    private void logAccessProfileSamples(AuditLogRecord record, AccessProfile entity, String columnName,
            Map<Sample, SampleSecurityLevel> o, Map<Sample, SampleSecurityLevel> n) {
        
        // header message.
        String pname = entity.getProject().getExperiment().getTitle();
        AuditLogDetail detail = new AuditLogDetail(record, columnName, null, null);
        StringBuffer sb = new StringBuffer("Access to samples on experiment ");
        sb.append(pname);
        if (entity.isPublicProfile()) {
            sb.append("'s Public Access Profile");
        } else {
            sb.append("'s Access Profile to Group ")
                    .append(entity.getGroup().getGroup().getGroupName());
        }        
        detail.setMessage(sb.toString());
        record.getDetails().add(detail);
        boolean added1Sample = false;

        if (o != null) {
            for (Map.Entry<Sample, SampleSecurityLevel> e : o.entrySet()) {
                SampleSecurityLevel v = n.get(e.getKey());
                if (v != null && v != e.getValue()) {
                    StringBuffer m = new StringBuffer(" - Access to sample ").append(e.getKey().getName());
                    m.append(" changed from ").append(e.getValue()).append(" to ").append(v);                
                    detail = new AuditLogDetail(record, columnName, null, null);
                    super.processDetail(detail, e.getValue(), v);
                    detail.setMessage(m.toString());
                    record.getDetails().add(detail);
                    added1Sample = true;
                }
            }
        }

        for (Map.Entry<Sample, SampleSecurityLevel> e : n.entrySet()) {
            // dont log transitions from null to NONE, as they occure when transitioning to a SELECTIVE state.
            if ((o == null || !o.containsKey(e.getKey())) && e.getValue() != SampleSecurityLevel.NONE) {
                detail = new AuditLogDetail(record, columnName, null, null);
                super.processDetail(detail, null, e.getValue());
                detail.setMessage(" - Access on sample " + e.getKey().getName() + " set to "
                        + e.getValue());
                record.getDetails().add(detail);
                added1Sample = true;
            }
        }
        if (!added1Sample) {
            // no need for a header is nothing was added.
            record.getDetails().remove(detail);
        }
    }
    
    private void addExperimentDetail(AuditLogRecord record, String attribute, Experiment experiment) {
        if (record.getDetails().size() == 0) {
            AuditLogDetail detail = new AuditLogDetail(record, attribute, null, null);
            detail.setMessage("Experiment " + experiment.getTitle() + ":");
            record.getDetails().add(detail);
        }
    }
}
