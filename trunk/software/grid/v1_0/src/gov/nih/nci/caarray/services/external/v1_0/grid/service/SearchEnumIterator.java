//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.external.v1_0.grid.service;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.array.ArrayDesign;
import gov.nih.nci.caarray.external.v1_0.array.ArrayProvider;
import gov.nih.nci.caarray.external.v1_0.array.AssayType;
import gov.nih.nci.caarray.external.v1_0.data.ArrayDataType;
import gov.nih.nci.caarray.external.v1_0.data.File;
import gov.nih.nci.caarray.external.v1_0.data.FileType;
import gov.nih.nci.caarray.external.v1_0.data.QuantitationType;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.experiment.ExperimentalContact;
import gov.nih.nci.caarray.external.v1_0.experiment.Organism;
import gov.nih.nci.caarray.external.v1_0.experiment.Person;
import gov.nih.nci.caarray.external.v1_0.factor.Factor;
import gov.nih.nci.caarray.external.v1_0.sample.Biomaterial;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Category;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Term;
import gov.nih.nci.caarray.external.v1_0.vocabulary.TermSource;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchResultIterator;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;

import org.globus.ws.enumeration.EnumIterator;
import org.globus.ws.enumeration.IterationConstraints;
import org.globus.ws.enumeration.IterationResult;
import org.globus.ws.enumeration.TimeoutException;
import org.globus.wsrf.encoding.ObjectSerializer;

/**
 * @author dkokotov
 *
 */
public class SearchEnumIterator<T extends AbstractCaArrayEntity> implements EnumIterator {
    private static final Map<Class<?>, QName> CLASS_TO_QNAME = new HashMap<Class<?>, QName>();
    static {
        CLASS_TO_QNAME.put(Experiment.class, new QName(
                "gme://External.caArray.caBIG/1.0/gov.nih.nci.caarray.external.experiment", "Experiment"));
        CLASS_TO_QNAME.put(Organism.class, new QName(
                "gme://External.caArray.caBIG/1.0/gov.nih.nci.caarray.external.experiment", "Organism"));
        CLASS_TO_QNAME.put(Person.class, new QName(
                "gme://External.caArray.caBIG/1.0/gov.nih.nci.caarray.external.experiment", "Person"));
        CLASS_TO_QNAME.put(ExperimentalContact.class, new QName(
                "gme://External.caArray.caBIG/1.0/gov.nih.nci.caarray.external.experiment", "ExperimentalContact"));
        CLASS_TO_QNAME.put(Term.class, new QName(
                "gme://External.caArray.caBIG/1.0/gov.nih.nci.caarray.external.vocabulary", "Term"));
        CLASS_TO_QNAME.put(Category.class, new QName(
                "gme://External.caArray.caBIG/1.0/gov.nih.nci.caarray.external.vocabulary", "Category"));
        CLASS_TO_QNAME.put(TermSource.class, new QName(
                "gme://External.caArray.caBIG/1.0/gov.nih.nci.caarray.external.vocabulary", "TermSource"));
        CLASS_TO_QNAME.put(Factor.class, new QName(
                "gme://External.caArray.caBIG/1.0/gov.nih.nci.caarray.external.factor", "Factor"));
        CLASS_TO_QNAME.put(Biomaterial.class, new QName(
                "gme://External.caArray.caBIG/1.0/gov.nih.nci.caarray.external.sample", "Biomaterial"));
        CLASS_TO_QNAME.put(Hybridization.class, new QName(
                "gme://External.caArray.caBIG/1.0/gov.nih.nci.caarray.external.sample", "Hybridization"));
        CLASS_TO_QNAME.put(File.class, new QName(
                "gme://External.caArray.caBIG/1.0/gov.nih.nci.caarray.external.data", "File"));
        CLASS_TO_QNAME.put(FileType.class, new QName(
                "gme://External.caArray.caBIG/1.0/gov.nih.nci.caarray.external.data", "FileType"));
        CLASS_TO_QNAME.put(QuantitationType.class, new QName(
                "gme://External.caArray.caBIG/1.0/gov.nih.nci.caarray.external.data", "QuantitationType"));
        CLASS_TO_QNAME.put(ArrayDataType.class, new QName(
                "gme://External.caArray.caBIG/1.0/gov.nih.nci.caarray.external.data", "ArrayDataType"));
        CLASS_TO_QNAME.put(ArrayProvider.class, new QName(
                "gme://External.caArray.caBIG/1.0/gov.nih.nci.caarray.external.array", "ArrayProvider"));
        CLASS_TO_QNAME.put(ArrayDesign.class, new QName(
                "gme://External.caArray.caBIG/1.0/gov.nih.nci.caarray.external.array", "ArrayDesign"));
        CLASS_TO_QNAME.put(AssayType.class, new QName(
                "gme://External.caArray.caBIG/1.0/gov.nih.nci.caarray.external.array", "AssayType"));
    }

    private SearchResultIterator<T> searchResultIterator;
    private QName qname;

    public SearchEnumIterator(Class<T> elementClass, SearchResultIterator<T> searchResultIterator) throws RemoteException {
        this.searchResultIterator = searchResultIterator;
        this.qname = CLASS_TO_QNAME.get(elementClass);
    }
    
    /**
     * {@inheritDoc}
     */
    public IterationResult next(IterationConstraints ic) throws TimeoutException, NoSuchElementException {
        if (this.searchResultIterator == null) {
            throw new NoSuchElementException("Enumeration has been released");
        }
        
        List<SOAPElement> soapElements = new ArrayList<SOAPElement>(ic.getMaxElements());        
        try {
            for (int i = 0; i < ic.getMaxElements() && searchResultIterator.hasNext(); i++) {
                T nextResult = searchResultIterator.next();
                SOAPElement element = ObjectSerializer.toSOAPElement(nextResult, this.qname);
                soapElements.add(element);
            }
           
            return wrapUpElements(soapElements, soapElements.size() < ic.getMaxElements());
        } catch (Exception ex) {
            release();
            NoSuchElementException nse = new NoSuchElementException("Error generating elements " + ex.getMessage());
            nse.setStackTrace(ex.getStackTrace());
            throw nse;
        }
    }
    
    protected IterationResult wrapUpElements(List<SOAPElement> soapElements, boolean end) {
        SOAPElement[] elements = new SOAPElement[soapElements.size()];
        soapElements.toArray(elements);
        return new IterationResult(elements, end);
    }
    
    /**
     * {@inheritDoc}
     */
    public void release() {
        this.searchResultIterator = null;
    }
}
