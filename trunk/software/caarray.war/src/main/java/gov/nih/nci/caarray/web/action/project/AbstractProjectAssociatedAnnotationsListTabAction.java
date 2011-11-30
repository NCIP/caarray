/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-war
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-war Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-war Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-war Software; (ii) distribute and
 * have distributed to and by third parties the caarray-war Software and any
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
package gov.nih.nci.caarray.web.action.project;

import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;

import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.web.displaytag.SortablePaginatedList;
import com.opensymphony.xwork2.validator.annotations.FieldExpressionValidator;

/**
 * Class that manages the annotation tabs.
 * @author Scott Miller
 * @param <T> the class of the annotations are being managed.
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public abstract class AbstractProjectAssociatedAnnotationsListTabAction<T extends AbstractBioMaterial> extends
        AbstractProjectProtocolAnnotationListTabAction {
    private static final long serialVersionUID = 1L;
    
    private final String associatedResourceKey;
    private String associatedValueName;
    private Collection<T> unassociatedValues;

    /**
     * default constructor.
     * @param resourceKey the base resource key.
     * @param associatedResourceKey the resource key for the associated annotation
     * @param pagedItems the paged list to use for this tab's item list
     */
    public AbstractProjectAssociatedAnnotationsListTabAction(String resourceKey, String associatedResourceKey,
            SortablePaginatedList<? extends PersistentObject, ?> pagedItems) {
        super(resourceKey, pagedItems);
        this.associatedResourceKey = associatedResourceKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    @FieldExpressionValidator(fieldName = "associatedValueName",
            key = "experiment.annotations.associationRequired", message = "",
            expression = "currentAssociationsCollection.size() - itemsToRemove.size() + itemsToAssociate.size() > 0")
    public String save() {
        // ideally this logic, along with the itemsToAssociate collection would be in the base class, but the
        // struts 2 type converter for persistent entity wasn't liking the generic collection.
        getCurrentAssociationsCollection().removeAll(getItemsToRemove());
        for (T item : getItemsToRemove()) {
            getAnnotationCollectionToUpdate(item).remove(getItem());
        }

        getCurrentAssociationsCollection().addAll(getItemsToAssociate());
        for (T item : getItemsToAssociate()) {
            getAnnotationCollectionToUpdate(item).add(getItem());
        }
        return super.save();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SkipValidation
    public String delete() {
        addOrphan(getItem());
        boolean doDelete = handleDelete();
        if (doDelete) {
            return super.delete();
        }
        updatePagedList();
        return "list";
    }

    /**
     * Action to search for associated annotations.
     * @return the string matching the result to follow
     */
    @SkipValidation
    public String searchForAssociationValues() {
        if (getAssociatedValueName() ==  null) {
            setAssociatedValueName("");
        }
        Collection<T> possibleValues = ServiceLocatorFactory.getGenericDataService().filterCollection(
                getPossibleAssociationsCollection(), "name", getAssociatedValueName());
        setUnassociatedValues(possibleValues);
        return "associationValues";
    }

    /**
     * Gets the set of initialSavedAssociations.
     * @return the set of items.
     */
    @SuppressWarnings("unchecked")
    public Collection getInitialSavedAssociations() {
        return CollectionUtils.subtract(getCurrentAssociationsCollection(), getItemsToRemove());
    }

    /**
     * does nothing, here to conform to java bean rules for the jsp.
     * @param c the param that will be ignored
     */
    @SuppressWarnings("unchecked")
    public void setInitialSavedAssociations(Collection c) {
        // here to conform to java bean rules for jsp
    }

    /**
     * Return the collection to filter when searching for associated values.
     * @return the collection to filter
     */
    protected abstract Collection<T> getPossibleAssociationsCollection();

    /**
     * Retrieve the collection that contains the current Associations.
     * @return the collection of the current associations.
     */
    public abstract Collection<T> getCurrentAssociationsCollection();

    /**
     * Method to get the collection of annotations on the association to update when changes are persisted.
     * @param item the item to retrieve the collection from.
     * @return the collection to update
     */
    @SuppressWarnings("unchecked")
    public abstract Collection getAnnotationCollectionToUpdate(T item);

    /**
     * Handles deletion of the current item.  Implementations should remove the current T
     * from the left association class and perform any other model cleanup necessary for the
     * delete to succeed.
     *
     * @return whether to proceed with the delete.  If false, will not delete.  Put a message
     *         into scope explaining why.
     */
    protected abstract boolean handleDelete();

    /**
     * @return the itemsToAssociate
     */
    public abstract List<T> getItemsToAssociate();

    /**
     * @return the itemsToRemove
     */
    public abstract List<T> getItemsToRemove();


    /**
     * @return the associatedValueName
     */
    public String getAssociatedValueName() {
        return this.associatedValueName;
    }

    /**
     * @param associatedValueName the associatedValueName to set
     */
    public void setAssociatedValueName(String associatedValueName) {
        this.associatedValueName = associatedValueName;
    }

    /**
     * @return the unassociatedValues
     */
    public Collection<T> getUnassociatedValues() {
        return this.unassociatedValues;
    }

    /**
     * @param unassociatedValues the unassociatedValues to set
     */
    public void setUnassociatedValues(Collection<T> unassociatedValues) {
        this.unassociatedValues = unassociatedValues;
    }

    /**
     * @return the resource key suffix for the name of the associated annotation for
     * the annotation this action is serving (the to the left of this one, ie Source for Sample).
     * The full resource key is expected to be <code>'experiment.' + this.getAssociatedResourceKey()</code>
     */
    public String getAssociatedResourceKey() {
        return associatedResourceKey;
    }
}
