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
package gov.nih.nci.caarray.web.action.protocol;

import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.domain.protocol.Protocol;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.security.PermissionDeniedException;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.CaArrayUsernameHolder;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.hibernate.criterion.Order;

import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.validator.annotations.CustomValidator;
import com.opensymphony.xwork2.validator.annotations.UrlValidator;
import com.opensymphony.xwork2.validator.annotations.ValidationParameter;
import com.opensymphony.xwork2.validator.annotations.Validations;

/**
 * @author Scott Miller
 */
public class ProtocolManagementAction extends ActionSupport implements Preparable {
    private static final String RETURN_INITIAL_TAB2_URL = "returnInitialTab2Url";
    private static final String RETURN_INITIAL_TAB2 = "returnInitialTab2";
    private static final String RETURN_INITIAL_TAB1 = "returnInitialTab1";
    private static final String RETURN_PROJECT_ID = "returnProjectId";
    private static final String START_WITH_EDIT = "startWithEdit";
    private static final long serialVersionUID = 1L;
    private List<Protocol> protocols;
    private Protocol protocol;
    private boolean createNewSource = false;
    private TermSource newSource;
    private List<TermSource> sources = new ArrayList<TermSource>();
    private boolean editMode = false;

    private boolean returnToProjectOnCompletion = false;
    private Long returnProjectId;
    private String returnInitialTab1 = "overview";
    private String returnInitialTab2;
    private String returnInitialTab2Url;

    /**
     * {@inheritDoc}
     */
    public void prepare() {
        if (getProtocol() != null && getProtocol().getId() != null) {
            Protocol retrieved = ServiceLocatorFactory.getGenericDataService().getPersistentObject(Protocol.class,
                    this.getProtocol().getId());
            if (retrieved == null) {
                throw new PermissionDeniedException(this.protocol,
                        SecurityUtils.PERMISSIONS_PRIVILEGE, CaArrayUsernameHolder.getUser());
            } else {
                this.setProtocol(retrieved);
            }
        }
        if (isCreateNewSource() && getProtocol() != null) {
            getProtocol().setSource(getNewSource());
        }
    }

    /**
     * load the protocol management ui.
     * @return the string indicating what result to follow.
     */
    @SkipValidation
    public String manage() {
        if (Boolean.valueOf(ServletActionContext.getRequest().getParameter(START_WITH_EDIT))) {
            HttpSession session = ServletActionContext.getRequest().getSession();
            session.setAttribute(START_WITH_EDIT, "true");
            session.setAttribute(RETURN_PROJECT_ID, getReturnProjectId());
            session.setAttribute(RETURN_INITIAL_TAB1, getReturnInitialTab1());
            session.setAttribute(RETURN_INITIAL_TAB2, getReturnInitialTab2());
            session.setAttribute(RETURN_INITIAL_TAB2_URL, getReturnInitialTab2Url());
        }
        return SUCCESS;
    }

    /**
     * load the protocol for editing, or a blank protocol for creation.
     * @return the string indicating what result to follow.
     */
    @SkipValidation
    public String edit() {
        setEditMode(true);
        setSources(ServiceLocatorFactory.getVocabularyService().getAllSources());
        return INPUT;
    }

    /**
     * Action for loading a protocol to view.
     * @return the string indicating which result to follow.
     */
    @SkipValidation
    public String details() {
        setEditMode(false);
        setSources(ServiceLocatorFactory.getVocabularyService().getAllSources());
        return INPUT;
    }

    /**
     * save the protocol.
     * @return the string indicating what result to follow.
     * @throws IllegalAccessException on error
     * @throws InstantiationException on error
     */
    @Validations(
        urls = {
            @UrlValidator(message = "", fieldName = "protocol.url", key = "struts.validator.url"),
            @UrlValidator(message = "", fieldName = "newSource.url", key = "struts.validator.url")
        }
    )
    public String save() throws InstantiationException, IllegalAccessException {
        ServiceLocatorFactory.getGenericDataService().save(getProtocol());
        ActionHelper.saveMessage(getText("protocol.saved", new String[] {getProtocol().getName() }));
        if (isReturnToProjectOnCompletion()) {
            return "projectEdit";
        }
        return list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        super.validate();
        if (!ActionHelper.isSkipValidationSetOnCurrentAction() && getProtocol() != null) {
            Protocol p = ServiceLocatorFactory.getVocabularyService().getProtocol(getProtocol().getName(),
                    getProtocol().getSource());
            if (p != null && !p.getId().equals(getProtocol().getId())) {
                addFieldError("protocol.name", getText("protocol.duplicate.name"));
            }
        }

        if (hasErrors()) {
            setSources(ServiceLocatorFactory.getVocabularyService().getAllSources());
        }
    }

    /**
     * handles the return to project / cancel action.
     * @return the string indicating which result to follow.
     */
    @SkipValidation
    public String projectEdit() {
        return "projectEdit";
    }

    /**
     * list the protocols.
     * @return the string indicating what result to follow.
     * @throws IllegalAccessException on error
     * @throws InstantiationException on error
     */
    @SkipValidation
    public String list() throws InstantiationException, IllegalAccessException {
        HttpSession session = ServletActionContext.getRequest().getSession();
        if (Boolean.valueOf((String) session.getAttribute(START_WITH_EDIT))) {
            setReturnProjectId((Long) session.getAttribute(RETURN_PROJECT_ID));
            setReturnInitialTab1((String) session.getAttribute(RETURN_INITIAL_TAB1));
            setReturnInitialTab2((String) session.getAttribute(RETURN_INITIAL_TAB2));
            setReturnInitialTab2Url((String) session.getAttribute(RETURN_INITIAL_TAB2_URL));
            setReturnToProjectOnCompletion(true);
            session.removeAttribute(START_WITH_EDIT);
            session.removeAttribute(RETURN_PROJECT_ID);
            session.removeAttribute(RETURN_INITIAL_TAB1);
            session.removeAttribute(RETURN_INITIAL_TAB2);
            session.removeAttribute(RETURN_INITIAL_TAB2_URL);
            return edit();
        }

        setProtocols(ServiceLocatorFactory.getGenericDataService().retrieveAll(Protocol.class, Order.asc("name")));
        return SUCCESS;
    }

    /**
     * @return the protocol
     */
    @CustomValidator(type = "hibernate")
    public Protocol getProtocol() {
        return this.protocol;
    }

    /**
     * @param protocol the protocol to set
     */
    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    /**
     * @return the protocols
     */
    public List<Protocol> getProtocols() {
        return this.protocols;
    }

    /**
     * @param protocols the protocols to set
     */
    public void setProtocols(List<Protocol> protocols) {
        this.protocols = protocols;
    }

    /**
     * @return the editMode
     */
    public boolean isEditMode() {
        return this.editMode;
    }

    /**
     * @param editMode the editMode to set
     */
    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    /**
     * @return the returnToProjectOnCompletion
     */
    public boolean isReturnToProjectOnCompletion() {
        return this.returnToProjectOnCompletion;
    }

    /**
     * @param returnToProjectOnCompletion the returnToProjectOnCompletion to set
     */
    public void setReturnToProjectOnCompletion(boolean returnToProjectOnCompletion) {
        this.returnToProjectOnCompletion = returnToProjectOnCompletion;
    }

    /**
     * @return the returnInitialTab1
     */
    public String getReturnInitialTab1() {
        return this.returnInitialTab1;
    }

    /**
     * @param returnInitialTab1 the returnInitialTab1 to set
     */
    public void setReturnInitialTab1(String returnInitialTab1) {
        this.returnInitialTab1 = returnInitialTab1;
    }

    /**
     * @return the returnInitialTab2
     */
    public String getReturnInitialTab2() {
        return this.returnInitialTab2;
    }

    /**
     * @param returnInitialTab2 the returnInitialTab2 to set
     */
    public void setReturnInitialTab2(String returnInitialTab2) {
        this.returnInitialTab2 = returnInitialTab2;
    }

    /**
     * @return the returnInitialTab2Url
     */
    public String getReturnInitialTab2Url() {
        return this.returnInitialTab2Url;
    }

    /**
     * @param returnInitialTab2Url the returnInitialTab2Url to set
     */
    public void setReturnInitialTab2Url(String returnInitialTab2Url) {
        this.returnInitialTab2Url = returnInitialTab2Url;
    }

    /**
     * @return the returnProjectId
     */
    public Long getReturnProjectId() {
        return this.returnProjectId;
    }

    /**
     * @param returnProjectId the returnProjectId to set
     */
    public void setReturnProjectId(Long returnProjectId) {
        this.returnProjectId = returnProjectId;
    }

    /**
     * @return the newSource
     */
    @CustomValidator(type = "hibernate",
            parameters = @ValidationParameter(name = "conditionalExpression", value = "createNewSource == true"))
    public TermSource getNewSource() {
        return this.newSource;
    }

    /**
     * @param newSource the newSource to set
     */
    public void setNewSource(TermSource newSource) {
        this.newSource = newSource;
    }

    /**
     * @return the sources
     */
    public List<TermSource> getSources() {
        return this.sources;
    }

    /**
     * @param sources the sources to set
     */
    public void setSources(List<TermSource> sources) {
        this.sources = sources;
    }

    /**
     * @return the createNewSource
     */
    public boolean isCreateNewSource() {
        return this.createNewSource;
    }

    /**
     * @param createNewSource the createNewSource to set
     */
    public void setCreateNewSource(boolean createNewSource) {
        this.createNewSource = createNewSource;
    }
}
