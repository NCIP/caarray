//
// $Id: FilePanelTableImp.java 1399 2010-09-08 20:02:33Z etienne_sf $
//
// jupload - A file upload applet.
// Copyright 2007 The JUpload Team
//
// Created: ?
// Creator: William JinHua Kwong
// Last modified: $Date: 2010-09-08 22:02:33 +0200 (mer., 08 sept. 2010) $
//
// This program is free software; you can redistribute it and/or modify it under
// the terms of the GNU General Public License as published by the Free Software
// Foundation; either version 2 of the License, or (at your option) any later
// version. This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
// details. You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software Foundation, Inc.,
// 675 Mass Ave, Cambridge, MA 02139, USA.
package wjhk.jupload2.gui.filepanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.TableColumnModel;

import wjhk.jupload2.exception.JUploadExceptionStopAddingFiles;
import wjhk.jupload2.filedata.FileData;
import wjhk.jupload2.gui.JUploadPanel;
import wjhk.jupload2.policies.UploadPolicy;

/**
 * Implementation of the FilePanel : it creates the
 * {@link wjhk.jupload2.gui.filepanel.FilePanelJTable}, and handles the
 * necessary functionalities.
 * 
 * @author William JinHua Kwong
 * @version $Revision: 1399 $
 */
public class FilePanelTableImp extends JPanel implements FilePanel,
        ComponentListener {

    /** A generated serialVersionUID, to avoid warning during compilation */
    private static final long serialVersionUID = -8273990467324350526L;
    private FilePanelJTable jtable;
    private FilePanelDataModel2 model;
    /**
     * The current policy, always useful.
     */
    private UploadPolicy uploadPolicy = null;
    /**
     * The main panel of the applet.
     */
    private JUploadPanel juploadPanel = null;

    ;

    /**
     * The view, which displays the view.
     */
    private JScrollPane scrollPane = null;

    /**
     * Creates a new instance.
     * 
     * @param juploadPanel The upload panel (parent).
     * @param uploadPolicy The upload policy to apply.
     */
    public FilePanelTableImp(JUploadPanel juploadPanel,
            UploadPolicy uploadPolicy) {
        this.juploadPanel = juploadPanel;
        this.uploadPolicy = uploadPolicy;

        setLayout(new BorderLayout());
        addMouseListener(juploadPanel.getMouseListener());
        setTransferHandler(juploadPanel.getTransferHandler());

        this.jtable = new FilePanelJTable(juploadPanel, uploadPolicy);

        this.model = new FilePanelDataModel2(uploadPolicy);
        this.jtable.setModel(this.model);

        this.scrollPane = new JScrollPane(this.jtable);
        add(this.scrollPane, BorderLayout.CENTER);
        this.scrollPane.addMouseListener(juploadPanel.getMouseListener());

        // We must resize columns, when the size of the view changes.
        this.scrollPane.getViewport().addComponentListener(this);
    }

    /**
     * @see wjhk.jupload2.gui.filepanel.FilePanel#addFiles(java.io.File[],java.io.File)
     */
    public final void addFiles(File[] f, File root) {
        if (null == f) {
            throw new java.lang.IllegalArgumentException(
                    "FilePanelTableImpl: filesToUpload may not be null)");
        } else {
            try {
                for (int i = 0; i < f.length; i++) {
                    addDirectoryFiles(f[i], root);
                }
            } catch (JUploadExceptionStopAddingFiles e) {
                // The user want to stop here. Nothing else to do.
                this.uploadPolicy.displayWarn(getClass().getName()
                        + ".addFiles() [" + e.getClass().getName() + "]: "
                        + e.getMessage());
            }
        }
        this.juploadPanel.updateButtonState();
    }

    /**
     * This method allows a recursive calls through the file hierarchy.
     * 
     * @param f The directory that contains the files to add
     * @param root The common root of all the added files.
     * @throws JUploadExceptionStopAddingFiles
     */
    private final void addDirectoryFiles(File f, File root)
            throws JUploadExceptionStopAddingFiles {
        if (!f.isDirectory()) {
            addFileOnly(f, root);
        } else {
            File[] dirFiles = f.listFiles();
            for (int i = 0; i < dirFiles.length; i++) {
                if (dirFiles[i].isDirectory()) {
                    addDirectoryFiles(dirFiles[i], root);
                } else {
                    addFileOnly(dirFiles[i], root);
                }
            }
        }
    }

    /**
     * Adds a single file into the file list.
     * 
     * @param f The file to add.
     * @param root The common root of all the added files.
     * @throws JUploadExceptionStopAddingFiles
     */
    private final void addFileOnly(File f, File root)
            throws JUploadExceptionStopAddingFiles {
        // Make sure we don't select the same file twice.
        if (!this.model.contains(f)) {
            this.model.addFile(f, root);
        }
    }

    /**
     * @see wjhk.jupload2.gui.filepanel.FilePanel#getFiles()
     */
    public final FileData[] getFiles() {
        FileData[] files = new FileData[getFilesLength()];
        for (int i = 0; i < files.length; i++) {
            files[i] = this.model.getFileDataAt(i);
        }
        return files;
    }

    /**
     * @see wjhk.jupload2.gui.filepanel.FilePanel#getFilesLength()
     */
    public final int getFilesLength() {
        return this.jtable.getRowCount();
    }

    /**
     * @see wjhk.jupload2.gui.filepanel.FilePanel#removeSelected()
     */
    public final void removeSelected() {
        int[] rows = this.jtable.getSelectedRows();
        for (int i = rows.length - 1; 0 <= i; i--) {
            this.model.removeRow(rows[i]);
        }
    }

    /**
     * @see java.awt.Container#removeAll()
     */
    @Override
    public final void removeAll() {
        for (int i = getFilesLength() - 1; 0 <= i; i--) {
            this.model.removeRow(i);
        }
    }

    /**
     * Removes all occurences of a file from the list. Each file should only
     * appear once here, but nobodody knows !
     * 
     * @param fileData The file to remove
     */
    public final void remove(FileData fileData) {
        this.model.removeRow(fileData);
    }

    /**
     * Clear the current selection in the JTable.
     */
    public final void clearSelection() {
        this.jtable.clearSelection();
    }

    /** @see wjhk.jupload2.gui.filepanel.FilePanel#focusTable() */
    public final void focusTable() {
        if (0 < this.jtable.getRowCount()) {
            this.jtable.requestFocus();
        }
    }

    /** @see wjhk.jupload2.gui.filepanel.FilePanel#getFileDataAt(Point) */
    public FileData getFileDataAt(Point point) {
        int row = this.jtable.rowAtPoint(point);
        return this.model.getFileDataAt(row);
    }

    /**
     * Return the component on which drop event can occur. Used by
     * {@link JUploadPanel}, when initializing the DropTarget.
     * 
     * @return Component on which the drop event can occur.
     */
    public Component getDropComponent() {
        return this;
    }

    /**
     * Catches the <I>hidden</I> event on the JViewport. {@inheritDoc}
     */
    public void componentHidden(ComponentEvent arg0) {
        // We don't care...
    }

    /**
     * Catches the <I>moved</I> event on the JViewport. {@inheritDoc}
     */
    public void componentMoved(ComponentEvent arg0) {
        // We don't care...
    }

    /**
     * When the size of the file list (actually the JViewport) changes, we adapt
     * the size if the columns. {@inheritDoc}
     */
    public void componentResized(ComponentEvent arg0) {
        // Is the width set?
        if (getWidth() > 0) {
            TableColumnModel colModel = this.jtable.getColumnModel();
            for (int i = 0; i < this.model.getColumnCount(); i++) {
                colModel
                        .getColumn(i)
                        .setPreferredWidth(
                                (this.model.getColumnSizePercentage(i) * this.scrollPane
                                        .getViewport().getWidth()) / 100);
            }
        }
    }

    /**
     * Catches the <I>shown</I> event on the JViewport. {@inheritDoc}
     */
    public void componentShown(ComponentEvent arg0) {
        // We don't care...
    }

    /**
     * Set color of files list grid border.
     * @param color awt Color
     */
    public void setGridBorderColor(Color color) {
        this.jtable.setGridColor(color);
    }

    /**
     * Set back color of table header
     * @param color awt Color
     */
    public void setTableHeaderBackColor(Color color) {
        this.jtable.getTableHeader().setBackground(color);
    }

    /**
     * Set table header text font
     * @param color awt Color
     */
    public void setTableHeaderFont(Font font) {
        this.jtable.getTableHeader().setFont(font);
    }

    /**
     * Set text color of table header
     * @param color awt Color
     */
    public void setTableHeaderTextColor(Color color) {
        this.jtable.getTableHeader().setForeground(color);
    }
}
