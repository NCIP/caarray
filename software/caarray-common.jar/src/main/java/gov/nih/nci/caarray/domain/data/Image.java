//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.domain.data;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.ForeignKey;

  /**

   */
@Entity
public class Image extends AbstractCaArrayEntity {
    private static final long serialVersionUID = 1234567890L;

    private String name;
    private Set<ProtocolApplication> protocolApplications = new HashSet<ProtocolApplication>();
    private CaArrayFile imageFile;
    private Hybridization hybridization;
    private RawArrayData rawArrayData;

    /**
     * Gets the name.
     *
     * @return the name
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE, nullable = false)
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
    @JoinColumn(name = "image")
    @ForeignKey(name = "protocolapp_image_fk")
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
     * Gets the imageFile.
     *
     * @return the imageFile
     */
    @ManyToOne
    @ForeignKey(name = "image_file_fk")
    public CaArrayFile getImageFile() {
        return imageFile;
    }

    /**
     * Sets the imageFile.
     *
     * @param imageFileVal the imageFile
     */
    public void setImageFile(final CaArrayFile imageFileVal) {
        this.imageFile = imageFileVal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return super.toString() + ", name=" + getName();
    }

    /**
     * @return the hybridization
     */
    @ManyToOne()
    @Cascade(CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "image_hybridization_fk")
    public Hybridization getHybridization() {
        return hybridization;
    }

    /**
     * @param hybridization the hybridization to set
     */
    public void setHybridization(Hybridization hybridization) {
        this.hybridization = hybridization;
    }

    /**
     * @return the raw array data
     */
    @ManyToOne
    @ForeignKey(name = "image_rawarraydata_fk")
    public RawArrayData getRawArrayData() {
        return rawArrayData;
    }

    /**
     * @param rawArrayData the raw array data to set
     */
    public void setRawArrayData(RawArrayData rawArrayData) {
        this.rawArrayData = rawArrayData;
    }
}
