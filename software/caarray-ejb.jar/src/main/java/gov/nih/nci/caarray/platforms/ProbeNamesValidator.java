//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.platforms;

import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates probe names against those actually present in an array design.
 * @author dharley
 *
 */
public class ProbeNamesValidator {
    
    private final ArrayDao arrayDao;
    private final ArrayDesign arrayDesign;
    
    /**   
     * Creates a new ProbeNamesValidator.
     * @param arrayDao the array DAO to use.
     * @param arrayDesign the array design to validate against.
     */
    public ProbeNamesValidator(final ArrayDao arrayDao, final ArrayDesign arrayDesign) {
        this.arrayDao = arrayDao;
        this.arrayDesign = arrayDesign;
    }

    /**
     * Validates a list of probe names against the specified array design.  Does not support probes with aliases
     * (i.e., can only have a single probe name).
     * @param fileValidationResult the validation result to write to.
     * @param potentiallyInvalidProbeNames the list of probe names to validate.
     */
    public void validateProbeNames(final FileValidationResult fileValidationResult,
            final List<String> potentiallyInvalidProbeNames) {
        potentiallyInvalidProbeNames.removeAll(getProbeNamesThatArePresentInDesign(potentiallyInvalidProbeNames));
        for (String badProbeName : potentiallyInvalidProbeNames) {
            fileValidationResult.addMessage(
                    Type.ERROR, formatErrorMessage(new String[] {(String) badProbeName}, arrayDesign));
        }
    }

    /**
     * Returns a properly-formatted error message for cases when a probe name is not found in an array design.
     * @param badProbeNameAliases the aliases for the bad probe name.
     * @param arrayDesign the array design which does not contain the bad probe name.
     * @return error message in proper format.
     */
    public static String formatErrorMessage(final String[] badProbeNameAliases, final ArrayDesign arrayDesign) {
        StringBuilder stringBuilder = new StringBuilder();
        if (badProbeNameAliases.length > 1) {
            stringBuilder.append("Probe with aliases ");
        } else {
            stringBuilder.append("Probe with name ");
        }
        for (int i = 0; i < badProbeNameAliases.length; i++) {
            stringBuilder.append("'" + badProbeNameAliases[i] + "'");
            if (i != (badProbeNameAliases.length - 1)) {
                stringBuilder.append(",");
            }
        }
        stringBuilder.append(" was not found in array design '" + arrayDesign.getName() + "' version '"
                + arrayDesign.getVersion() + "'.");
        return stringBuilder.toString();
    }
    
    private List<String> getProbeNamesThatArePresentInDesign(final List<String> probeNames) {
        List<PhysicalProbe> physicalProbesPresentInDesign = arrayDao.getPhysicalProbeByNames(arrayDesign, probeNames);
        List<String> probeNamesThatArePresentInDesign = new ArrayList<String>();
        for (PhysicalProbe probe : physicalProbesPresentInDesign) {
            probeNamesThatArePresentInDesign.add(probe.getName());
        }
        return probeNamesThatArePresentInDesign;
    }

}
