//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.domain.data;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.NotNull;

/**
 *
 */
@Entity
@Table(name = "arraydata")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "discriminator",
        discriminatorType = DiscriminatorType.STRING
)
public abstract class AbstractArrayData extends AbstractCaArrayEntity {

    private static final long serialVersionUID = 1234567890L;
    private String name;
    private CaArrayFile dataFile;
    private ArrayDataType type;
    private Set<ProtocolApplication> protocolApplications = new HashSet<ProtocolApplication>();
    private DataSet dataSet;

    /**
     * Gets the dataFile.
     *
     * @return the dataFile
     */
    @ManyToOne
    @NotNull
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.DELETE })
    @ForeignKey(name = "arraydata_file_fk")
    public CaArrayFile getDataFile() {
        return dataFile;
    }

    /**
     * Sets the dataFile.
     *
     * @param dataFileVal the dataFile
     */
    public void setDataFile(final CaArrayFile dataFileVal) {
        this.dataFile = dataFileVal;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param nameVal the name
     */
    public void setName(final String nameVal) {
        this.name = nameVal;
    }

    /**
     * Gets the protocolApplications.
     *
     * @return the protocolApplications
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "array_data")
    @ForeignKey(name = "protocolapp_arraydata_fk")
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    public Set<ProtocolApplication> getProtocolApplications() {
        return protocolApplications;
    }

    /**
     * Sets the protocolApplications.
     *
     * @param protocolApplicationsVal the protocolApplications
     */
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setProtocolApplications(final Set<ProtocolApplication> protocolApplicationsVal) {
        this.protocolApplications = protocolApplicationsVal;
    }

    /**
     * @return the type
     */
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "arraydata_type_fk")
    public ArrayDataType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(ArrayDataType type) {
        this.type = type;
    }

    /**
     * @return the dataSet
     */
    @OneToOne
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.DELETE })
    public DataSet getDataSet() {
        return dataSet;
    }

    /**
     * @param dataSet the dataSet to set
     */
    public void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append(name).append(dataFile).toString();
    }
    
    /**
     * @return the set of hybridizations associated with this array data.
     */
    @Transient
    public abstract Set<Hybridization> getHybridizations();
}
