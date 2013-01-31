//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.domain.protocol;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.Image;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.apache.commons.lang.builder.ToStringBuilder;
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
    private Image image;
    private AbstractArrayData arrayData;
    private String notes;
    private Set<ParameterValue> values = new HashSet<ParameterValue>();

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
        this.image = other.image;
        this.arrayData = other.arrayData;
        this.notes = other.notes;
        for (ParameterValue pv : other.values) {
            ParameterValue newPv = new ParameterValue(pv);
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
     * Gets the image.
     *
     * @return the image
     */
    @ManyToOne
    @ForeignKey(name = "protocolapp_image_fk")
    public Image getImage() {
        return image;
    }

    /**
     * Sets the image.
     *
     * @param image the image
     */
    public void setImage(final Image image) {
        this.image = image;
    }

    /**
     * Gets the values.
     *
     * @return the values
     */
    @OneToMany(mappedBy = "protocolApplication", fetch = FetchType.LAZY)
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.DELETE })
    public Set<ParameterValue> getValues() {
        return values;
    }

    /**
     * Sets the values.
     *
     * @param valuesVal the values
     */
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setValues(final Set<ParameterValue> valuesVal) {
        this.values = valuesVal;
    }

    /**
     * @return the arrayData
     */
    @ManyToOne
    @ForeignKey(name = "protocolapp_arraydata_fk")
    public AbstractArrayData getArrayData() {
        return arrayData;
    }

    /**
     * @param arrayData the arrayData to set
     */
    public void setArrayData(AbstractArrayData arrayData) {
        this.arrayData = arrayData;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
