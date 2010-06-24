/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray Software License (the License) is between NCI and You. You (or
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
 * its rights in the caArray Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray Software; (ii) distribute and
 * have distributed to and by third parties the caArray Software and any
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
package gov.nih.nci.caarray.application.arraydesign;

import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.validation.InvalidDataFileException;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.util.List;
import java.util.Set;

/**
 * Provides array design detail parsing, storage, and retrieval functionality. Interface to the
 * ArrayDesign subsystem.
 */
public interface ArrayDesignService {
    /**
     * The default JNDI name to use to lookup <code>ProjectManagementService</code>.
     */
    String JNDI_NAME = "caarray/ArrayDesignServiceBean/local";

    /**
     * Validates an array design in preparation for save.
     *
     * @param design the design to validate
     * @return the validation result.
     */
    ValidationResult validateDesign(ArrayDesign design);

    /**
     * Validates a set of array design files.
     * 
     * @param designFiles the array design files to validate. These should be a set of files representing a single array
     *            design. Typically only a single file is expected, but some design formats (e.g. Affymetrix PGF/CLF)
     *            consist of multiple files
     * @return the validation result.
     */
    ValidationResult validateDesign(Set<CaArrayFile> designFiles);

    /**
     * Imports top-level attributes for an existing array design from the associated
     * <code>designFile</code>, validating the design file and updating attributes
     * if valid.
     *
     * @param arrayDesign the design to import details for.
     */
    void importDesign(ArrayDesign arrayDesign);

    /**
     * Imports details for an existing array design from associated files(s) including
     * <code>designFile</code> and <code>annotationFile</code>.
     *
     * @param arrayDesign the design to import details for.
     */
    void importDesignDetails(ArrayDesign arrayDesign);

    /**
     * @return the list of all Organizations that are array providers in the system.
     */
    List<Organization> getAllProviders();

    /**
     * Returns the list of ArrayDesigns with at least one of the given assay types and the provider.  Either the
     * provider or the list of assayTypes is required.  If the list of assay types is null or empty, the function
     * will return the list of array designs for the given provider.  If the provider is null, the function will
     * return the list of array designs associated with any of the assay types passed in.
     * @param provider the provider is optional if the list of assayTypes is provided
     * @param assayTypes the list of assay types is optional if provider is specified
     * @return the List&lt;ArrayDesign&gt; of the array designs with the given provider and assay type
     */
    List<ArrayDesign> getImportedArrayDesigns(Organization provider, Set<AssayType> assayTypes);

    /**
     * Returns the <code>ArrayDesign</code> with the id given.
     *
     * @param id get <code>ArrayDesign</code> matching this id
     * @return the <code>ArrayDesign</code>.
     */
    ArrayDesign getArrayDesign(Long id);

    /**
     * Returns the list of all ArrayDesigns.
     * @return the List&lt;ArrayDesign&gt; of array designs
     */
    List<ArrayDesign> getArrayDesigns();

    /**
     * Retrieves an <code>ArrayDesign</code> by LSID.
     *
     * @param lsidAuthority the LSID authority
     * @param lsidNamespace the LSID namespace
     * @param lsidObjectId the LSID object ID
     * @return the matching design or null.
     */
    ArrayDesign getArrayDesign(String lsidAuthority, String lsidNamespace, String lsidObjectId);

    /**
     * Returns true if an array design is locked, meaning that it is associated
     * with an existing experiment.
     * @param id the array design id
     * @return true if the array design is locked
     */
    boolean isArrayDesignLocked(Long id);

    /**
     * Saves an array design. May be called with either a new ArrayDesign not yet in the data store, or an already
     * existing design to be updated.
     * 
     * @param arrayDesign the array design to save
     * @return the persisted array design. This may not be the same object instance as passed in.
     * @throws IllegalAccessException if trying to modify locked fields on an array design or if the array design file
     *             is currently being imported
     * @throws InvalidDataFileException if array is duplicate
     */
    ArrayDesign saveArrayDesign(ArrayDesign arrayDesign) throws IllegalAccessException, InvalidDataFileException;

    /**
     * Checks for duplicate array design.
     *
     * @param arrayDesign the array design to check
     * @return duplicate
     */
    boolean isDuplicate(ArrayDesign arrayDesign);


    /**
     * Deletes an array design. Will not delete designs that are locked or in an
     * importing state.
     *
     * @param arrayDesign
     *            the array design to delete
     * @throws ArrayDesignDeleteException if the array design could not be deleted
     */
    void deleteArrayDesign(ArrayDesign arrayDesign) throws ArrayDesignDeleteException;

    /**
     * Retrieves an existing <code>DesignElementList</code> for an array design by LSID.
     *
     * @param lsidAuthority the LSID authority
     * @param lsidNamespace the LSID namespace
     * @param lsidObjectId the LSID object ID
     * @return the matching design element list or null.
     */
    DesignElementList getDesignElementList(String lsidAuthority, String lsidNamespace, String lsidObjectId);

    /**
     * Get array designs that have files that were uploaded but not parsed by earlier caArray versions,
     * but can now be parsed.
     * @return designs with unparsed - now parsable data file.
     * @since 2.4.0
     */
    List<ArrayDesign> getArrayDesignsWithReImportableFiles();

}
