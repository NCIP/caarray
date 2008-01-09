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

import gov.nih.nci.caarray.domain.contact.AbstractContact;
import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.project.ExperimentContact;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.web.action.ActionHelper;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.validator.annotations.CustomValidator;
import com.opensymphony.xwork2.validator.annotations.FieldExpressionValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.ValidationParameter;
import com.opensymphony.xwork2.validator.annotations.Validations;

/**
 * Action for Contacts tab of Project management.
 * @author Dan Kokotov
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class ProjectContactsAction extends ProjectTabAction {
    private static final long serialVersionUID = 1L;

    private static final String REQUIRED_STRING_KEY = "struts.validator.requiredString";

    private User user;
    private boolean piIsMainPoc;
    private Person primaryInvestigator;
    private Person mainPointOfContact;

    /**
     * load a given tab in the submit experiment workflow.
     *
     * @return name of result to forward to
     */
    @Override
    @SkipValidation
    public String load() {
        setup();
        return super.load();
    }

    /**
     * setup contacts tab.
     */
    public void setup() {
        this.user = UsernameHolder.getCsmUser();

        ExperimentContact pi = getProject().getExperiment().getPrimaryInvestigator();
        if (pi != null) {
            this.primaryInvestigator = (Person) pi.getContact();
            if (pi.isMainPointOfContact()) {
                this.piIsMainPoc = true;
                this.mainPointOfContact = new Person();
            } else {
                this.piIsMainPoc = false;
                ExperimentContact mainPoc = getProject().getExperiment().getMainPointOfContact();
                if (mainPoc != null) {
                    this.mainPointOfContact = (Person) mainPoc.getContact();
                } else {
                    this.mainPointOfContact = new Person();
                }
            }
        } else {
            this.primaryInvestigator = new Person(this.user);
            this.piIsMainPoc = true;
            this.mainPointOfContact = new Person();
        }
    }

    /**
     * save a project.
     *
     * @return path String
     */
    @Override
    @SuppressWarnings("PMD")
    @Validations(
        requiredStrings = {
            @RequiredStringValidator(fieldName = "primaryInvestigator.firstName", key = REQUIRED_STRING_KEY, 
                    message = ""),
            @RequiredStringValidator(fieldName = "primaryInvestigator.lastName", key = REQUIRED_STRING_KEY, 
                    message = ""),
            @RequiredStringValidator(fieldName = "primaryInvestigator.email", key = REQUIRED_STRING_KEY, 
                    message = "") }, 
        fieldExpressions = {
            @FieldExpressionValidator(expression = "piIsMainPoc || mainPointOfContact.firstName != null", 
                    fieldName = "mainPointOfContact.firstName", key = REQUIRED_STRING_KEY, message = ""),
            @FieldExpressionValidator(expression = "piIsMainPoc || mainPointOfContact.lastName != null", 
                    fieldName = "mainPointOfContact.lastName", key = REQUIRED_STRING_KEY, message = ""),
            @FieldExpressionValidator(expression = "piIsMainPoc || mainPointOfContact.email != null", 
                    fieldName = "mainPointOfContact.email", key = REQUIRED_STRING_KEY, message = "")
        }
    )
    public String save() {
        Term piRole = ActionHelper.getMOTerm(ExperimentContact.PI_ROLE);
        Term mainPocRole = ActionHelper.getMOTerm(ExperimentContact.MAIN_POC_ROLE);

        ExperimentContact pi = getProject().getExperiment().getPrimaryInvestigator();
        if (pi != null) {
            copyContact(this.primaryInvestigator, pi.getContact());
            if (this.piIsMainPoc && !pi.isMainPointOfContact()) {
                getProject().getExperiment().removeSeparateMainPointOfContact();
                pi.getRoles().add(mainPocRole);
            }
            if (!this.piIsMainPoc) {
                ExperimentContact mainPoc = getProject().getExperiment().getMainPointOfContact();
                if (pi.isMainPointOfContact()) {
                    pi.removeMainPointOfContactRole();
                    mainPoc = new ExperimentContact(getExperiment(), new Person(), mainPocRole);
                    getProject().getExperiment().getExperimentContacts().add(1, mainPoc); 
                }
                copyContact(this.mainPointOfContact, mainPoc.getContact());
            }
        } else {
            pi = new ExperimentContact(getExperiment(), this.primaryInvestigator, piRole);
            getProject().getExperiment().getExperimentContacts().add(pi);
            if (this.piIsMainPoc) {
                pi.getRoles().add(mainPocRole);
            } else {
                ExperimentContact mainPoc = new ExperimentContact(getExperiment(),
                                                                  this.mainPointOfContact,
                                                                  mainPocRole);
                getProject().getExperiment().getExperimentContacts().add(mainPoc);
            }
        }

        String result = super.save();
        setup();
        return result;
    }

    /**
     * Helper method to copy the properties of one contact to another. The id property is not copied
     *
     * @param source contact to copy from
     * @param dest contact to copy to
     */
    @SuppressWarnings("deprecation")
    private void copyContact(AbstractContact source, AbstractContact dest) {
        Long id = dest.getId();
        try {
            PropertyUtils.copyProperties(dest, source);
        } catch (IllegalAccessException e) {
            // cannot happen
            LOG.fatal(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            // cannot happen
            LOG.fatal(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            // cannot happen
            LOG.fatal(e.getMessage(), e);
        }
        dest.setId(id);
    }

    /**
     * @return the user
     */
    public User getUser() {
        return this.user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the piIsMainPoc
     */
    public boolean isPiIsMainPoc() {
        return this.piIsMainPoc;
    }

    /**
     * @param piIsMainPoc the piIsMainPoc to set
     */
    public void setPiIsMainPoc(boolean piIsMainPoc) {
        this.piIsMainPoc = piIsMainPoc;
    }

    /**
     * @return the primaryInvestigator
     */
    @CustomValidator(type = "hibernate", parameters = @ValidationParameter(name = "conditionalExpression",
                    value = "primaryInvestigator.email != null"))
    public Person getPrimaryInvestigator() {
        return this.primaryInvestigator;
    }

    /**
     * @param primaryInvestigator the primaryInvestigator to set
     */
    public void setPrimaryInvestigator(Person primaryInvestigator) {
        this.primaryInvestigator = primaryInvestigator;
    }

    /**
     * @return the mainPointOfContact
     */
    @CustomValidator(type = "hibernate", parameters = @ValidationParameter(name = "conditionalExpression",
                    value = "mainPointOfContact.email != null && !piIsMainPoc"))
    public Person getMainPointOfContact() {
        return this.mainPointOfContact;
    }

    /**
     * @param mainPointOfContact the mainPointOfContact to set
     */
    public void setMainPointOfContact(Person mainPointOfContact) {
        this.mainPointOfContact = mainPointOfContact;
    }
}
