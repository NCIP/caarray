//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.plugins.illumina;

import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.platforms.ValueParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Populates the hyb data from each row in the data table.
 * 
 * @param <QT> QuantitationTypeDescriptor
 * @author gax
 * @since 2.4.0
 */
public class HybDataBuilder <QT extends Enum<QT> & QuantitationTypeDescriptor> extends AbstractParser {

    private int rowIndex;
    private final DataSet dataSet;
    private final AbstractHeaderParser<QT> header;
    private final ValueParser valueParser;

    /**
     * @param dataSet the data set to add the values to.
     * @param header parser contructed from the table header.
     * @param valueParser the parser to use to extract data values from the file
     */
    public HybDataBuilder(DataSet dataSet, AbstractHeaderParser<QT> header, ValueParser valueParser) {
        this.rowIndex = 0;
        this.dataSet = dataSet;
        this.header = header;
        this.valueParser = valueParser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean parse(List<String> row, int lineNum) {
        Map<String, AbstractHeaderParser<QT>.ValueLoader> hybMap =
                new HashMap<String, AbstractHeaderParser<QT>.ValueLoader>();
        for (AbstractHeaderParser<QT>.ValueLoader vl : header.getLoaders()) {
            hybMap.put(vl.getHybName(), vl);
        }
        for (HybridizationData d : dataSet.getHybridizationDataList()) {
            AbstractHeaderParser<QT>.ValueLoader vl = hybMap.get(d.getHybridization().getName());
            for (QT qt : vl.getQTypes()) {
                String val = vl.getValue(qt, row);
                valueParser.setValue(d.getColumn(qt), rowIndex, val);
            }
        }
        rowIndex++;
        return true;
    }

}
