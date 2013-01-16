//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.external.v1_0.impl;

import gov.nih.nci.caarray.external.v1_0.value.MeasurementValue;
import gov.nih.nci.caarray.external.v1_0.value.TermValue;
import gov.nih.nci.caarray.external.v1_0.value.UserDefinedValue;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Term;
import gov.nih.nci.caarray.services.external.BeanMapperLookup;
import net.sf.dozer.util.mapping.MapperIF;
import net.sf.dozer.util.mapping.converters.CustomConverter;

/**
 * Dozer converter for factor value. 
 * 
 * @author dkokotov
 *
 */
public class ValueConverter implements CustomConverter {
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Object convert(Object dest, Object src, Class destClass, Class srcClass) {
        if (src == null) {
            return null;
        } else if (src instanceof String) {
            UserDefinedValue value = new UserDefinedValue();
            value.setValue((String) src);
            return value;
        } else if (src instanceof Float) {
            MeasurementValue mval = new MeasurementValue();
            mval.setMeasurement((Float) src);
            return mval;
        } else if (src instanceof gov.nih.nci.caarray.domain.vocabulary.Term) {
            TermValue tval = new TermValue();
            MapperIF mapper = BeanMapperLookup.getMapper(BeanMapperLookup.VERSION_1_0); 
            Term term = (Term) mapper.map(src, Term.class);
            tval.setTerm(term);
            return tval;
        } else {
            return null;
        }
    }
}
