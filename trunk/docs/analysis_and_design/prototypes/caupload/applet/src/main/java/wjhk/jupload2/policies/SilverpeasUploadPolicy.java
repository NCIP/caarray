// $Id: SilverpeasUploadPolicy.java 143  2010-01-27 11:01:06 +0100 (mer. 27 janv. 2010) ehsavoie $
//
// jupload - A file upload applet.
// Copyright 2010 The JUpload Team
//
// Created: 2010-05-14
// Creator: ehsavoie
// Last modified: $Date: 2010-01-27 11:01:06 +0100 (mer. 27 janv. 2010) $
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.dnd.DropTargetDropEvent;
import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import wjhk.jupload2.context.JUploadContext;
import wjhk.jupload2.exception.JUploadException;
import wjhk.jupload2.exception.JUploadExceptionTooBigFile;
import wjhk.jupload2.gui.JUploadPanel;

/**
 * Personnalised Policy with no button : use only DragAndDrop or Copy/Paste with automatic upload.<br/>
 * This is given as an example of a personnalized policy to show how JUpload is easy to adapt to your
 * specific needs.
 * <ul>
 *   <li>support for updating the background color.</li>
 *   <li>load an HTML page to be displayed as a message into the Applet: allows a 'richer' label.</li>
 *   <li>pre-validation of files size (when they are added, instead of before upload).</li>
 * </ul>
 * @author ehsavoie
 */
public class SilverpeasUploadPolicy extends DefaultUploadPolicy {

  /** */
  public static final String MESSAGE_URL = "message";
  /** */
  public static final String COLOR_RED = "bgcolor_r";
  /** */
  public static final String COLOR_BLUE = "bgcolor_b";
  /** */
  public static final String COLOR_GREEN = "bgcolor_g";
  /** */
  public static final String COLOR_ALPHA = "bgcolor_a";

  /**
   * The JUpload constructor for this upload policy. Like all upload policies,
   * this constructor is called by the {@link UploadPolicyFactory}
   *
   * @param juploadContext
   * @throws JUploadException
   */
  public SilverpeasUploadPolicy(JUploadContext juploadContext)
          throws JUploadException {
    super(juploadContext);
    setNbFilesPerRequest(20);
    displayInfo("Loading SilverpeasUploadPolicy ...");
  }

  /**
   * This methods allow the upload policy to override the default disposition
   * of the components on the applet.
   *
   * @see UploadPolicy#addComponentsToJUploadPanel(JUploadPanel)
   */
  @Override
  public void addComponentsToJUploadPanel(JUploadPanel jUploadPanel) {
    // Set the global layout of the panel.
    displayInfo("Displaying SilverpeasUploadPolicy ...");
    jUploadPanel.getJComponent().setLayout(new BoxLayout(jUploadPanel.getJComponent(),
            BoxLayout.Y_AXIS));
    displayInfo("Accessing the message " + getMessage());
    Color color = new Color(getContext().getParameter(COLOR_RED, Color.LIGHT_GRAY.getRed()),
            getContext().getParameter(COLOR_GREEN, Color.LIGHT_GRAY.getGreen()),
            getContext().getParameter(COLOR_BLUE, Color.LIGHT_GRAY.getBlue()),
            getContext().getParameter(COLOR_ALPHA, Color.LIGHT_GRAY.getAlpha()));
    jUploadPanel.getJComponent().setBackground(color);
    jUploadPanel.getStatusLabel().setBackground(color);
    jUploadPanel.getUploadProgressBar().setBackground(color);

    jUploadPanel.getStatusLabel().setText(getMessage());
    jUploadPanel.getStatusLabel().setHorizontalAlignment(JLabel.CENTER);
    jUploadPanel.getStatusLabel().setVerticalAlignment(JLabel.CENTER);
    Dimension appletSize = getContext().getApplet().getSize();
    Dimension preferredAppletSize = getContext().getApplet().getPreferredSize();
    Dimension maxAppletSize = getContext().getApplet().getMaximumSize();
    jUploadPanel.getStatusLabel().setAlignmentX(Component.CENTER_ALIGNMENT);
    jUploadPanel.getStatusLabel().setAlignmentY(Component.CENTER_ALIGNMENT);
    jUploadPanel.getStatusLabel().setPreferredSize(new Dimension(preferredAppletSize.width, preferredAppletSize.height
            - 25));
    jUploadPanel.getStatusLabel().setSize(new Dimension(appletSize.width, appletSize.height - 25));
    jUploadPanel.getStatusLabel().setMaximumSize(new Dimension(maxAppletSize.width, maxAppletSize.height
            - 25));
    jUploadPanel.getStatusLabel().setMinimumSize(new Dimension(appletSize.width, appletSize.height
            - 25));
    jUploadPanel.getStatusLabel().setBackground(new Color(DEFAULT_ALBUM_ID));
    jUploadPanel.getJComponent().add(jUploadPanel.getStatusLabel());
    jUploadPanel.getJComponent().setAlignmentX(Component.CENTER_ALIGNMENT);
    jUploadPanel.getUploadProgressBar().setAlignmentX(Component.CENTER_ALIGNMENT);
    jUploadPanel.getUploadProgressBar().setAlignmentY(Component.CENTER_ALIGNMENT);
    jUploadPanel.getUploadProgressBar().setPreferredSize(
            new Dimension(preferredAppletSize.width, 20));
    jUploadPanel.getUploadProgressBar().setSize(new Dimension(appletSize.width, 20));
    jUploadPanel.getUploadProgressBar().setMaximumSize(new Dimension(maxAppletSize.width, 20));
    jUploadPanel.getUploadProgressBar().setMinimumSize(new Dimension(appletSize.width, 20));
    jUploadPanel.getUploadProgressBar().setStringPainted(false);
    jUploadPanel.getUploadProgressBar().setBorderPainted(false);
    // Then, add on the screen of the only component that is visible.
    jUploadPanel.getJComponent().add(jUploadPanel.getUploadProgressBar());
    // Now, we add the log window.
    jUploadPanel.showOrHideLogWindow();
    jUploadPanel.getJComponent().add(jUploadPanel.getJLogWindowPane());
    displayInfo("Displaying SilverpeasUploadPolicy ...");
  }

  /**
   * Loads the message from the specified URL.
   * @return the remote message.
   */
  protected String getMessage() {
    displayInfo("The message to be displayed is " + juploadContext.getParameter(MESSAGE_URL, ""));
    String urlContent = juploadContext.getParameter(MESSAGE_URL, "");
    String content = urlContent;
    HttpURLConnection connection = null;
    BufferedReader in = null;
    try {
      URL url = new URL(urlContent);
      connection = (HttpURLConnection) url.openConnection();
      connection.connect();
      in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
      displayInfo("Getting data ...");
      CharArrayWriter text = new CharArrayWriter();
      int size = 0;
      char[] buffer = new char[8];
      while ((size = in.read(buffer)) >= 0) {
        text.write(buffer, 0, size);
      }
      content = text.toString();
    } catch (IOException ioex) {
      displayErr(ioex);
    } finally {
      try {
        if (in != null) {
          in.close();
        }
      } catch (IOException ex) {
        displayErr(ex);
      }
      connection.disconnect();
    }
    return content;
  }

  /**
   * Default reaction after a successful drop operation: no action.
   *
   * @see UploadPolicy#afterFileDropped(DropTargetDropEvent)
   */
  @Override
  public void afterFileDropped(DropTargetDropEvent dropEvent) {
    getContext().getUploadPanel().doStartUpload();
  }

  /**
   * @param description
   * @see UploadPolicy#sendDebugInformation(String, Exception) */
  @Override
  public void sendDebugInformation(String description, Exception exception) {
    if (exception instanceof JUploadExceptionTooBigFile) {
       getContext().getUploadPanel().getFilePanel().removeAll();
    }
    super.sendDebugInformation(description, exception);
  }
}