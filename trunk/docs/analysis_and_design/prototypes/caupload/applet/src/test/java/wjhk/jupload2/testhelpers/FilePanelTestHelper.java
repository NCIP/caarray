//
// $Id$
//
// jupload - A file upload applet.
//
// Copyright 2010 The JUpload Team
//
// Created: 9 fevr. 2010
// Creator: etienne_sf
// Last modified: $Date$
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
package wjhk.jupload2.testhelpers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.TransferHandler;

import org.apache.log4j.Logger;

import wjhk.jupload2.filedata.FileData;
import wjhk.jupload2.gui.filepanel.FilePanel;

/**
 * @author etienne_sf
 * 
 */
public class FilePanelTestHelper implements FilePanel {

    /** Logger for this class */
    protected final Logger logger = Logger.getLogger(getClass());
    /** List of files that this class is 'filepanel' contains */
    public List<FileData> filesToUpload = new ArrayList<FileData>();

    /**
     * @param filesToUpload
     */
    public FilePanelTestHelper(List<FileData> filesToUpload) {
        this.filesToUpload = filesToUpload;
    }

    /**
     * @see wjhk.jupload2.gui.filepanel.FilePanel#addFiles(java.io.File[],
     *      java.io.File)
     */
    public void addFiles(File[] f, File root) {
        throw new UnsupportedOperationException(this.getClass()
                + ".addFiles() is not implemented in tests cases");
    }

    /**
     * @see wjhk.jupload2.gui.filepanel.FilePanel#clearSelection()
     */
    public void clearSelection() {
        throw new UnsupportedOperationException(this.getClass()
                + ".clearSelection() is not implemented in tests cases");
    }

    /**
     * @see wjhk.jupload2.gui.filepanel.FilePanel#focusTable()
     */
    public void focusTable() {
        throw new UnsupportedOperationException(this.getClass()
                + ".focusTable() is not implemented in tests cases");
    }

    /**
     * @see wjhk.jupload2.gui.filepanel.FilePanel#getActionMap()
     */
    public ActionMap getActionMap() {
        throw new UnsupportedOperationException(this.getClass()
                + ".getActionMap() is not implemented in tests cases");
    }

    /**
     * @see wjhk.jupload2.gui.filepanel.FilePanel#getDropComponent()
     */
    public Component getDropComponent() {
        throw new UnsupportedOperationException(this.getClass()
                + ".getDropComponent() is not implemented in tests cases");
    }

    /**
     * @see wjhk.jupload2.gui.filepanel.FilePanel#getFileDataAt(java.awt.Point)
     */
    public FileData getFileDataAt(Point point) {
        throw new UnsupportedOperationException(this.getClass()
                + ".getFileDataAt() is not implemented in tests cases");
    }

    /**
     * @see wjhk.jupload2.gui.filepanel.FilePanel#getFiles()
     */
    public FileData[] getFiles() {
        // filesToUpload.toArray() doesn't return a FileData[], but a Object[]
        // that can not be cast to a FileData[] !
        FileData[] files = new FileData[this.filesToUpload.size()];
        int i = 0;
        for (FileData fd : this.filesToUpload) {
            files[i++] = fd;
        }
        return files;
    }

    /**
     * @see wjhk.jupload2.gui.filepanel.FilePanel#getFilesLength()
     */
    public int getFilesLength() {
        return this.filesToUpload.size();
    }

    /**
     * @see wjhk.jupload2.gui.filepanel.FilePanel#remove(wjhk.jupload2.filedata.FileData)
     */
    public void remove(FileData fileData) {
        this.logger.warn(this.getClass()
                + ".remove() is not implemented in tests cases");
    }

    /**
     * @see wjhk.jupload2.gui.filepanel.FilePanel#removeAll()
     */
    public void removeAll() {
        throw new UnsupportedOperationException(this.getClass()
                + ".removeAll() is not implemented in tests cases");
    }

    /**
     * @see wjhk.jupload2.gui.filepanel.FilePanel#removeSelected()
     */
    public void removeSelected() {
        throw new UnsupportedOperationException(this.getClass()
                + ".removeSelected() is not implemented in tests cases");
    }

    /**
     * @see wjhk.jupload2.gui.filepanel.FilePanel#setTransferHandler(javax.swing.TransferHandler)
     */
    public void setTransferHandler(TransferHandler newHandler) {
        throw new UnsupportedOperationException(this.getClass()
                + ".setTransferHandler() is not implemented in tests cases");
    }

    /**
     * @see wjhk.jupload2.gui.filepanel.FilePanel#setGridBorderColor(java.awt.Color) 
     * @param color Color
     */
    public void setGridBorderColor(Color color) {
        throw new UnsupportedOperationException(this.getClass()
                + ".setGridBorderColor() is not implemented in tests cases");
    }

    /**
     * @see wjhk.jupload2.gui.filepanel.FilePanel#setTableHeaderBackColor(java.awt.Color)
     * @param color Color
     */
    public void setTableHeaderBackColor(Color color) {
        throw new UnsupportedOperationException(this.getClass()
                + ".setTableHeaderBackColor() is not implemented in tests cases");
    }

    /**
     * @see wjhk.jupload2.gui.filepanel.FilePanel#setTableHeaderFont(java.awt.Font)
     * @param font Font
     */
    public void setTableHeaderFont(Font font) {
        throw new UnsupportedOperationException(this.getClass()
                + ".setTableHeaderFont() is not implemented in tests cases");
    }

    /**
     * @see wjhk.jupload2.gui.filepanel.FilePanel#setTableHeaderTextColor(java.awt.Color)
     * @param color Color
     */
    public void setTableHeaderTextColor(Color color) {
        throw new UnsupportedOperationException(this.getClass()
                + ".setTableHeaderTextColor() is not implemented in tests cases");
    }
}
