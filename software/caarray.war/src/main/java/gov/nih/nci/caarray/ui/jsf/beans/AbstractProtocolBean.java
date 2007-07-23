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
package gov.nih.nci.caarray.ui.jsf.beans;

import gov.nih.nci.caarray.application.protocol.ProtocolService;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.domain.protocol.Protocol;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.util.j2ee.ServiceLocator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;

/**
 * Base class for backing beans that need to use protocol information.
 *
 * @author tavelae
 */
public abstract class AbstractProtocolBean implements Serializable {

    private static final long serialVersionUID = -7548806609762035864L;

    private Protocol protocol;
    private List<Term> protocolTypes;

    @EJB
    private VocabularyService vocabularyService;

    @EJB
    private ProtocolService protocolService;

    /**
     * Creates a new instance.
     */
    AbstractProtocolBean() {
        super();
    }

    /**
     * Returns the list of protocol type choices.
     *
     * @return the protocol types.
     */
    public final List<SelectItem> getProtocolTypeItems() {
        if (getLog().isTraceEnabled()) {
            getLog().trace("getProtocolTypes()");
        }
        List<SelectItem> protocolTypeItems = new ArrayList<SelectItem>(getProtocolTypes().size());
        for (int i = 0; i < getProtocolTypes().size(); i++) {
            Term type = getProtocolTypes().get(i);
            protocolTypeItems.add(new SelectItem(type.getId(), type.getValue()));
        }
        return protocolTypeItems;
    }

    abstract Log getLog();

    private List<Term> getProtocolTypes() {
        if (protocolTypes == null) {
            try {
                protocolTypes = getVocabularyService().getTerms("ProtocolType");
            } catch (Exception e) {
                getLog().error("Couldn't retrieve ProtocolTypes", e);
                return null;
            }
        }
        return protocolTypes;
    }

    /**
     * Returns the current protocol for editing or viewing.
     *
     * @return the protocol
     */
    public final Protocol getProtocol() {
        return protocol;
    }

    /**
     * Sets the protocol to be edited.
     *
     * @param protocol the protocol to set
     */
    public final void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    private VocabularyService getVocabularyService() {
        if (vocabularyService == null) {
            vocabularyService = (VocabularyService) ServiceLocator.getInstance().lookup(VocabularyService.JNDI_NAME);
        }
        return vocabularyService;
    }

    final void setVocabularyService(VocabularyService vocabularyService) {
        this.vocabularyService = vocabularyService;
    }

    /**
     * Returns the id of the protocol's type.
     *
     * @return the protocolTypeId
     */
    public final Long getProtocolTypeId() {
        if (getLog().isDebugEnabled()) {
            getLog().debug("getProtocolTypeId()");
        }
        if (getProtocol().getType() != null) {
            return getProtocol().getType().getId();
        } else {
            return null;
        }
    }

    /**
     * Sets the protocol type by id.
     *
     * @param protocolTypeId the protocolTypeId to set
     */
    public final void setProtocolTypeId(Long protocolTypeId) {
        if (getLog().isDebugEnabled()) {
            getLog().debug("setProtocolTypeId(" + protocolTypeId + ")");
        }
        getProtocol().setType(getProtocolType(protocolTypeId));
    }

    private Term getProtocolType(Long protocolTypeId) {
        for (Term type : getProtocolTypes()) {
            if (type.getId().equals(protocolTypeId)) {
                return type;
            }
        }
        return null;
    }

    final ProtocolService getProtocolService() {
        if (protocolService == null) {
            protocolService = (ProtocolService) ServiceLocator.getInstance().lookup(ProtocolService.JNDI_NAME);
        }
        return protocolService;
    }

}
