/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray2
 * Software was developed in conjunction with the National Cancer Institute 
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent 
 * government employees are authors, any rights in such works shall be subject 
 * to Title 17 of the United States Code, section 105. 
 *
 * This caArray2 Software License (the License) is between NCI and You. You (or 
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
 * its rights in the caArray2 Software to (i) use, install, access, operate, 
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray2 Software; (ii) distribute and 
 * have distributed to and by third parties the caArray2 Software and any 
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
package gov.nih.nci.caarray.services.external.v1_0.grid.service;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.array.ArrayDesign;
import gov.nih.nci.caarray.external.v1_0.array.ArrayProvider;
import gov.nih.nci.caarray.external.v1_0.array.AssayType;
import gov.nih.nci.caarray.external.v1_0.data.ArrayDataType;
import gov.nih.nci.caarray.external.v1_0.data.File;
import gov.nih.nci.caarray.external.v1_0.data.FileType;
import gov.nih.nci.caarray.external.v1_0.data.QuantitationType;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.experiment.ExperimentalContact;
import gov.nih.nci.caarray.external.v1_0.experiment.Organism;
import gov.nih.nci.caarray.external.v1_0.experiment.Person;
import gov.nih.nci.caarray.external.v1_0.factor.Factor;
import gov.nih.nci.caarray.external.v1_0.sample.Biomaterial;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Category;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Term;
import gov.nih.nci.caarray.external.v1_0.vocabulary.TermSource;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchResultIterator;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;

import org.globus.ws.enumeration.EnumIterator;
import org.globus.ws.enumeration.IterationConstraints;
import org.globus.ws.enumeration.IterationResult;
import org.globus.ws.enumeration.TimeoutException;
import org.globus.wsrf.encoding.ObjectSerializer;

/**
 * @author dkokotov
 *
 */
public class SearchEnumIterator<T extends AbstractCaArrayEntity> implements EnumIterator {
    private static final Map<Class<?>, QName> CLASS_TO_QNAME = new HashMap<Class<?>, QName>();
    static {
        CLASS_TO_QNAME.put(Experiment.class, new QName(
                "gme://External.caArray.caBIG/1.0/gov.nih.nci.caarray.external.experiment", "Experiment"));
        CLASS_TO_QNAME.put(Organism.class, new QName(
                "gme://External.caArray.caBIG/1.0/gov.nih.nci.caarray.external.experiment", "Organism"));
        CLASS_TO_QNAME.put(Person.class, new QName(
                "gme://External.caArray.caBIG/1.0/gov.nih.nci.caarray.external.experiment", "Person"));
        CLASS_TO_QNAME.put(ExperimentalContact.class, new QName(
                "gme://External.caArray.caBIG/1.0/gov.nih.nci.caarray.external.experiment", "ExperimentalContact"));
        CLASS_TO_QNAME.put(Term.class, new QName(
                "gme://External.caArray.caBIG/1.0/gov.nih.nci.caarray.external.vocabulary", "Term"));
        CLASS_TO_QNAME.put(Category.class, new QName(
                "gme://External.caArray.caBIG/1.0/gov.nih.nci.caarray.external.vocabulary", "Category"));
        CLASS_TO_QNAME.put(TermSource.class, new QName(
                "gme://External.caArray.caBIG/1.0/gov.nih.nci.caarray.external.vocabulary", "TermSource"));
        CLASS_TO_QNAME.put(Factor.class, new QName(
                "gme://External.caArray.caBIG/1.0/gov.nih.nci.caarray.external.factor", "Factor"));
        CLASS_TO_QNAME.put(Biomaterial.class, new QName(
                "gme://External.caArray.caBIG/1.0/gov.nih.nci.caarray.external.sample", "Biomaterial"));
        CLASS_TO_QNAME.put(Hybridization.class, new QName(
                "gme://External.caArray.caBIG/1.0/gov.nih.nci.caarray.external.sample", "Hybridization"));
        CLASS_TO_QNAME.put(File.class, new QName(
                "gme://External.caArray.caBIG/1.0/gov.nih.nci.caarray.external.data", "File"));
        CLASS_TO_QNAME.put(FileType.class, new QName(
                "gme://External.caArray.caBIG/1.0/gov.nih.nci.caarray.external.data", "FileType"));
        CLASS_TO_QNAME.put(QuantitationType.class, new QName(
                "gme://External.caArray.caBIG/1.0/gov.nih.nci.caarray.external.data", "QuantitationType"));
        CLASS_TO_QNAME.put(ArrayDataType.class, new QName(
                "gme://External.caArray.caBIG/1.0/gov.nih.nci.caarray.external.data", "ArrayDataType"));
        CLASS_TO_QNAME.put(ArrayProvider.class, new QName(
                "gme://External.caArray.caBIG/1.0/gov.nih.nci.caarray.external.array", "ArrayProvider"));
        CLASS_TO_QNAME.put(ArrayDesign.class, new QName(
                "gme://External.caArray.caBIG/1.0/gov.nih.nci.caarray.external.array", "ArrayDesign"));
        CLASS_TO_QNAME.put(AssayType.class, new QName(
                "gme://External.caArray.caBIG/1.0/gov.nih.nci.caarray.external.array", "AssayType"));
    }

    private SearchResultIterator<T> searchResultIterator;
    private QName qname;

    public SearchEnumIterator(Class<T> elementClass, SearchResultIterator<T> searchResultIterator) throws RemoteException {
        this.searchResultIterator = searchResultIterator;
        this.qname = CLASS_TO_QNAME.get(elementClass);
    }
    
    /**
     * {@inheritDoc}
     */
    public IterationResult next(IterationConstraints ic) throws TimeoutException, NoSuchElementException {
        if (this.searchResultIterator == null) {
            throw new NoSuchElementException("Enumeration has been released");
        }
        
        List<SOAPElement> soapElements = new ArrayList<SOAPElement>(ic.getMaxElements());        
        try {
            for (int i = 0; i < ic.getMaxElements() && searchResultIterator.hasNext(); i++) {
                T nextResult = searchResultIterator.next();
                SOAPElement element = ObjectSerializer.toSOAPElement(nextResult, this.qname);
                soapElements.add(element);
            }
           
            return wrapUpElements(soapElements, soapElements.size() < ic.getMaxElements());
        } catch (Exception ex) {
            release();
            NoSuchElementException nse = new NoSuchElementException("Error generating elements " + ex.getMessage());
            nse.setStackTrace(ex.getStackTrace());
            throw nse;
        }
    }
    
    protected IterationResult wrapUpElements(List<SOAPElement> soapElements, boolean end) {
        SOAPElement[] elements = new SOAPElement[soapElements.size()];
        soapElements.toArray(elements);
        return new IterationResult(elements, end);
    }
    
    /**
     * {@inheritDoc}
     */
    public void release() {
        this.searchResultIterator = null;
    }
}
