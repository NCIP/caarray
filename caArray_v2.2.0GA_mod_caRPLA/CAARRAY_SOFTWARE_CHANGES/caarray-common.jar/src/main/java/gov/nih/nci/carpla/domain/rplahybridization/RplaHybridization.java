//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.carpla.domain.rplahybridization;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.Image;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.project.AbstractExperimentDesignNode;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentDesignNodeType;
import gov.nih.nci.caarray.domain.project.FactorValue;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;
import gov.nih.nci.caarray.security.Protectable;
import gov.nih.nci.carpla.domain.rplarray.RplArray;
import gov.nih.nci.carpla.domain.antibody.Antibody;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

@Entity
public class RplaHybridization extends AbstractExperimentDesignNode {


	
	

	
	
	

	
	// #################################################################
	private Date						_date;

	public Date getDate () {
		return _date;
	}

	public void setDate ( Date date) {
		_date = date;
	}

	// #################################################################
	private RplArray	_rplArray;

	@OneToOne(fetch = FetchType.LAZY)
	public RplArray getRplArray () {
		return _rplArray;

	}

	public void setRplArray ( RplArray rarray) {
		_rplArray = rarray;
	}

	// #################################################################
	
	private ProtocolApplication	_protocolApplication;

	@ManyToOne(fetch = FetchType.LAZY)
	@Cascade( { CascadeType.SAVE_UPDATE, CascadeType.DELETE })
	@ForeignKey(name = "rplahybridization_protocolapp_fk")
	public ProtocolApplication getProtocolApplication () {
		return this._protocolApplication;
	}

	public void setProtocolApplication ( ProtocolApplication protocolApplication)
	{
		_protocolApplication = protocolApplication;
	}

	// #################################################################
	private Antibody	_antibody;

	@ManyToOne
	//@NotNull
	@Cascade( { CascadeType.SAVE_UPDATE})
	public Antibody getAntibody () {
		return _antibody;
	}

	public void setAntibody ( Antibody antibody) {
		_antibody = antibody;

	}

	// #################################################################
	private String	_name;

	@NotNull
	@Length(min = 1, max = DEFAULT_STRING_COLUMN_SIZE)
	public String getName () {
		return _name;
	}

	public void setName ( String name) {
		_name = name;

	}

	// #################################################################
	private Set<Image>					images					= new HashSet<Image>();
	
	@OneToMany
	@Cascade( { CascadeType.SAVE_UPDATE })
	public Set<Image> getImages () {
		return this.images;
	}

	
	private void setImages ( final Set<Image> imagesVal) {
		this.images = imagesVal;
	}

	public void addImage ( Image image) {

		this.images.add(image);
	}

	// #################################################################
	private Set<DerivedArrayData>		derivedDataCollection	= new HashSet<DerivedArrayData>();
	
	@OneToMany
	@Cascade( { CascadeType.DELETE, CascadeType.SAVE_UPDATE })
	public Set<DerivedArrayData> getDerivedDataCollection () {
		return this.derivedDataCollection;
	}

	
	private void setDerivedDataCollection ( final Set<DerivedArrayData> derivedDatasVal)
	{
		this.derivedDataCollection = derivedDatasVal;
	}

	// #################################################################
	
	private Set<RawArrayData>			rawDataCollection		= new HashSet<RawArrayData>();
	@OneToMany
	@Cascade( { CascadeType.SAVE_UPDATE, CascadeType.DELETE })
	public Set<RawArrayData> getRawDataCollection () {
		return this.rawDataCollection;
	}

	
	private void setRawDataCollection ( final Set<RawArrayData> rawDataVals) {
		this.rawDataCollection = rawDataVals;
	}

	
	public void addRawArrayData ( RawArrayData rawArrayData) {
		this.rawDataCollection.add(rawArrayData);
	}

	// #################################################################
	private Experiment					experiment;
	@ManyToOne
	@NotNull
	@Cascade( { CascadeType.ALL})
	public Experiment getExperiment () {
		return experiment;
	}

	public void setExperiment ( Experiment experiment) {
		this.experiment = experiment;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString () {
		return new ToStringBuilder(this).append(this._name).toString();
	}

	/**
	 * @return all the data files related to this hybridization (raw or derived)
	 */
	@Transient
	public Set<CaArrayFile> getAllDataFiles () {
		Set<CaArrayFile> files = new HashSet<CaArrayFile>();
		for (RawArrayData rad : getRawDataCollection()) {
			files.add(rad.getDataFile());
		}
		for (DerivedArrayData dad : getDerivedDataCollection()) {
			files.add(dad.getDataFile());
		}
		return files;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<? extends Protectable> relatedProtectables () {
		return Collections.EMPTY_SET;
	}

	/**
	 * @return the uncompressed size of all associated data files, in bytes
	 */
	@Transient
	public int getUncompressedSizeOfDataFiles () {
		int total = 0;
		for (CaArrayFile caArrayFile : getAllDataFiles()) {
			total += caArrayFile.getUncompressedSize();
		}
		return total;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transient
	public ExperimentDesignNodeType getNodeType () {
		return ExperimentDesignNodeType.RPLAHYBRIDIZATION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transient
	public Set<? extends AbstractExperimentDesignNode> getDirectPredecessors ()
	{
		return Collections.EMPTY_SET;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transient
	public Set<? extends AbstractExperimentDesignNode> getDirectSuccessors () {
		return Collections.emptySet();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doAddDirectPredecessor ( AbstractExperimentDesignNode predecessor)
	{
		throw new IllegalArgumentException("Should never be called.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doAddDirectSuccessor ( AbstractExperimentDesignNode successor)
	{
		throw new IllegalArgumentException("Should never be called");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void merge ( AbstractExperimentDesignNode node) {
		RplaHybridization hyb = (RplaHybridization) node;
		super.merge(hyb);

		mergeImages(hyb);

	}

	private void mergeImages ( RplaHybridization hyb) {
		// not bidirectional with regard to Image
		
		this.getImages().addAll(hyb.getImages());
		hyb.getImages().clear();
	}

	
	@Transient
	public void addProtocolApplication ( ProtocolApplication protocolApplication)
	{
		throw new IllegalArgumentException("Should never be called");
		
	}

	
	public void clearProtocolApplications () {
		throw new IllegalArgumentException("Should never be called");
		
	}

	@Transient
	public List<ProtocolApplication> getProtocolApplications () {
		return Collections.EMPTY_LIST;
	}

	

}
