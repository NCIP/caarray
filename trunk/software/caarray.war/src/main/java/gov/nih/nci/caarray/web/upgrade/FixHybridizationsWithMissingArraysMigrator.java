//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.upgrade;

import gov.nih.nci.caarray.application.arraydata.AbstractDataFileHandler;
import gov.nih.nci.caarray.application.arraydata.ArrayDataHandlerFactory;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignServiceBean;
import gov.nih.nci.caarray.domain.MultiPartBlob;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.util.HibernateUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
public class FixHybridizationsWithMissingArraysMigrator extends AbstractMigrator {
    private Connection connection;
    
    /**
     * {@inheritDoc}
     */
    public void migrate() throws MigrationStepFailedException {
        try {
            this.connection = HibernateUtil.getNewConnection();
            List<Long> hybIdsWithoutArray = getHybIdsWithNoArrayDesign();
            for (Long hid : hybIdsWithoutArray) {
                ensureArrayDesignSetForHyb(hid);
            }
        } catch (SQLException e) {
            throw new MigrationStepFailedException("Could not fix hybridizations", e);
        } catch (IOException e) {
            throw new MigrationStepFailedException("Could not fix hybridizations", e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) { // NOPMD
                // no-op
            }            
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
        PreparedStatement ps = this.connection.prepareStatement(sql);
        ps.setLong(1, adid);
        ps.setLong(2, aid);
        ps.executeUpdate();
    }

    private void setArrayAndDesign(Long hid, Long adid) throws SQLException {
        String sql = "insert into array (design) values (?)";
        PreparedStatement ps = this.connection.prepareStatement(sql);
        ps.setLong(1, adid);
        ps.executeUpdate();
        
        sql = "select last_insert_id()";
        ps = this.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        if (!rs.next()) {
            throw new SQLException("Could not determine id of just-inserted array row");
        }
        Long aid = rs.getLong(1);
        
        sql = "update hybridization set array = ? where id = ?";
        ps = this.connection.prepareStatement(sql);
        ps.setLong(1, aid);
        ps.setLong(2, hid);
        ps.executeUpdate();        
    }
    
    private Long getArrayFromHybridizationId(Long hid) throws SQLException {
        String sql = "select h.array from hybridization h where h.id = ?";
        PreparedStatement ps = this.connection.prepareStatement(sql);
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
                    PreparedStatement ps = connection.prepareStatement(sql);
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
        PreparedStatement ps = this.connection.prepareStatement(sql);
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
        PreparedStatement ps = this.connection.prepareStatement(sql);
        ps.setLong(1, fileId);

        ResultSet rs = ps.executeQuery();
        MultiPartBlob mpb = new MultiPartBlob();
        while (rs.next()) {
            mpb.addBlob(rs.getBlob(1));
        }
        
        File f = File.createTempFile("datafile", null);
        InputStream is = mpb.readCompressedContents();
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
        PreparedStatement ps = this.connection.prepareStatement(sql);
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
        PreparedStatement ps = this.connection.prepareStatement(sql);
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
        PreparedStatement ps = this.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        List<Long> ids = new ArrayList<Long>();
        while (rs.next()) {
            ids.add(rs.getLong(1));
        }
        return ids;
    }
}
