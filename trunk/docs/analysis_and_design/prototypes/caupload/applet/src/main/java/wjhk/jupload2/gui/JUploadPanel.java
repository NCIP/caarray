//
// $Id: JUploadPanelImpl.java 303 2007-07-21 07:42:51 +0000 (sam., 21 juil.
// 2007)
// etienne_sf $
//
// jupload - A file upload applet.
// Copyright 2007 The JUpload Team
//
// Created: ?
// Creator: William JinHua Kwong
// Last modified: $Date: 2010-02-09 11:32:18 +0100 (mar., 09 févr. 2010) $
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

package wjhk.jupload2.gui;

import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.TransferHandler;

import wjhk.jupload2.gui.filepanel.FilePanel;
import wjhk.jupload2.upload.FileUploadManagerThread;

/**
 * Interface for the central object of the JUpload GUI. It creates and contains
 * all GUI items: creation of necessary elements, and calls to
 * {@link wjhk.jupload2.policies.UploadPolicy} methods to allow easy
 * personalization.
 * 
 * @author etienne_sf
 * 
 */
public interface JUploadPanel {

    /**
     * This methods show or hides the logWindow, depending on the following
     * applet parameters. The following conditions must be met, to hide the log
     * window: <DIR> <LI>showLogWindow (must be False) <LI>debugLevel (must be 0
     * or less) </DIR>
     */
    public void showOrHideLogWindow();

    /**
     * Reaction to a click on the browse button.
     */
    public void doBrowse();

    /**
     * Reaction to a click on the remove button. This method actually removes
     * the selected files in the file list.
     */
    public void doRemove();

    /**
     * Reaction to a click on the removeAll button. This method actually removes
     * all the files in the file list.
     */
    public void doRemoveAll();

    /**
     * Reaction to a click on the upload button. This method can be called from
     * outside to start the upload.
     */
    public void doStartUpload();

    /**
     * Reaction to a click on the stop button. This stops the running on upload.
     * This method can be called from outside to start the upload.
     */
    public void doStopUpload();

    /**
     * Select or unselect the applet buttons
     */
    public void updateButtonState();

    /** Clear the current log window content. */
    public void clearLogWindow();

    /**
     * Copy the log window content into the clipboard. Allows easy access to the
     * debug output.
     * 
     */
    public void copyLogWindow();

    /**
     * @return the actionListener, that'll manage the button interaction.
     */
    public ActionListener getActionListener();

    /**
     * @return the browseButton
     */
    public JButton getBrowseButton();

    /**
     * Returns the awt container, that contains all the objects of the GUI.
     * 
     * @return The Container.
     */
    public JComponent getJComponent();

    /**
     * @return the dndListener
     */
    public DnDListener getDndListener();

    /**
     * @return the filePanel
     */
    public FilePanel getFilePanel();

    /**
     * The component that contains the log window. It is used to display the
     * content of the log window, with the relevant scroll bars.
     * 
     * @return the jLogWindowPane
     */
    public JScrollPane getJLogWindowPane();

    /**
     * The component that manages the mouse.
     * 
     * @return the MouseListener
     */
    public MouseListener getMouseListener();

    /**
     * @return the preparationProgressBar
     */
    public JProgressBar getPreparationProgressBar();

    /**
     * @return the uploadProgressBar
     */
    public JProgressBar getUploadProgressBar();

    /**
     * @return the removeAllButton
     */
    public JButton getRemoveAllButton();

    /**
     * @return the removeButton
     */
    public JButton getRemoveButton();

    /**
     * @return the statusLabel
     */
    public JLabel getStatusLabel();

    /**
     * @return the stopButton
     */
    public JButton getStopButton();

    /**
     * @see JPanel#getTransferHandler()
     * 
     * @return The TransfertHandler
     */
    public TransferHandler getTransferHandler();

    /**
     * @return the uploadButton
     */
    public JButton getUploadButton();

    /**
     * This method opens the popup menu, if the mouseEvent is relevant. In this
     * case it returns true. Otherwise, it does nothing and returns false.
     * 
     * @param mouseEvent The triggered mouse event.
     * @return true if the popup menu was opened, false otherwise.
     */
    public boolean maybeOpenPopupMenu(MouseEvent mouseEvent);

    /**
     * Standard setter for filePanel.
     * 
     * @param filePanel
     */
    public void setFilePanel(FilePanel filePanel);

    /**
     * @return the fileUploadManagerThread
     */
    public FileUploadManagerThread getFileUploadManagerThread();


    /**
     * External interface
     */
    public void doSelectFiles();
    public String getProgressInfoJSON();
    public boolean isUploadFinished();

}