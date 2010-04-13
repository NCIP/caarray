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
package gov.nih.nci.caarray.upgrade;

import gov.nih.nci.caarray.application.arraydata.AbstractDataFileHandler;
import gov.nih.nci.caarray.application.arraydata.ArrayDataHandlerFactory;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignServiceBean;
import gov.nih.nci.caarray.domain.MultiPartBlob;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.FileType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import liquibase.database.Database;
import liquibase.exception.CustomChangeException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * Migrator to find hybridizations with missing arrays, and where possible, create them 
 * based on array designs specified in data files or the experiment. 
 * 
 * The migrator will use the array design for the experiment, if there is only one. Otherwise,
 * it will extract the array design from one of the data files associated with the hybridization, if
 * present. If none of the data files specify the array design, and the experiment has more than one
 * array design specified, then no action will be taken.
 *
 * @author Dan Kokotov
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.CyclomaticComplexity" })
public class FixHybridizationsWithMissingArraysMigrator extends AbstractCustomChange {        
    private Database database;
            
    /**
     * {@inheritDoc}
     */
    public void execute(Database db) throws CustomChangeException {
        try {
            this.database = db;
            List<Long> hybIdsWithoutArray = getHybIdsWithNoArrayDesign();
            for (Long hid : hybIdsWithoutArray) {
                ensureArrayDesignSetForHyb(hid);
            }
        } catch (SQLException e) {
            throw new CustomChangeException("Could not fix hybridizations", e);
        } catch (IOException e) {
            throw new CustomChangeException("Could not fix hybridizations", e);
        }
        
    }

    private void ensureArrayDesignSetForHyb(Long hid) throws SQLException, IOException {
        Long adid = getArrayDesignIdFromExperiment(hid);
        if (adid == null) {
            adid = getArrayDesignIdFromFiles(hid);
        }
        if (adid != null) {
            setArrayDesignForHyb(hid, adid);
        }
    }
    
    private void setArrayDesignForHyb(Long hid, Long adid) throws SQLException {
        Long aid = getArrayFromHybridizationId(hid);
        if (aid == null) {
            setArrayAndDesign(hid, adid);            
        } else {
            setArrayDesignForArray(aid, adid);
        }
    }

    private void setArrayDesignForArray(Long aid, Long adid) throws SQLException {
        String sql = "update array set design = ? where id = ?";
        PreparedStatement ps = database.getConnection().prepareStatement(sql);
        ps.setLong(1, adid);
        ps.setLong(2, aid);
        ps.executeUpdate();
    }

    private void setArrayAndDesign(Long hid, Long adid) throws SQLException {
        String sql = "insert into array (design) values (?)";
        PreparedStatement ps = database.getConnection().prepareStatement(sql);
        ps.setLong(1, adid);
        ps.executeUpdate();
        
        sql = "select last_insert_id()";
        ps = database.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        if (!rs.next()) {
            throw new SQLException("Could not determine id of just-inserted array row");
        }
        Long aid = rs.getLong(1);
        
        sql = "update hybridization set array = ? where id = ?";
        ps = database.getConnection().prepareStatement(sql);
        ps.setLong(1, aid);
        ps.setLong(2, hid);
        ps.executeUpdate();        
    }
    
    private Long getArrayFromHybridizationId(Long hid) throws SQLException {
        String sql = "select h.array from hybridization h where h.id = ?";
        PreparedStatement ps = database.getConnection().prepareStatement(sql);
        ps.setLong(1, hid);
        ResultSet rs = ps.executeQuery();
        Long id = null;
        if (rs.first() && rs.getLong(1) != 0) {
            id = rs.getLong(1);
        }
        return id;
    }

    private Long getArrayDesignIdFromFiles(Long hid) throws SQLException, IOException {
        List<Long> dataFileIds = getDataFileIds(hid);
        for (Long fileId : dataFileIds) {
            Long adid = getArrayDesignFromFile(fileId);
            if (adid != null) {
                return adid;
            }
        }
        return null;
    }
    
    private Long getArrayDesignFromFile(Long fileId) throws SQLException, IOException {
        FileType ft = getFileType(fileId);

        if (!ft.isArrayData()) {
            return null;
        }
        
        AbstractDataFileHandler handler = ArrayDataHandlerFactory.getInstance().getHandler(ft);
        File file = getFile(fileId);
        
        ArrayDesignService ads = new ArrayDesignServiceBean() {
            public ArrayDesign getArrayDesign(String lsidAuthority,
                    String lsidNamesapce, String lsidObjectId) {
                try {
                    String sql = "select id from array_design where lsid_authority = ? and lsid_namespace = ? "
                        + " and lsid_object_id = ?";
                    PreparedStatement ps = database.getConnection().prepareStatement(sql);
                    ps.setString(1, lsidAuthority);
                    ps.setString(2, lsidNamesapce);
                    ps.setString(3, lsidObjectId);
                    ResultSet rs = ps.executeQuery();
                    ArrayDesign ad = new ArrayDesign();
                    if (rs.first()) {
                        ad.setId(rs.getLong(1));
                    }
                    return ad;
                } catch (SQLException e) {
                    throw new IllegalStateException("Could not get array design ", e);
                }
            }
        };
        ArrayDesign ad = handler.getArrayDesign(ads, file);
        
        FileUtils.deleteQuietly(file);
        return ad.getId();
    }
    
    private FileType getFileType(Long fileId) throws SQLException {
        String sql = "select type from caarrayfile where id = ?";
        PreparedStatement ps = database.getConnection().prepareStatement(sql);
        ps.setLong(1, fileId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return FileType.valueOf(rs.getString(1));        
        }
        return null;        
    }


    private File getFile(Long fileId) throws SQLException, IOException {
        String sql = "select bh.contents from caarrayfile f join caarrayfile_blob_parts fbp on f.id = fbp.caarrayfile "
            + " join blob_holder bh on fbp.blob_parts = bh.id where f.id = ? order by fbp.contents_index";
        PreparedStatement ps = database.getConnection().prepareStatement(sql);
        ps.setLong(1, fileId);

        ResultSet rs = ps.executeQuery();
        MultiPartBlob mpb = new MultiPartBlob();
        while (rs.next()) {
            mpb.addBlob(rs.getBlob(1));
        }
        
        File f = File.createTempFile("datafile", null);
        InputStream is = mpb.readUncompressedContents();
        FileOutputStream fos = FileUtils.openOutputStream(f); 
        IOUtils.copy(is, fos);
        IOUtils.closeQuietly(is);
        IOUtils.closeQuietly(fos);        
        
        return f;        
    }

    private List<Long> getDataFileIds(Long hid) throws SQLException {
        String sql = "select f.id from caarrayfile f left join arraydata ad on f.id = ad.data_file "
            + " left join rawarraydata_hybridizations radh on ad.id = radh.rawarraydata_id "
            + " left join hybridization h on radh.hybridization_id = h.id "
            + " left join derivedarraydata_hybridizations dadh on ad.id = dadh.derivedarraydata_id "
            + " left join hybridization h2 on dadh.hybridization_id = h2.id where h.id = ? or h2.id = ?";
        PreparedStatement ps = database.getConnection().prepareStatement(sql);
        ps.setLong(1, hid);
        ps.setLong(2, hid);
        ResultSet rs = ps.executeQuery();
        List<Long> ids = new ArrayList<Long>();
        while (rs.next()) {
            ids.add(rs.getLong(1));
        }
        return ids;
    }

    private Long getArrayDesignIdFromExperiment(Long hid) throws SQLException {
        String sql = "select ad.id from hybridization h join experiment e on h.experiment = e.id "
                + " join experimentarraydesign ead on e.id = ead.experiment_id "
                + " join array_design ad on ead.arraydesign_id = ad.id where h.id = ?";
        PreparedStatement ps = database.getConnection().prepareStatement(sql);
        ps.setLong(1, hid);
        ResultSet rs = ps.executeQuery();
        Long id = null;
        if (rs.first() && rs.isLast()) {
            id = rs.getLong(1);
        }
        return id;
    }
    
    private List<Long> getHybIdsWithNoArrayDesign() throws SQLException {
        String sql = "select h.id from hybridization h left join array a on h.array = a.id "
                + " left join array_design ad on a.design = ad.id where a.id is null or ad.id is null";
        PreparedStatement ps = database.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        List<Long> ids = new ArrayList<Long>();
        while (rs.next()) {
            ids.add(rs.getLong(1));
        }
        return ids;
    }
}
