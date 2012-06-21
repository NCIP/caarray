/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-common.jar
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-common.jar Software License (the License) is between NCI and You. You (or
 * Your) shall mean a person or an entity, and all other entities that control,
 * are controlled by, or are under common control with the entity. Control for
 * purposes of this definition means (i) the direct or indirect power to cause
 * the direction or management of such entity, whether by contract or otherwise,
 * or (ii) ownership of fifty percent (50%) or more of the outstanding shares,
 * or (iii) beneficial ownership of such entity.
 *
 * This License is granted provided that You agree to the conditions described
 * below. NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up,
 * no-charge, irrevocable, transferable and royalty-free right and license in
 * its rights in the caarray-common.jar Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-common.jar Software; (ii) distribute and
 * have distributed to and by third parties the caarray-common.jar Software and any
 * modifications and derivative works thereof; and (iii) sublicense the
 * foregoing rights set out in (i) and (ii) to third parties, including the
 * right to license such rights to further third parties. For sake of clarity,
 * and not by way of limitation, NCI shall have no right of accounting or right
 * of payment from You or Your sub-licensees for the rights granted under this
 * License. This License is granted at no charge to You.
 *
 * Your redistributions of the source code for the Software must retain the
 * above copyright notice, this list of conditions and the disclaimer and
 * limitation of liability of Article 6, below. Your redistributions in object
 * code form must reproduce the above copyright notice, this list of conditions
 * and the disclaimer of Article 6 in the documentation and/or other materials
 * provided with the distribution, if any.
 *
 * Your end-user documentation included with the redistribution, if any, must
 * include the following acknowledgment: This product includes software
 * developed by 5AM and the National Cancer Institute. If You do not include
 * such end-user documentation, You shall include this acknowledgment in the
 * Software itself, wherever such third-party acknowledgments normally appear.
 *
 * You may not use the names "The National Cancer Institute", "NCI", or "5AM"
 * to endorse or promote products derived from this Software. This License does
 * not authorize You to use any trademarks, service marks, trade names, logos or
 * product names of either NCI or 5AM, except as required to comply with the
 * terms of this License.
 *
 * For sake of clarity, and not by way of limitation, You may incorporate this
 * Software into Your proprietary programs and into any third party proprietary
 * programs. However, if You incorporate the Software into third party
 * proprietary programs, You agree that You are solely responsible for obtaining
 * any permission from such third parties required to incorporate the Software
 * into such third party proprietary programs and for informing Your
 * sub-licensees, including without limitation Your end-users, of their
 * obligation to secure any required permissions from such third parties before
 * incorporating the Software into such third party proprietary software
 * programs. In the event that You fail to obtain such permissions, You agree
 * to indemnify NCI for any claims against NCI by such third parties, except to
 * the extent prohibited by law, resulting from Your failure to obtain such
 * permissions.
 *
 * For sake of clarity, and not by way of limitation, You may add Your own
 * copyright statement to Your modifications and to the derivative works, and
 * You may provide additional or different license terms and conditions in Your
 * sublicenses of modifications of the Software, or any derivative works of the
 * Software as a whole, provided Your use, reproduction, and distribution of the
 * Work otherwise complies with the conditions stated in this License.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, 5AM SOLUTIONS, INC. OR THEIR
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.LogicalProbe;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.ArrayDataType;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.search.QuantitationTypeSearchCriteria;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 * DAO for entities in the <code>gov.nih.nci.caarray.domain.array</code> package.
 * 
 * @author Rashmi Srinivasa
 */
public interface ArrayDao extends CaArrayDao {
    /**
     * Returns the <code>ArrayDesign</code> with the id given.
     * 
     * @param id get <code>ArrayDesign</code> matching this id
     * @return the <code>ArrayDesign</code>.
     */
    ArrayDesign getArrayDesign(long id);

    /**
     * Returns the entity matching the LSID given.
     * 
     * @param lsidAuthority the LSID authority
     * @param lsidNamespace the LSID namespace
     * @param lsidObjectId the LSID object ID
     * @return the matching design or null.
     */
    ArrayDesign getArrayDesign(String lsidAuthority, String lsidNamespace, String lsidObjectId);

    /**
     * Returns the list of ArrayDesigns with at least one of the given assay types and the provider. Either the provider
     * or the list of assayTypes is required. If the list of assay types is null or empty, the function will return the
     * list of array designs for the given provider. If the provider is null, the function will return the list of array
     * designs associated with any of the assay types passed in.
     * 
     * @param provider the provider is optional if the list of assayTypes is provided
     * @param assayTypes the list of assay types is optional if provider is specified
     * @param importedOnly whether to only return ArrayDesigns which have finished importing (ie whose corresponding
     *            design file has a status of IMPORTED). if true only those array designs are returned, otherwise, all
     *            array designs are.
     * @return the List&lt;ArrayDesign&gt; of the array designs with the given provider and assay type
     */
    List<ArrayDesign> getArrayDesigns(Organization provider, Set<AssayType> assayTypes, boolean importedOnly);

    /**
     * Returns the array data type corresponding to the given descriptor, if one exists.
     * 
     * @param descriptor search for the matching type
     * @return the matching type, or null if not in the database.
     */
    ArrayDataType getArrayDataType(ArrayDataTypeDescriptor descriptor);

    /**
     * Returns the array data object for the file with given id.
     * 
     * @param fileId id of file for which to retrieve data
     * @return the associated data.
     */
    AbstractArrayData getArrayData(Long fileId);

    /**
     * Returns the quantitation type corresponding to the given descriptor, if one exists.
     * 
     * @param descriptor search for the matching type
     * @return the matching type, or null if not in the database.
     */
    QuantitationType getQuantitationType(QuantitationTypeDescriptor descriptor);

    /**
     * Returns true if an array design is associated with an existing experiment. The provider, type, and design file
     * cannot be modified on a locked array design.
     * 
     * @param id array design id
     * @return true if the array design is locked
     */
    boolean isArrayDesignLocked(Long id);

    /**
     * Returns the design element list matching the LSID given.
     * 
     * @param lsidAuthority the LSID authority
     * @param lsidNamespace the LSID namespace
     * @param lsidObjectId the LSID object ID
     * @return the matching design list or null.
     */
    DesignElementList getDesignElementList(String lsidAuthority, String lsidNamespace, String lsidObjectId);

    /**
     * Return a mapping of logical probe names to ids for the given list of probe names.
     * 
     * @param design probes must belong to this design
     * @param names the names of the probes for which to get ids
     * @return the Map&lt;String, Long> of probe names to ids for the given names
     */
    Map<String, Long> getLogicalProbeNamesToIds(ArrayDesign design, List<String> names);

    /**
     * Returns a list of IDs of logical probes in a given array design, in batches.
     * 
     * @param design probes must belong to this design
     * @param params paging parameters (sorting parameters are ignored)
     * @return a list of matching IDs
     */
    List<Long> getLogicalProbeIds(ArrayDesign design, PageSortParams<LogicalProbe> params);

    /**
     * Select named Physical Probes from a array design, if found.
     * 
     * @param design owner design.
     * @param names the names of probes of interest.
     * @return a potentially incomplete list of the probes of interest.
     */
    List<PhysicalProbe> getPhysicalProbeByNames(ArrayDesign design, List<String> names);

    /**
     * Returns a set of names of physical probes in a given array design.
     * @param design probes must belong to this design
     * @return a set of probe names
     */
    Set<String> getPhysicalProbeNames(ArrayDesign design);

    /**
     * Save a batch of design element entries in a design element list. The entries are put in the list starting at a
     * given index.
     * 
     * @param designElementList the design element list to which the entries belong (must already be persistent)
     * @param startIndex the starting index in the list at which the entries should be saved
     * @param logicalProbeIds the ids of the design elements which should be added to the list starting at given index
     */
    void
            createDesignElementListEntries(DesignElementList designElementList, int startIndex,
                    List<Long> logicalProbeIds);

    /**
     * Delete array design detail.
     * 
     * @param design array design associated with the arrayDesignDetail that will be deleted.
     */
    void deleteArrayDesignDetails(ArrayDesign design);

    /**
     * Create all features for an array design, rows * cols total features will be created. It is assumed that there is
     * only one block.
     * 
     * @param rows number of rows in the design
     * @param cols number of columns in the design
     * @param designDetails array design details in which to create the features
     */
    void createFeatures(int rows, int cols, ArrayDesignDetails designDetails);

    /**
     * Get the ID of the first feature associated with the given design details (the lowest ID number).
     * 
     * @param designDetails array design details to find features for
     * @return lowest ID associated with the design details
     */
    Long getFirstFeatureId(ArrayDesignDetails designDetails);

    /**
     * Gets the array designs associated with an ArrayDesignDetails object. Most array design details are only
     * associated with one array design, but some (such as those imported from PGF/CLF files) are associated with
     * several.
     * 
     * @param arrayDesignDetails the array design details by which to look up array designs
     * @return the ArrayDesigns associated with the given details
     */
    List<ArrayDesign> getArrayDesigns(ArrayDesignDetails arrayDesignDetails);

    /**
     * Performs a query for quantitation types by the given criteria.
     * 
     * @param params paging and sorting parameters
     * @param criteria the criteria for the search
     * @return a list of matching quantitation types
     */
    List<QuantitationType> searchForQuantitationTypes(PageSortParams<QuantitationType> params,
            QuantitationTypeSearchCriteria criteria);

    /**
     * Get array designs that have files that were uploaded but not parsed by earlier caArray versions, but can now be
     * parsed.
     * 
     * @return designs with unparsed - now parsable data file.
     * @since 2.4.0
     */
    List<ArrayDesign> getArrayDesignsWithReImportable();

    /**
     * Get the data handles for all parsed data in the persistent store.
     * 
     * @return a List of URIs for the data handles corresponding to the data underlying all AbstractDataColumns in the
     *         system
     */
    List<URI> getAllParsedDataHandles();
}
