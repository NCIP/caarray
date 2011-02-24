<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-template" prefix="template"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested" prefix="nested"%>

<tr>
                <td width="100%" valign="top">
                  
                  <!-- target of anchor to skip menus --><a name="content" />
                  
                  <table summary="" cellpadding="0" cellspacing="0" border="0" width="600" height="100%">
                    
                    <!-- banner begins -->
                    <tr>
                      <td class="bannerHome"><img src="images/bannerHome.gif" height="140"></td>
                    </tr>
                    <!-- banner begins -->
                    
                    <tr>
                      <td height="100%">
                        <!-- target of anchor to skip menus --><a name="content" />
                        <table summary="" cellpadding="0" cellspacing="0" border="0" height="100%">
                          <tr>
                            <td>
                            
                              <!-- welcome begins -->
                              <table summary="" cellpadding="0" cellspacing="0" border="0" width="390" height="100%">
                                <tr><td class="welcomeTitle" height="20"><bean:message key="label.home_welcome_message_header" /></td>
                                </tr>
                                <tr>
                                  <td class="welcomeContent" valign="top">
                                    <bean:message key="label.home_welcome_message" />
                                  </td>
                                </tr>
                              </table>
                              <!-- welcome ends -->
                            
                            </td>
                            <td valign="top">
                              
                              <!-- sidebar begins -->
                              <table summary="" cellpadding="0" cellspacing="0" border="0" height="100%">
                                
                                <!-- login begins -->
                                
                                <!-- login ends -->
                                
                                <!-- what's new begins -->
                                <tr>
                                  <td valign="top">
                                    <table summary="" cellpadding="0" cellspacing="0" border="0" width="100%" class="sidebarSection">
                                      <tr>
                                        <td class="sidebarTitle" height="20">WHAT'S NEW</td>
                                      </tr>
                                      <tr>
                                        <td class="sidebarContent">
                                       	<li> All new user interface! 
										<li>Query on Object ID 
										<li>Viewable Object attributes 
										<li>LLT now uses CSM for authentication and authorization 
 
                                        
										 </td>
                                      </tr>
                                    </table>
                                  </td>
                                </tr>
                                <!-- what's new ends -->
                                
                                <!-- did you know? begins -->
                                <tr>
                                  <td valign="top">
                                    <table summary="" cellpadding="0" cellspacing="0" border="0" width="100%" height="100%" class="sidebarSection">
                                    <tr>
                                      <td class="sidebarTitle" height="20">DID YOU KNOW?</td>
                                    </tr>
                                    <tr>
                                      <td class="sidebarContent" valign="top">
                                    <li>LLT can be customized to view the logs from any application you are developing. 
									<li>Given proper permissions, you can view log messages from other servers. 
									<li>You can view log message soon after they occur by providing a future End Date, and resubmitting the search. 
                                      </td>
                                    </tr>
                                    </table>
                                  </td>
                                </tr>
                                <!-- did you know? ends -->
                                
                                <!-- spacer cell begins (keep for dynamic expanding) -->
                                <tr><td valign="top" height="100%">
                                    <table summary="" cellpadding="0" cellspacing="0" border="0" width="100%" height="100%" class="sidebarSection">
                                    <tr>
                                      <td class="sidebarContent" valign="top">&nbsp;</td>
                                    </tr>
                                    </table>
																</td></tr>
                                <!-- spacer cell ends -->
																
                              </table>
                              <!-- sidebar ends -->
                              
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>