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
package gov.nih.nci.caarray.dataStorage.spi;

import gov.nih.nci.caarray.dataStorage.DataStoreException;
import gov.nih.nci.caarray.dataStorage.StorageMetadata;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;

/**
 * Primary interface for storage engine modules that provide binary data storage and retrieval services to caArray.
 * <p>
 * A Data Storage engine is able to store and load opaque blocks of binary data. Data Storages support handling the data
 * either as-is or compressed using GZip, and can accept or produce either representation (it is up to the storage
 * engine to decide whether to compress/decompress on the fly or store both representations).
 * <p>
 * A block of data is identified by a URI, initially assigned by the storage engine that first stored it. The URI
 * consists of a scheme and a scheme-specific part. The scheme is used to identify the storage engine which is capable
 * of handling it. A storage engine may be capable of handling multiple schemes, but a given scheme should only be
 * handled by a single storage engine.
 */
public interface DataStorage {
    /**
     * Add a new block of data to the storage system.
     * 
     * @param stream a stream from which data will be read
     * @param boolean compressed whether the data in the stream is compressed or not. If the data is compressed, GZip
     *        compression should be used.
     * @return a StorageMetadata object describing the added block of data, including a handle for later access to the
     *         data
     */
    StorageMetadata add(InputStream stream, boolean compressed) throws DataStoreException;

    /**
     * Get a listing of all data blocks this storage engine is currently managing.
     * 
     * @return an Iterable of StorageMetadata, where each item describes one block of data managed by this storage
     *         engine
     */
    Iterable<StorageMetadata> list();

    /**
     * Remove a block of data from storage.
     * 
     * @param handle the handle referencing the data to be removed.
     */
    void remove(URI handle) throws DataStoreException;

    /**
     * Remove multiple data blocks from storage. Some storage engines may be more efficient at removing multiple data
     * blocks at a time, and thus may have an optimized implementation of this method.
     * 
     * @param handles the handles referencing the datas to be removed.
     */
    void remove(Collection<URI> handles) throws DataStoreException;

    /**
     * Return a <code>java.io.File</code> which will hold the data identified by the given handle. The client should
     * eventually call releaseFile() for this <code>handle</code>.
     * 
     * @param handle the handle referencing the data to be obtained.
     * @param compressed whether the file should hold the compressed (if true) or uncompressed (if false) view of the
     *            data. Compressed data uses GZip compression.
     * @return the <code>java.io.File</code> with the referenced data, possibly compressed.
     */
    File openFile(URI handle, boolean compressed) throws DataStoreException;

    /**
     * Return a <code>java.io.InputStream</code> which can be used to read the data identified by the given handle. The
     * client should make sure to close the stream once it is done with it. Implementors may use a temporary file to
     * hold the data, but may also provide a stream that accesses the data from its original location.
     * 
     * @param handle the handle referencing the data to be obtained.
     * @param compressed whether the stream should hold the compressed (if true) or uncompressed (if false) view of the
     *            data. Compressed data uses GZip compression.
     * @return the <code>java.io.InputStream</code> with the referenced data, possibly compressed.
     */
    InputStream openInputStream(URI handle, boolean compressed) throws DataStoreException;

    /**
     * Releases the file that was previously obtained from this storage engine for the data block with given handle.
     * 
     * @param handle the handle referencing the data block that was obtained as a file via a call to openFile.
     * @param compressed whether the openFile call was to get compressed or uncompressed data.
     */
    void releaseFile(URI handle, boolean compressed);
}
