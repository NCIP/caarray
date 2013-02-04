//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain.data;

import gov.nih.nci.caarray.domain.array.AbstractDesignElement;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Allows for the specification of precisely which array data are to be returned from
 * caArray. Provided as an argument to <code>DataRetrievalService.getData()</code>.
 */
public class DataRetrievalRequest implements Serializable {

    private static final long serialVersionUID = 408918013610243473L;

    private Long id;
    private List<AbstractDesignElement> designElements = new ArrayList<AbstractDesignElement>();
    private List<Hybridization> hybridizations = new ArrayList<Hybridization>();
    private List<QuantitationType> quantitationTypes = new ArrayList<QuantitationType>();

    /**
     * Creates a new, empty request instance.
     */
    public DataRetrievalRequest() {
        super();
    }

    /**
     * Returns the id.
     *
     * @return the id
     * @deprecated only exists to fulfill caDSR loading requirement
     */
    @Deprecated
    public Long getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the id to set
     * @deprecated only exists to fulfill caDSR loading requirement
     */
    @Deprecated
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Include data related to this design element.
     *
     * @param element find data related to this design element.
     */
    public void addDesignElement(AbstractDesignElement element) {
        designElements.add(element);
    }

    /**
     * Include data related to this hybridization.
     *
     * @param hybridization find data related to this hybridization.
     */
    public void addHybridization(Hybridization hybridization) {
        hybridizations.add(hybridization);
    }

    /**
     * Return data for this quantitation type. Only hybridization data that contains
     * all configured <code>QuantitationTypes</code> will be returned.
     *
     * @param quantitationType find data related to this quantitation type.
     */
    public void addQuantitationType(QuantitationType quantitationType) {
        quantitationTypes.add(quantitationType);
    }

    /**
     * @return the designElements
     */
    public List<AbstractDesignElement> getDesignElements() {
        return designElements;
    }

    /**
     * @return the hybridizations
     */
    public List<Hybridization> getHybridizations() {
        return hybridizations;
    }

    /**
     * @return the quantitationTypes
     */
    public List<QuantitationType> getQuantitationTypes() {
        return quantitationTypes;
    }

    // Setters are required for castor
    @SuppressWarnings({ "unused", "PMD.UnusedPrivateMethod" })
    private void setQuantitationTypes(List<QuantitationType> elements) {
        this.quantitationTypes = elements;
    }

    @SuppressWarnings({ "unused", "PMD.UnusedPrivateMethod" })
    private void setDesignElements(List<AbstractDesignElement> elements) {
        this.designElements = elements;
    }

    @SuppressWarnings({ "unused", "PMD.UnusedPrivateMethod" })
    private void setHybridizations(List<Hybridization> elements) {
        this.hybridizations = elements;
    }

}
