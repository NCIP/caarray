/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-common-jar
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-common-jar Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-common-jar Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-common-jar Software; (ii) distribute and
 * have distributed to and by third parties the caarray-common-jar Software and any
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
package gov.nih.nci.caarray.domain;

import gov.nih.nci.caarray.util.HibernateUtil;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.IndexColumn;

/**
 * This class is used to get around a mysql issue where the mysql server uses memory extremetly
 * poorly on a connection where a blob is being used.  In order to get around this memory issue we
 * are breaking a single blob up in to many smaller blobs.
 * @author Scott Miller
 */
@Embeddable
public class MultiPartBlob {
    private static final int DEFAULT_BLOB_SIZE = 50 * 1024 * 1024;

    /**
     * Constant holding the default blobl size.  By default it will be 50 MB.
     */
    private static int blobSize = DEFAULT_BLOB_SIZE;

    /**
     * Set the max size of a stored blob.
     * @param size the size in bytes
     */
    public static final void setBlobSize(int size) {
        blobSize = size;
    }

    private List<BlobHolder> blobParts = new ArrayList<BlobHolder>();

    /**
     * The blobParts as stored by hibernate.
     * @return the blobParts the blobParts.
     */
    @OneToMany(fetch = FetchType.LAZY)
    @IndexColumn(name = "contents_index")
    @Cascade(value = CascadeType.ALL)
    private List<BlobHolder> getBlobParts() {
        return this.blobParts;
    }

    /**
     * @param blobParts the blobParts to set
     */
    @SuppressWarnings({ "unused", "PMD.UnusedPrivateMethod" })
    private void setBlobParts(List<BlobHolder> contents) {
        this.blobParts = contents;
    }

    /**
     * Method that takes an input stream and breaks it up in to multiple blobs.
     * Note that this method loads each chunk in to a byte[], while this is not
     * ideal, this will be done by the mysql driver anyway, so we are not adding
     * a new inefficiancy.
     * @param data the input stream to store.
     * @throws IOException on error reading from the stream.
     */
    public void writeData(InputStream data) throws IOException {
        BufferedInputStream bufferedData = new BufferedInputStream(data, blobSize);
        byte[] buffer = new byte[blobSize];
        int len = 0;
        while ((len = bufferedData.read(buffer)) > 0) {
            if (len < blobSize) {
                buffer = ArrayUtils.subarray(buffer, 0, len);
            }
            BlobHolder bh = new BlobHolder();
            bh.setContents(Hibernate.createBlob(buffer));
            getBlobParts().add(bh);
            buffer = new byte[blobSize];
        }
    }

    /**
     * Method that produces a single input stream from the internal list of blobs.
     * @return the input stream.
     * @throws SQLException on error reading from the blobs.
     */
    public InputStream readData() throws SQLException {
        Vector<InputStream> isVector = new Vector<InputStream>(); // NOPMD
        for (BlobHolder currentBlobHolder : getBlobParts()) {
            isVector.add(currentBlobHolder.getContents().getBinaryStream());
        }
        return new SequenceInputStream(isVector.elements());
    }

    /**
     * Clear the blobParts.
     */
    public void clearAndEvictData() {
        HibernateUtil.getCurrentSession().evict(this);
        for (BlobHolder bh : getBlobParts()) {
            bh.setContents(null);
        }
    }
}