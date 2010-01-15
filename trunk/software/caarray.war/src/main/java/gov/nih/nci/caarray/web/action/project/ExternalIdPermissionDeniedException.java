package gov.nih.nci.caarray.web.action.project;

import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.security.PermissionDeniedException;

/**
 * A custom exception to show a message specific to retrieving a biomaterial by external id.
 */
public class ExternalIdPermissionDeniedException extends PermissionDeniedException {

    private static final long serialVersionUID = -1271512112927323609L;

    /**
     * Create a new SampleExternalIdPermissionDeniedException for a given user not having the given
     * privilege to the given entity.
     *
     * @param bm the biomaterial on which an operation was requested
     * @param privilege the privilege that was needed to perform the operation
     * @param userName the user attempting the operation
     */
    public ExternalIdPermissionDeniedException(AbstractBioMaterial bm, String privilege, String userName) {
        super(bm, privilege, userName, String.format("User %s does not have %s privilege for %s with external id %s",
                userName, privilege, bm.getClass().getName(), bm.getExternalId()));
    }
}