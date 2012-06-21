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

import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.TransferHandler;

import org.apache.log4j.Logger;

import wjhk.jupload2.gui.DnDListener;
import wjhk.jupload2.gui.JUploadPanel;
import wjhk.jupload2.gui.filepanel.FilePanel;
import wjhk.jupload2.upload.FileUploadManagerThread;

/**
 * @author etienne_sf
 */
public class JUploadPanelTestHelper implements JUploadPanel {
	/** Logger for this class */
	protected final Logger logger = Logger.getLogger(getClass());
	/** */
	public FilePanel filePanel = null;
	/** */
	public FileUploadManagerThread fileUploadManagerThread = null;
	/** */
	public JProgressBar preparationProgressBar = new JProgressBar();
	/** */
	public JProgressBar uploadProgressBar = new JProgressBar();
	/** */
	public JLabel statusLabel = new JLabel("JUpload applet (in JUnit tests)");

	/**
	 * @param filePanel
	 */
	public JUploadPanelTestHelper(FilePanel filePanel) {
		this.filePanel = filePanel;
	}

	/**
	 * @see wjhk.jupload2.gui.JUploadPanel#clearLogWindow()
	 */
	public void clearLogWindow() {
		throw new UnsupportedOperationException(this.getClass()
				+ ".clearLogWindow() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.gui.JUploadPanel#copyLogWindow()
	 */
	public void copyLogWindow() {
		throw new UnsupportedOperationException(this.getClass()
				+ ".copyLogWindow() is not implemented in tests cases");
	}

    public void doSelectFiles() {
    }

	/**
	 * @see wjhk.jupload2.gui.JUploadPanel#doBrowse()
	 */
	public void doBrowse() {
		throw new UnsupportedOperationException(this.getClass()
				+ ".doBrowse() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.gui.JUploadPanel#doRemove()
	 */
	public void doRemove() {
		throw new UnsupportedOperationException(this.getClass()
				+ ".doRemove() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.gui.JUploadPanel#doRemoveAll()
	 */
	public void doRemoveAll() {
		throw new UnsupportedOperationException(this.getClass()
				+ ".doRemoveAll() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.gui.JUploadPanel#doStartUpload()
	 */
	public void doStartUpload() {
		throw new UnsupportedOperationException(this.getClass()
				+ ".doStartUpload() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.gui.JUploadPanel#doStopUpload()
	 */
	public void doStopUpload() {
		throw new UnsupportedOperationException(this.getClass()
				+ ".doStopUpload() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.gui.JUploadPanel#getActionListener()
	 */
	public ActionListener getActionListener() {
		throw new UnsupportedOperationException(this.getClass()
				+ ".getActionListener() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.gui.JUploadPanel#getBrowseButton()
	 */
	public JButton getBrowseButton() {
		throw new UnsupportedOperationException(this.getClass()
				+ ".getBrowseButton() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.gui.JUploadPanel#getDndListener()
	 */
	public DnDListener getDndListener() {
		throw new UnsupportedOperationException(this.getClass()
				+ ".getDndListener() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.gui.JUploadPanel#getFilePanel()
	 */
	public FilePanel getFilePanel() {
		return this.filePanel;
	}

	/**
	 * @see wjhk.jupload2.gui.JUploadPanel#getFileUploadManagerThread()
	 */
	public FileUploadManagerThread getFileUploadManagerThread() {
		return this.fileUploadManagerThread;
	}

	/**
	 * @see wjhk.jupload2.gui.JUploadPanel#getJComponent()
	 */
	public JComponent getJComponent() {
		throw new UnsupportedOperationException(this.getClass()
				+ ".getJComponent() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.gui.JUploadPanel#getJLogWindowPane()
	 */
	public JScrollPane getJLogWindowPane() {
		throw new UnsupportedOperationException(this.getClass()
				+ ".getJLogWindowPane() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.gui.JUploadPanel#getMouseListener()
	 */
	public MouseListener getMouseListener() {
		throw new UnsupportedOperationException(this.getClass()
				+ ".getMouseListener() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.gui.JUploadPanel#getPreparationProgressBar()
	 */
	public JProgressBar getPreparationProgressBar() {
		return this.preparationProgressBar;
	}

	/**
	 * @see wjhk.jupload2.gui.JUploadPanel#getRemoveAllButton()
	 */
	public JButton getRemoveAllButton() {
		throw new UnsupportedOperationException(this.getClass()
				+ ".getRemoveAllButton() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.gui.JUploadPanel#getRemoveButton()
	 */
	public JButton getRemoveButton() {
		throw new UnsupportedOperationException(this.getClass()
				+ ".getRemoveButton() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.gui.JUploadPanel#getStatusLabel()
	 */
	public JLabel getStatusLabel() {
		return this.statusLabel;
	}

	/**
	 * @see wjhk.jupload2.gui.JUploadPanel#getStopButton()
	 */
	public JButton getStopButton() {
		throw new UnsupportedOperationException(this.getClass()
				+ ".getStopButton() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.gui.JUploadPanel#getTransferHandler()
	 */
	public TransferHandler getTransferHandler() {
		throw new UnsupportedOperationException(this.getClass()
				+ ".getTransferHandler() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.gui.JUploadPanel#getUploadButton()
	 */
	public JButton getUploadButton() {
		throw new UnsupportedOperationException(this.getClass()
				+ ".getUploadButton() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.gui.JUploadPanel#getUploadProgressBar()
	 */
	public JProgressBar getUploadProgressBar() {
		return this.uploadProgressBar;
	}

	/**
	 * @see wjhk.jupload2.gui.JUploadPanel#maybeOpenPopupMenu(java.awt.event.MouseEvent)
	 */
	public boolean maybeOpenPopupMenu(MouseEvent mouseEvent) {
		throw new UnsupportedOperationException(this.getClass()
				+ ".maybeOpenPopupMenu() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.gui.JUploadPanel#setFilePanel(wjhk.jupload2.gui.filepanel.FilePanel)
	 */
	public void setFilePanel(FilePanel filePanel) {
		throw new UnsupportedOperationException(this.getClass()
				+ ".setFilePanel() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.gui.JUploadPanel#showOrHideLogWindow()
	 */
	public void showOrHideLogWindow() {
		this.logger.warn(this.getClass()
				+ ".showOrHideLogWindow() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.gui.JUploadPanel#updateButtonState()
	 */
	public void updateButtonState() {
		this.logger.warn(this.getClass()
				+ ".updateButtonState() is not implemented in tests cases");
	}


	@Override
    public String getProgressInfoJSON() {
        return "";
    }

    @Override
    public boolean isUploadFinished() {
        return false;
    }

}
