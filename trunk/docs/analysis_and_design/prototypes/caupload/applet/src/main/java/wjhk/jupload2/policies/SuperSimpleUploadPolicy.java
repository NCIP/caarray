//
// $Id: DefaultUploadPolicy.java 289 2007-06-19 10:04:46 +0000 (mar., 19 juin
// 2007) etienne_sf $
//
// jupload - A file upload juploadContext.
// Copyright 2007 The JUpload Team
//
// Created: 2006-05-04
// Creator: etienne_sf
// Last modified: $Date: 2010-02-12 18:25:00 +0100 (ven, 12 Feb 2010) $
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

package wjhk.jupload2.policies;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.awt.dnd.DropTarget;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import wjhk.jupload2.context.JUploadContext;
import wjhk.jupload2.exception.JUploadException;
import wjhk.jupload2.gui.JUploadPanel;

/**
 * A SuperSimpleUploadPolicy - see
 * https://sourceforge.net/tracker/?func=detail&atid
 * =490055&aid=2954497&group_id=59144
 * 
 * @author nordfalk
 * @version $Revision: 978 $
 */

public class SuperSimpleUploadPolicy extends DefaultUploadPolicy {

	/**
	 * The main constructor : use default values, and the given postURL.
	 * 
	 * @param juploadContext
	 *            The current juploadContext. As the reference to the current
	 *            upload policy exists almost everywhere, this parameter allows
	 *            any access to anyone on the juploadContext... including
	 *            reading the applet parameters.
	 * @throws JUploadException
	 *             If an applet parameter is invalid
	 */
	public SuperSimpleUploadPolicy(JUploadContext juploadContext)
			throws JUploadException {
		super(juploadContext);
	}

	/**
	 * Implementation of
	 * {@link wjhk.jupload2.policies.UploadPolicy#createTopPanel(JButton, JButton, JButton, JUploadPanel)}
	 * 
	 * @see wjhk.jupload2.policies.UploadPolicy#createTopPanel(JButton, JButton,
	 *      JButton, JUploadPanel)
	 */
	public JPanel createTopPanel(JButton browse, JButton remove,
			JButton removeAll, JUploadPanel jUploadPanel) {
		JPanel jPanel = new JPanel();

		jPanel.setLayout(new GridLayout(1, 3, 10, 5));
		jPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		jPanel.add(browse);
		// jPanel.add(removeAll);
		jPanel.add(remove);
		remove.setVisible(false); // will appear after first file has been added

		jUploadPanel.getJComponent().setBorder(
				BorderFactory.createLineBorder(SystemColor.controlDkShadow));

		jPanel.setBackground(Color.WHITE);

		return jPanel;
	}

	JPanel dndFilesHere;
	JPanel progressPanel;
	JPanel statusBar;

	/**
	 * This methods allow the upload policy to override the default disposition
	 * of the components on the applet.
	 * 
	 * @see UploadPolicy#addComponentsToJUploadPanel(JUploadPanel)
	 */
	public void addComponentsToJUploadPanel(JUploadPanel jUploadPanel) {
		// Set the global layout of the panel.
		jUploadPanel.getJComponent().setLayout(
				new BoxLayout(jUploadPanel.getJComponent(), BoxLayout.Y_AXIS));

		// The top panel is the upper part of the applet: above the file
		// list.
		// JPanel topPanel = new JPanel();
		JPanel topPanel = createTopPanel(jUploadPanel.getBrowseButton(),
				jUploadPanel.getRemoveButton(), jUploadPanel
						.getRemoveAllButton(), jUploadPanel);
		if (topPanel != null) {
			jUploadPanel.getJComponent().add(topPanel);
			topPanel.addMouseListener(jUploadPanel.getMouseListener());
		}

		Component c = jUploadPanel.getFilePanel().getDropComponent();
		// Then, we add the file list.
		jUploadPanel.getJComponent().add(c);
		c.setVisible(false);

		c.setBackground(Color.WHITE);

		dndFilesHere = new JPanel();
		new DropTarget(this.dndFilesHere, jUploadPanel.getDndListener());

		dndFilesHere.setLayout(new BorderLayout());
		dndFilesHere.setBackground(Color.WHITE);
		JLabel jlabel = new JLabel(
				getLocalizedString("dragDirectoriesAndFilesToHere"));
		jlabel.setFont(jlabel.getFont().deriveFont(36f));
		jlabel.setHorizontalAlignment(SwingConstants.CENTER);
		dndFilesHere.add(jlabel, BorderLayout.CENTER);
		jUploadPanel.getJComponent().add(dndFilesHere);

		// The progress panel contains the progress bar, and the upload and stop
		// buttons.
		progressPanel = createProgressPanel(jUploadPanel
				.getPreparationProgressBar(), jUploadPanel
				.getUploadProgressBar(), jUploadPanel.getUploadButton(),
				jUploadPanel.getStopButton(), jUploadPanel);
		jUploadPanel.getJComponent().add(progressPanel);
		jUploadPanel.getJComponent().addMouseListener(
				jUploadPanel.getMouseListener());

		progressPanel.setVisible(false); // will appear after 1st file drop
		progressPanel.setBackground(Color.WHITE);

		// Now, we add the log window.
		jUploadPanel.showOrHideLogWindow();
		jUploadPanel.getJComponent().add(jUploadPanel.getJLogWindowPane());

		// And, to finish with: the status bar.
		statusBar = createStatusBar(jUploadPanel.getStatusLabel(), jUploadPanel);
		if (null != statusBar) {
			jUploadPanel.getJComponent().add(statusBar);
			statusBar.addMouseListener(jUploadPanel.getMouseListener());

			statusBar.setVisible(false);
			statusBar.setBackground(Color.WHITE);
			jUploadPanel.getStatusLabel().setText(" ");
		}
	}

	/** {@inheritDoc} */
	public void updateButtonState(int executionStatus) {
		super.updateButtonState(executionStatus);
		if (executionStatus == UploadPolicy.EXEC_STATUS_READY) {
			this.juploadContext.getUploadPanel().getRemoveButton().setVisible(
					true);
			this.dndFilesHere.setVisible(false);
			this.progressPanel.setVisible(true);
			this.statusBar.setVisible(true);
			this.juploadContext.getUploadPanel().getFilePanel()
					.getDropComponent().setVisible(true);
		} else if (executionStatus == UploadPolicy.EXEC_STATUS_UPLOADING) {
			this.juploadContext.getUploadPanel().getRemoveButton().setVisible(
					true);
			this.dndFilesHere.setVisible(false);
			this.progressPanel.setVisible(true);
			this.statusBar.setVisible(true);
			this.juploadContext.getUploadPanel().getFilePanel()
					.getDropComponent().setVisible(true);
		}

	}

}
