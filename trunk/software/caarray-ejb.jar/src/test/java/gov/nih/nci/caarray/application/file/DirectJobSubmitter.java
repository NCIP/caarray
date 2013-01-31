//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.file;

/**
 * Submitter that circumvents JMS
 */
class DirectJobSubmitter implements FileManagementJobSubmitter {

    private final FileManagementMDB fileManagementMDB;

    DirectJobSubmitter(FileManagementMDB fileManagementMDB) {
        this.fileManagementMDB = fileManagementMDB;
    }

    public void submitJob(AbstractFileManagementJob job) {
        StubbedObjectMessage message = new StubbedObjectMessage(job);
        fileManagementMDB.onMessage(message);
    }

}
