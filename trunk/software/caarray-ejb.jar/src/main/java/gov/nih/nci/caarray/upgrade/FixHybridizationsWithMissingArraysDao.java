/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray2-4-0-x
 * Software was developed in conjunction with the National Cancer Institute 
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent 
 * government employees are authors, any rights in such works shall be subject 
 * to Title 17 of the United States Code, section 105. 
 *
 * This caarray2-4-0-x Software License (the License) is between NCI and You. You (or 
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
 * its rights in the caarray2-4-0-x Software to (i) use, install, access, operate, 
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray2-4-0-x Software; (ii) distribute and 
 * have distributed to and by third parties the caarray2-4-0-x Software and any 
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
package gov.nih.nci.caarray.upgrade;

import gov.nih.nci.caarray.domain.LSID;
import gov.nih.nci.caarray.domain.MultiPartBlob;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.FileType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates all database access required for the FixHybridizationsWithMissingArraysMigrator.
 * 
 * @author jscott
 */
public class FixHybridizationsWithMissingArraysDao {

    private Connection connection;

    /**
     * Set the connection the dao will use.
     * 
     * @param connection the connection to use
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     * Get a list of hybridizations that either have no associated array or have an associated array with no associated
     * array design.
     * 
     * @return the hybridization ids
     * @throws SQLException on error
     */
    public List<Long> getHybIdsWithNoArrayOrNoArrayDesign() throws SQLException {
        final String sql =
                "select h.id from hybridization h left join array a on h.array = a.id "
                        + " left join array_design ad on a.design = ad.id where a.id is null or ad.id is null";
        final PreparedStatement ps = this.connection.prepareStatement(sql);
        final ResultSet rs = ps.executeQuery();
        final List<Long> ids = new ArrayList<Long>();
        while (rs.next()) {
            ids.add(rs.getLong(1));
        }
        return ids;
    }

    /**
     * Get the unique array design id associated with an experiment.
     * 
     * @param eid the experiment id
     * @return the unique array design id or null if there is no such unique id
     * @throws SQLException on error
     */
    public Long getUniqueArrayDesignIdFromExperiment(Long eid) throws SQLException {
        final String sql =
                "select ad.id from hybridization h join experiment e on h.experiment = e.id "
                        + " join experimentarraydesign ead on e.id = ead.experiment_id "
                        + " join array_design ad on ead.arraydesign_id = ad.id where h.id = ?";
        final PreparedStatement ps = this.connection.prepareStatement(sql);
        ps.setLong(1, eid);
        final ResultSet rs = ps.executeQuery();
        Long id = null;
        if (rs.first() && rs.isLast()) {
            id = rs.getLong(1);
        }
        return id;
    }

    /**
     * Get the ids for all imported files associated with a hybridization.
     * 
     * @param hid the hybridization id
     * @return the id list
     * @throws SQLException on error
     */
    public List<Long> getImportedDataFileIdsFromHybId(Long hid) throws SQLException {
        final String sql =
                " " + " select f.id " + " from caarrayfile f " + " left join arraydata ad on f.id = ad.data_file "
                        + " left join rawarraydata_hybridizations radh on ad.id = radh.rawarraydata_id "
                        + " left join hybridization h on radh.hybridization_id = h.id "
                        + " left join derivedarraydata_hybridizations dadh on ad.id = dadh.derivedarraydata_id "
                        + " left join hybridization h2 on dadh.hybridization_id = h2.id "
                        + " where (h.id = ? or h2.id = ?) " + "   and f.status = 'IMPORTED' ";
        final PreparedStatement ps = this.connection.prepareStatement(sql);
        ps.setLong(1, hid);
        ps.setLong(2, hid);
        final ResultSet rs = ps.executeQuery();
        final List<Long> ids = new ArrayList<Long>();
        while (rs.next()) {
            ids.add(rs.getLong(1));
        }
        return ids;
    }

    /**
     * Get the file type of a file.
     * 
     * @param fileId the id of the file
     * @return the file type or null
     */
    public FileType getFileType(Long fileId) {
        
        throw new UnsupportedOperationException("Operation not supported for v2.5.0 or higher.");

        // TODO: JIRA# ARRAY-1934: Follow-on tasks stemming from decision whether or not to support 
        // direct upgrade from  pre-2.4.1 versions to v2.5. 
        // Pending resolution of above issue, we have temporarily replaced the code below with the
        // code above in order to avoid compile error in the FileType.valueOf() call,
        // which is no longer defined after Dan's plugin redesign. 
        // To fix the compile error requires non-trivial
        // design and implementation which we are deferring pending resolution of above issue.

        // String sql = "select type from caarrayfile where id = ?";
        // PreparedStatement ps = connection.prepareStatement(sql);
        // ps.setLong(1, fileId);
        // ResultSet rs = ps.executeQuery();
        // if (rs.next()) {
        // return FileType.valueOf(rs.getString(1));
        // }
        // return null;

    }

    /**
     * Get the first array design matching an LSID in a list of LSIDs.
     * 
     * @param designLsids the list of LSIDs
     * @return the matching array design or an empty array design if not match is found
     */
    @SuppressWarnings("deprecation")
    public ArrayDesign getFirstArrayDesignFromLsidList(List<LSID> designLsids) {
        for (final LSID lsid : designLsids) {
            try {
                final String sql =
                        "select id from array_design where lsid_authority = ? and lsid_namespace = ? "
                                + " and lsid_object_id = ?";
                final PreparedStatement ps = this.connection.prepareStatement(sql);
                ps.setString(1, lsid.getAuthority());
                ps.setString(2, lsid.getNamespace());
                ps.setString(3, lsid.getObjectId());
                final ResultSet rs = ps.executeQuery();
                if (rs.first()) {
                    final ArrayDesign ad = new ArrayDesign();
                    ad.setId(rs.getLong(1));
                    return ad;
                }
            } catch (final SQLException e) {
                throw new IllegalStateException("Could not get array design ", e);
            }
        }
        return new ArrayDesign();
    }

    /**
     * Get the id of the unique array associated array a hybridization.
     * 
     * @param hid the hybridization id
     * @return the array id or null if there is no such unique array
     * @throws SQLException on error
     */
    public Long getArrayFromHybridizationId(Long hid) throws SQLException {
        final String sql = "select h.array from hybridization h where h.id = ?";
        final PreparedStatement ps = this.connection.prepareStatement(sql);
        ps.setLong(1, hid);
        final ResultSet rs = ps.executeQuery();
        Long id = null;
        if (rs.first() && rs.getLong(1) != 0) {
            id = rs.getLong(1);
        }
        return id;
    }

    /**
     * Get a file BLOB.
     * 
     * @param fileId the file id
     * @return the blob or an empty blob if no data is found
     * @throws SQLException on error
     */
    public MultiPartBlob getFileBlob(Long fileId) throws SQLException {
        final String sql =
                "select bh.contents from caarrayfile f join caarrayfile_blob_parts fbp on f.id = fbp.caarrayfile "
                        + " join blob_holder bh on fbp.blob_parts = bh.id where f.id = ? order by fbp.contents_index";
        final PreparedStatement ps = this.connection.prepareStatement(sql);
        ps.setLong(1, fileId);

        final ResultSet rs = ps.executeQuery();
        final MultiPartBlob mpb = new MultiPartBlob();
        while (rs.next()) {
            mpb.addBlob(rs.getBlob(1));
        }
        return mpb;
    }

    /**
     * Associate an array design with an array.
     * 
     * @param aid the array id
     * @param adid the array design id
     * @throws SQLException on error
     */
    public void setArrayDesignForArray(Long aid, Long adid) throws SQLException {
        final String sql = "update array set design = ? where id = ?";
        final PreparedStatement ps = this.connection.prepareStatement(sql);
        ps.setLong(1, adid);
        ps.setLong(2, aid);
        ps.executeUpdate();
    }

    /**
     * Create an array associated with an array design and associate a hybridization with it.
     * 
     * @param hid the hybridization id
     * @param adid the array design id
     * @throws SQLException on error
     */
    public void setArrayAndDesign(Long hid, Long adid) throws SQLException {
        String sql = "insert into array (design) values (?)";
        PreparedStatement ps = this.connection.prepareStatement(sql);
        ps.setLong(1, adid);
        ps.executeUpdate();

        sql = "select last_insert_id()";
        ps = this.connection.prepareStatement(sql);
        final ResultSet rs = ps.executeQuery();
        if (!rs.next()) {
            throw new SQLException("Could not determine id of just-inserted array row");
        }
        final Long aid = rs.getLong(1);

        sql = "update hybridization set array = ? where id = ?";
        ps = this.connection.prepareStatement(sql);
        ps.setLong(1, aid);
        ps.setLong(2, hid);
        ps.executeUpdate();
    }
}