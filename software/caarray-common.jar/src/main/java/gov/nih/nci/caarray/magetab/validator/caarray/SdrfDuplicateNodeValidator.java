//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.magetab.validator.caarray;

import gov.nih.nci.caarray.magetab.sdrf.SdrfColumn;
import gov.nih.nci.caarray.magetab.sdrf.SdrfColumnType;
import gov.nih.nci.caarray.magetab.sdrf.SdrfColumns;
import gov.nih.nci.caarray.magetab.validator.SdrfColumnsValidator;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Validator enforcing rule that there can be no duplicate node types in the hybridization channel.
 * 
 * @author dharley
 */
public class SdrfDuplicateNodeValidator  implements SdrfColumnsValidator {
    
    private Map<String, ArrayList> nodeInstancesMap;
    
    /**
     * {@inheritDoc}
     */
    public void validate(SdrfColumns columns, List<ValidationMessage> messages) {
        nodeInstancesMap = new HashMap<String, ArrayList>();
        for (SdrfColumn sdrfColumn : columns.getColumns()) {
            if (sdrfColumn.getType().isNode()
                && isNodeTypeThatCannotBeDuplicated(sdrfColumn.getType().getDisplayName())) {
                ArrayList<SdrfColumn> nodesOfThisTypeList = nodeInstancesMap.get(sdrfColumn.getType().getDisplayName());
                if (null == nodesOfThisTypeList) {
                    nodesOfThisTypeList = new ArrayList<SdrfColumn>();
                    nodeInstancesMap.put(sdrfColumn.getType().getDisplayName(), nodesOfThisTypeList);
                }
                nodesOfThisTypeList.add(sdrfColumn);
            }
        }
        for (String nodeType : nodeInstancesMap.keySet()) {
            ArrayList<SdrfColumn> nodesOfThisTypeList = nodeInstancesMap.get(nodeType);
            if (nodesOfThisTypeList.size() > 1) {
                messages.add(new ValidationMessage(Type.ERROR, "SDRF file should only contain 1 '" + nodeType
                        + "' column, but " +  nodesOfThisTypeList.size() + " were found."));
            }
        }
    }
    
    private boolean isNodeTypeThatCannotBeDuplicated(String nodeType) {
        return SdrfColumnType.SOURCE_NAME.getDisplayName().equals(nodeType)
        || SdrfColumnType.SAMPLE_NAME.getDisplayName().equals(nodeType)
        || SdrfColumnType.EXTRACT_NAME.getDisplayName().equals(nodeType)
        || SdrfColumnType.LABELED_EXTRACT_NAME.getDisplayName().equals(nodeType)
        || SdrfColumnType.HYBRIDIZATION_NAME.getDisplayName().equals(nodeType);
    }
    
}
