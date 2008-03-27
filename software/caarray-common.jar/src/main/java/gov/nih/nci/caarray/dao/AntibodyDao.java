package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.ArrayDataType;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.carpla.domain.Antibody;

import java.util.List;

public interface AntibodyDao {
	
	
	
//	
//
//    /**
//     * Returns the <code>ArrayDesign</code> with the id given.
//     *
//     * @param id get <code>ArrayDesign</code> matching this id
//     * @return the <code>ArrayDesign</code>.
//     */
//    ArrayDesign getArrayDesign(long id);
	
	
	Antibody getAntibody(long id);

	List<Antibody> getAntibodies ();
		
		

	
	
	
	
	
//
//    /**
//     * Returns the entity matching the LSID given.
//     *
//     * @param lsidAuthority the LSID authority
//     * @param lsidNamespace the LSID namespace
//     * @param lsidObjectId the LSID object ID
//     * @return the matching design or null.
//     */
//    ArrayDesign getArrayDesign(String lsidAuthority, String lsidNamespace, String lsidObjectId);
//
//    /**
//     * Returns the list of all ArrayDesigns.
//     * @return the List&lt;ArrayDesign&gt; of the array designs
//     */
//    List<ArrayDesign> getArrayDesigns();
//
//    /**
//     * Returns the list of ArrayDesigns with the given provider.
//     * @param provider the provider
//     * @param importedOnly whether to only return ArrayDesigns which have finished importing (ie
//     * whose corresponding design file has a status of IMPORTED). if true only those array designs
//     * are returned, otherwise, all array designs are.
//     * @return the List&lt;ArrayDesign&gt; of the array designs whose
//     * provider is the given provider
//     */
//    List<ArrayDesign> getArrayDesignsForProvider(Organization provider, boolean importedOnly);
//
//    /**
//     * Returns the list of ArrayDesigns with the given provider and assay type.
//     * @param provider the provider
//     * @param assayType the assay type
//     * @param importedOnly whether to only return ArrayDesigns which have finished importing (ie
//     * whose corresponding design file has a status of IMPORTED). if true only those array designs
//     * are returned, otherwise, all array designs are.
//     * @return the List&lt;ArrayDesign&gt; of the array designs with the given provider and assay type
//     */
//    List<ArrayDesign> getArrayDesigns(Organization provider, AssayType assayType, boolean importedOnly);
//
//    /**
//     * Returns the list of Organizations that are a provider for at least
//     * one ArrayDesign in the system.
//     * @return the List&lt;Organization&gt; of Organizations where for each
//     * organization in the list there exists at least one ArrayDesign for which
//     * that Organization is the provider
//     */
//    List<Organization> getArrayDesignProviders();
//
//    /**
//     * Returns the array data object for the id given.
//     *
//     * @param id get <code>AbstractArrayData</code> matching this id
//     * @return the associated data.
//     */
//    AbstractArrayData getArrayData(long id);
//
//    /**
//     * Returns the <code>RawArrayData</code> object associated with the file provided.
//     *
//     * @param file find data for this file.
//     * @return the associated data or null.
//     */
//    RawArrayData getRawArrayData(CaArrayFile file);
//
//    /**
//     * Returns the <code>DerivedArrayData</code> object associated with the file provided.
//     *
//     * @param file find data for this file.
//     * @return the associated data or null.
//     */
//    DerivedArrayData getDerivedArrayData(CaArrayFile file);
//
//    /**
//     * Returns the array data type corresponding to the given descriptor, if one
//     * exists.
//     *
//     * @param descriptor search for the matching type
//     * @return the matching type, or null if not in the database.
//     */
//    ArrayDataType getArrayDataType(ArrayDataTypeDescriptor descriptor);
//
//    /**
//     * Returns the quantitation type corresponding to the given descriptor, if one
//     * exists.
//     *
//     * @param descriptor search for the matching type
//     * @return the matching type, or null if not in the database.
//     */
//    QuantitationType getQuantitationType(QuantitationTypeDescriptor descriptor);
//    /**
//     * Returns the hybridization matching the given id.
//     *
//     * @param id id to retrieve
//     * @return the matching hybridization.
//     */
//    Hybridization getHybridization(Long id);
//
//    /**
//     * Returns true if an array design is associated with an existing experiment.
//     * The provider, type, and design file cannot be modified on a locked array
//     * design.
//     * @param id array design id
//     * @return true if the array design is locked
//     */
//    boolean isArrayDesignLocked(Long id);
//
//    /**
//     * Returns the design element list matching the LSID given.
//     *
//     * @param lsidAuthority the LSID authority
//     * @param lsidNamespace the LSID namespace
//     * @param lsidObjectId the LSID object ID
//     * @return the matching design list or null.
//     */
//    DesignElementList getDesignElementList(String lsidAuthority, String lsidNamespace, String lsidObjectId);
//
//	
//	
//	
//	
//	
//	
//	
//	
//	

}
