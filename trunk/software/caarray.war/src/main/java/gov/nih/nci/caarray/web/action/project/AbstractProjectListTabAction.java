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

import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.project.InconsistentProjectStateException;
import gov.nih.nci.caarray.application.project.ProposalWorkflowException;
import gov.nih.nci.caarray.domain.project.AbstractExperimentDesignNode;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.struts2.interceptor.validation.SkipValidation;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.web.displaytag.SortablePaginatedList;
import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;

/**
 * Base class for project tab actions that implement list-type tabs, ie Factors, Sources, etc,
 * that display a list of entities associated with project and allow you to add, edit, remove, copy them.
 *
 * @author Dan Kokotov
 */
public abstract class AbstractProjectListTabAction extends ProjectTabAction {
    private static final long serialVersionUID = 1L;

    /** page size for item lists. */
    public static final int PAGE_SIZE = 15;

    private final String resourceKey;
    private final SortablePaginatedList<? extends PersistentObject, ?> pagedItems;

    /**
     * @param resourceKey the resource to display
     * @param pagedItems the paged list to use for this tab's item list
     */
    public AbstractProjectListTabAction(String resourceKey,
            SortablePaginatedList<? extends PersistentObject, ?> pagedItems) {
        super();
        this.resourceKey = resourceKey;
        this.pagedItems = pagedItems;
    }

    /**
     * loads the tab with a list of the items.
     * {@inheritDoc}
     */
    @Override
    @SkipValidation
    public String load() {
        String checkResult = checkProject();
        if (checkResult != null) {
            return checkResult;
        }
        updatePagedList();
        return "list";
    }

    /**
     * Helper method that should be called to refresh the list of items according
     * to current paging parameters.
     */
    @SuppressWarnings("unchecked")
    protected void updatePagedList() {
        GenericDataService gds = ServiceLocatorFactory.getGenericDataService();
        this.pagedItems.setList(gds.pageCollection(getCollection(), this.pagedItems.getPageSortParams()));
        this.pagedItems.setFullListSize(gds.collectionSize(getCollection()));
    }

    /**
     * loads the tab with editing a single item.
     * @return input
     */
    @SkipValidation
    public String edit() {
        String checkResult = checkProject();
        if (checkResult != null) {
            return checkResult;
        }
        setEditMode(true);
        return INPUT;
    }

    /**
     * loads the tab with viewing a single item.
     * @return input
     */
    @SkipValidation
    public String view() {
        String checkResult = checkProject();
        if (checkResult != null) {
            return checkResult;
        }
        setEditMode(false);
        return INPUT;
    }

    /**
     * Saves the item.
     *
     * @return the string indicating the result to use.
     */
    @Override
    @SuppressWarnings("unchecked")
    public String save() {
        String checkResult = checkProject();
        if (checkResult != null) {
            return checkResult;
        }

        if (!checkBioMaterialName()) {
            return edit();
        }

        PersistentObject item = getItem();
        if (item.getId() == null) {
            getCollection().add(item);
            if (item instanceof AbstractBioMaterial) {
                AbstractBioMaterial abm = (AbstractBioMaterial) getItem();
                abm.setExperiment(getProject().getExperiment());
            }
            ActionHelper.saveMessage(getText("experiment.items.created", new String[] {getItemName()}));
        } else {
            ActionHelper.saveMessage(getText("experiment.items.updated", new String[] {getItemName()}));
        }
        String result = super.save();
        updatePagedList();
        return result;
    }

    private boolean checkBioMaterialName() {
        // validate biomaterial name
        if (getItem() instanceof AbstractExperimentDesignNode) {
            AbstractExperimentDesignNode bio = (AbstractExperimentDesignNode) getItem();
            for (Object obj : getCollection()) {
                    AbstractExperimentDesignNode abm = (AbstractExperimentDesignNode) obj;
                    if (bio.getName().equals(abm.getName())
                            && bio.getId() != abm.getId()) {
                        String errorField = "current" + getBioMaterialType(bio) + ".name";
                        List<Object> strList = new ArrayList<Object>();
                        strList.add(getBioMaterialType(bio));
                        strList.add(bio.getName());
                        addFieldError(errorField, getText("experiment.annotations.bioname.duplicate", strList));
                        return false;
                    }
            }
        }

        return true;
    }

    private String getBioMaterialType(AbstractExperimentDesignNode bio) {
        String className = bio.getClass().getName()
            .substring(bio.getClass().getName().lastIndexOf(".") + 1);
        return getText("experiment." + className.toLowerCase(Locale.getDefault()));
    }

    /**
     * Gets the label for the item to be used in success messages.
     * @return
     */
    private String getItemName() {
        return getText("experiment." + this.resourceKey);
    }

    /**
     * remove the item with the given id from the set of items.
     *
     * @return the string indicating which result to forward to.
     */
    @SkipValidation
    public String delete() {
        String checkResult = checkProject();
        if (checkResult != null) {
            return checkResult;
        }
        getCollection().remove(getItem());
        ActionHelper.saveMessage(getText("experiment.items.deleted", new String[] {getItemName()}));
        super.save();
        updatePagedList();
        return "list";
    }

    /**
     * Copy the item to a new object with a name obtained by adding a counter to the old name,
     * and return to the list screen.
     *
     * @return the string indicating the result to forward to.
     */
    @SkipValidation
    public String copy() {
        String checkResult = checkProject();
        if (checkResult != null) {
            return checkResult;
        }
        try {
            doCopyItem();
            ActionHelper.saveMessage(getText("experiment.items.copied", new String[] {getItemName()}));
            ActionHelper.saveMessage(getText("project.saved"));
        } catch (ProposalWorkflowException e) {
            handleWorkflowError();
        } catch (InconsistentProjectStateException e) {
            handleInconsistentStateError(e);
        }
        updatePagedList();
        return "list";
    }

    /**
     * return the project's collection of items to which new items should be added or from which items
     * should be removed.
     * DEVELOPER NOTE: this is intentionally ungenericized as there is no way to make this work with genericized
     * Collections due to limitations of generic type bounds in Java
     * @return project's collection of objects.
     */
    @SuppressWarnings("unchecked")
    protected abstract Collection getCollection();

    /**
     * @return  the List item currently being edited, for save/copy/remove methods
     */
    protected abstract PersistentObject getItem();

    /**
     * Subclasses should make the actual call to the appropriate service method to copy the item.
     * @throws ProposalWorkflowException when the experiment cannot be saved due to workflow restrictions
     * @throws InconsistentProjectStateException when the experiment cannot be saved due to inconsistent state
     */
    protected abstract void doCopyItem() throws ProposalWorkflowException, InconsistentProjectStateException;

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        super.validate();
        if (this.hasErrors()) {
            setEditMode(true);
        }
    }

    /**
     * @return the pagedItems
     */
    public SortablePaginatedList<?, ?> getPagedItems() {
        return pagedItems;
    }
}
