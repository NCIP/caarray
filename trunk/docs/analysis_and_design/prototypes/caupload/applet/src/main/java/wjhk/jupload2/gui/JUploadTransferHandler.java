package wjhk.jupload2.gui;

/**
 * The JUploadTransferHandler allows easy management of pasted files onto the
 * applet. It just checks that the pasted selection is compatible (that is: it's
 * a file list), and calls the addFile methods, to let the core applet work.
 */
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import wjhk.jupload2.filedata.DefaultFileData;
import wjhk.jupload2.policies.UploadPolicy;

class JUploadTransferHandler extends TransferHandler {

  /** A generated serialVersionUID, to avoid warning during compilation */
  private static final long serialVersionUID = -1241261479500810699L;
  DataFlavor fileListFlavor = DataFlavor.javaFileListFlavor;
  /** Specific data flavor for Linux where the clipboard contains URLs to files instead of the files themselves */
  DataFlavor uriListFlavor;
  /**
   * The JUpload panel for this applet.
   */
  JUploadPanel uploadPanel = null;
  /**
   * The current upload policy.
   */
  UploadPolicy uploadPolicy = null;

  /**
   * The standard constructor.
   *
   * @param uploadPolicy The current uploadPolicy
   * @param uploadPanel The JUploadPanel. Must given here, as this constructor
   *            is called in the JUploadPanel construction. So the
   *            uploadPolicy.getUploadPanel() returns null.
   */
  public JUploadTransferHandler(UploadPolicy uploadPolicy,
          JUploadPanel uploadPanel) {
    this.uploadPolicy = uploadPolicy;
    this.uploadPanel = uploadPanel;
    try {
      this.uriListFlavor = new DataFlavor("text/uri-list;class=java.lang.String");
    } catch (ClassNotFoundException ex) {
      this.uriListFlavor = DataFlavor.javaFileListFlavor;
    }
  }

  /**
   * @see javax.swing.TransferHandler#importData(javax.swing.JComponent,
   *      java.awt.datatransfer.Transferable)
   */
  @Override
  @SuppressWarnings("unchecked")
  public boolean importData(JComponent c, Transferable t) {
    DataFlavor[] flavors = t.getTransferDataFlavors();
    boolean importAccepted = false;
    if (canImport(c, flavors)) {
      try {
        List<File> fileList = new ArrayList<File>();
        if (isFile(flavors)) {
          fileList = (List<File>) t.getTransferData(this.fileListFlavor);
          importAccepted = true;
        } else if (isUrl(flavors)) {
          Reader in = uriListFlavor.getReaderForText(t);
          BufferedReader br = new BufferedReader(in);
          String uriStr;
          while ((uriStr = br.readLine()) != null) {
            try {
              fileList.add(new File(new URI(uriStr)));
            } catch (URISyntaxException use) {
              this.uploadPolicy.displayErr(this.getClass().getName()
                      + ".importData()", use);
            }
          }
          importAccepted = true;
        }
        File[] fileArray = fileList.toArray(new File[fileList.size()]);
        this.uploadPanel.getFilePanel().addFiles(fileArray,
                DefaultFileData.getRoot(fileArray));
        return importAccepted;
      } catch (UnsupportedFlavorException ufe) {
        this.uploadPolicy.displayErr(this.getClass().getName()
                + ".importData()", ufe);
      } catch (IOException ioe) {
        this.uploadPolicy.displayErr(this.getClass().getName()
                + ".importData()", ioe);
      }
    }

    return importAccepted;
  }

  /**
   * @see javax.swing.TransferHandler#getSourceActions(javax.swing.JComponent)
   */
  @Override
  public int getSourceActions(JComponent c) {
    return MOVE;
  }

  /**
   * @see javax.swing.TransferHandler#canImport(javax.swing.JComponent,
   *      java.awt.datatransfer.DataFlavor[])
   */
  @Override
  public boolean canImport(JComponent c, DataFlavor[] flavors) {
    return isFile(flavors) || isUrl(flavors);
  }

  /**
   * Indicates if this data flavor is for a File type of data.
   * @param flavors the flavors
   * @return true if the data is of type file.
   */
  protected boolean isFile(DataFlavor[] flavors) {
    for (DataFlavor flavor : flavors) {
      if (this.fileListFlavor.equals(flavor)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Indicates if this data flavor is for a URL type of data.
   * @param flavors the flavors
   * @return true if the data is of type url.
   */
  protected boolean isUrl(DataFlavor[] flavors) {
    for (DataFlavor flavor : flavors) {
      if (this.uriListFlavor.equals(flavor)) {
        return true;
      }
    }
    return false;
  }
}
