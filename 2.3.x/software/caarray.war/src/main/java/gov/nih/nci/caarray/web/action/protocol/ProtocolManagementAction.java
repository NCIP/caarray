//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action.protocol;

import gov.nih.nci.caarray.domain.protocol.Protocol;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.security.PermissionDeniedException;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.web.action.CaArrayActionHelper;

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
            Protocol retrieved = CaArrayActionHelper.getGenericDataService().getPersistentObject(Protocol.class,
                    this.getProtocol().getId());
            if (retrieved == null) {
                throw new PermissionDeniedException(this.protocol,
                        SecurityUtils.PERMISSIONS_PRIVILEGE, UsernameHolder.getUser());
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
        setSources(CaArrayActionHelper.getVocabularyService().getAllSources());
        return INPUT;
    }

    /**
     * Action for loading a protocol to view.
     * @return the string indicating which result to follow.
     */
    @SkipValidation
    public String details() {
        setEditMode(false);
        setSources(CaArrayActionHelper.getVocabularyService().getAllSources());
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
        CaArrayActionHelper.getGenericDataService().save(getProtocol());
        List<String> args = new ArrayList<String>();
        args.add(getProtocol().getName());
        ActionHelper.saveMessage(getText("protocol.saved", args));
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
            Protocol p = CaArrayActionHelper.getVocabularyService().getProtocol(getProtocol().getName(),
                    getProtocol().getSource());
            if (p != null && !p.getId().equals(getProtocol().getId())) {
                addFieldError("protocol.name", getText("protocol.duplicate.name"));
            }
        }

        if (hasErrors()) {
            setSources(CaArrayActionHelper.getVocabularyService().getAllSources());
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

        setProtocols(CaArrayActionHelper.getGenericDataService().retrieveAll(Protocol.class, Order.asc("name")));
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
