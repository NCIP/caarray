//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action;

import gov.nih.nci.caarray.web.upgrade.Migration;
import gov.nih.nci.caarray.web.upgrade.MigrationStatus;
import gov.nih.nci.caarray.web.upgrade.UpgradeManager;

import java.util.List;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Winston Cheng
 *
 */
public class UpgradeStatusAction extends ActionSupport {

    /**
     * {@inheritDoc}
     */
    @Override
    public String execute() {
        return Action.SUCCESS;
    }

    /**
     * @return the list of upgrade steps
     */
    public List<Migration> getUpgradeList() {
        return UpgradeManager.getInstance().getUpgradeList();
    }

    /**
     * @return true if upgrade is in progress
     */
    public boolean isUpgrading() {
        for (Migration m : getUpgradeList()) {
            if (MigrationStatus.COMPLETE.equals(m.getStatus())) {
                continue;
            } else {
                return !MigrationStatus.ERROR.equals(m.getStatus());
            }
        }
        return false;
    }
}
