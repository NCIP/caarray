//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.util.audit;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import java.util.Set;

import org.junit.Test;

import com.fiveamsolutions.nci.commons.audit.AuditLogRecord;
import com.fiveamsolutions.nci.commons.audit.AuditType;
import com.google.common.collect.Sets;

import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;

/**
 * @author wcheng
 *
 */
public class ArrayDataHandlerTest extends AbstractAuditHandlerTest<AbstractArrayData> {
    private static final String HYBRIDIZATIONS = "hybridizations";
    
    @Override
    protected void setupHandler() {
        handler = new ArrayDataHandler(processor);
    }

    @Override
    protected void setupEntity() {
        CaArrayFile file = new CaArrayFile();
        file.setName("TestFile");
        entity = new RawArrayData();
        entity.setDataFile(file);
    }

    @Test
    public void addedDataFile() {
        Set<Hybridization> newVal = Sets.newHashSet(new Hybridization());
        AuditLogRecord record = createAuditLogRecord(AuditType.INSERT);
        assertTrue(logEntity(record, HYBRIDIZATIONS, null, newVal));
        verify(processor).addDetail(record, HYBRIDIZATIONS, " - Data File TestFile added", null, newVal);
    }
}
