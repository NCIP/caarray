/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-ejb-jar
 * Software was developed in conjunction with the National Cancer Institute 
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent 
 * government employees are authors, any rights in such works shall be subject 
 * to Title 17 of the United States Code, section 105. 
 *
 * This caarray-ejb-jar Software License (the License) is between NCI and You. You (or 
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
 * its rights in the caarray-ejb-jar Software to (i) use, install, access, operate, 
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-ejb-jar Software; (ii) distribute and 
 * have distributed to and by third parties the caarray-ejb-jar Software and any 
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
package gov.nih.nci.caarray.services.external.v1_0.data;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.data.DataSet;
import gov.nih.nci.caarray.external.v1_0.data.FileStreamableContents;
import gov.nih.nci.caarray.external.v1_0.data.MageTabFileSet;
import gov.nih.nci.caarray.external.v1_0.query.DataSetRequest;
import gov.nih.nci.caarray.services.external.v1_0.InvalidInputException;
import gov.nih.nci.caarray.services.external.v1_0.InvalidReferenceException;

import javax.ejb.Remote;

/**
 * Remote service for retrieving file and parsed data. Used by the grid service, and can also be used directly by 
 * EJB clients.
 * 
 * @author dkokotov
 */
@Remote
public interface DataService {
    /**
     * The JNDI name to look up this Remote EJB under.
     */
    String JNDI_NAME = "caarray/external/v1_0/DataServiceBean";
        
    /**
     * Retrieves the parsed data set identified by the given request. The DataSet will contain a HybridizationData for
     * each Hybridization referenced by the request, and/or for each Hybridization linked to a data File referenced by
     * the request. Each HybridizationData will contain one DataColumn for each QuantitationType referenced by the
     * request.
     * 
     * @param dataSetRequest a DataSetRequest instance identifying the parsed data to be retrieved. The request must
     *            specify at least one hybridization or one file, and at least one quantitation type.
     * @throws InvalidReferenceException if any of the hybridization, file or quantitation references in the
     *             dataSetRequest are not valid
     * @throws InvalidInputException if no quantitation type is specified in the request, or if at least one file OR one
     *             hybridization is not specified in the request.
     * @throws InconsistentDataSetsException if the data sets for the hybridizations and/or files in the request are not
     *             consistent, e.g. do not correspond to the same design element list.
     * @return the DataSet with the requested parsed data, as specified above.
     */
    DataSet getDataSet(DataSetRequest dataSetRequest) throws InvalidReferenceException, InvalidInputException,
            InconsistentDataSetsException;

    /**
     * Returns a RemoteInputStream through which the client can retrieve the data for the file identified by the given
     * reference. The client must take care to ensure the RemoteInputStream is closed when the contents is read, even
     * when exceptions occur.
     * 
     * @param fileRef the reference identifying the file to retrieve.
     * @param compressed if true, then the RemoteInputStream will return the contents of the file compressed using Gzip
     * @return the remote input stream (using the rmiio library) from which the file contents can be read.
     * @throws InvalidReferenceException if the fileRef is not a valid file reference.
     * @throws DataTransferException if there is an error streaming the data.
     */
    FileStreamableContents streamFileContents(CaArrayEntityReference fileRef, boolean compressed)
            throws InvalidReferenceException, DataTransferException;

    /**
     * Retrieves a set of files containing the MAGE-TAB IDF and SDRF for the experiment identified by the given
     * reference. The IDF and SDRF are generated dynamically. The file set also contains references to the data files
     * referenced by the SDRF.
     * 
     * @param experimentRef reference identifying the experiment
     * @return a MageTabFileSet consisting of the generated IDF and SDRF for this experiment, and references to the 
     * data files linked to hybridizations in the experiment
     * @throws InvalidReferenceException if the experimentRef is not a valid experiment reference
     * @throws DataTransferException if there is an error generating the mage-tab file data
     */    
    MageTabFileSet exportMageTab(CaArrayEntityReference experimentRef) throws InvalidReferenceException,
            DataTransferException;
}
