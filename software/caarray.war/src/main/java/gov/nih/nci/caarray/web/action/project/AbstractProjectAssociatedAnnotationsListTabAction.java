//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
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
