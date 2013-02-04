//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================

package gov.nih.nci.caarray.domain.protocol;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.ForeignKey;


/**
 * Application of a protocol to an entity, such as a bio material or hybridization.
 */
@Entity
@BatchSize(size = AbstractCaArrayObject.DEFAULT_BATCH_SIZE)
public class ProtocolApplication extends AbstractCaArrayEntity {
    private static final long serialVersionUID = 1234567890L;

    private Protocol protocol;
    private String notes;
    private Set<AbstractParameterValue> values = new HashSet<AbstractParameterValue>();

    /**
     * Default constructor.
     */
    public ProtocolApplication() {
        // needed for hibernate
    }

    /**
     * Constructs a new ProtocolApplication based on another.
     * @param other other ProtocolApplication to make a copy of
     */
    public ProtocolApplication(ProtocolApplication other) {
        this.protocol = other.protocol;
        this.notes = other.notes;
        for (AbstractParameterValue pv : other.values) {
            AbstractParameterValue newPv = null;
            if (pv instanceof AbstractParameterValue) {
                newPv = new MeasurementParameterValue((MeasurementParameterValue) pv);
            } else if (pv instanceof TermBasedParameterValue) {
                newPv = new TermBasedParameterValue((TermBasedParameterValue) pv);
            } else {
                newPv = new UserDefinedParameterValue((UserDefinedParameterValue) pv);
            }
            newPv.setProtocolApplication(this);
            values.add(newPv);
        }
    }

    /**
     * Gets the protocol.
     *
     * @return the protocol
     */
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "protocolapp_protocol")
    public Protocol getProtocol() {
        return protocol;
    }

    /**
     * Sets the protocol.
     *
     * @param protocolVal the protocol
     */
    public void setProtocol(final Protocol protocolVal) {
        this.protocol = protocolVal;
    }

    /**
     * Gets the values.
     *
     * @return the values
     */
    @OneToMany(mappedBy = "protocolApplication", fetch = FetchType.LAZY)
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.DELETE })
    public Set<AbstractParameterValue> getValues() {
        return values;
    }

    /**
     * Sets the values.
     *
     * @param valuesVal the values
     */
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setValues(final Set<AbstractParameterValue> valuesVal) {
        this.values = valuesVal;
    }
    
    /**
     * Return the parameter value for the parameter with given name in this protocol application. 
     * If there is none, return null.
     * @param parameterName name of parameter for which to find a value.
     * @return the parameter value for parameter with given name or null if there is none.
     */
    public AbstractParameterValue getValue(final String parameterName) {
        return (AbstractParameterValue) CollectionUtils.find(getValues(), new Predicate() {
            public boolean evaluate(Object o) {
                AbstractParameterValue fv = (AbstractParameterValue) o;
                return parameterName.equals(fv.getParameter().getName());
            }
        });
    }

    /**
     * @return the notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     * @param notes the notes to set
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
