/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarrayGridClientExamples_v1_0
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarrayGridClientExamples_v1_0 Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarrayGridClientExamples_v1_0 Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarrayGridClientExamples_v1_0 Software; (ii) distribute and
 * have distributed to and by third parties the caarrayGridClientExamples_v1_0 Software and any
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
package caarray.client.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * General purpose utility methods.
 * 
 * @author vaughng
 * Jun 26, 2009
 */
public class TestUtils {

	/**
	   * Splits an input string around a delimiter, ignoring the delimiter
	   * if it appears inside quotes.
	   * 
	   * @param input String to be split into an array.
	   * @param delimiter Regular expression, as a String, about which the input will be split.
	   * @return An array of Strings split around a delimiter, with delimiters inside quoted text ignored.
	   */
	  public static String[] split(String input, String delimiter)
	  {
	      
	    if (input == null)
	      return null;
	    
	    String[] delimited;
	    
	    if (input.indexOf("\"") < 0)
	    {
	        delimited = input.split(delimiter);
	        return removeNulls(delimited);  
	    }
	      
	    
	    //Quoted text containing at least one comma
	    String quotePattern = "\"[^\"]*" + delimiter + "[^\"]*\"";
	    String substitute = "%delim%";
	    
	    Pattern pattern = Pattern.compile(quotePattern);
	    Matcher matcher = pattern.matcher(input);
	    StringBuffer replacement = new StringBuffer();
	    
	    //Replace all commas inside quoted text so they will be ignored in the split operation
	    while (matcher.find())
	    {
	      String group = matcher.group();
	      group = group.replaceAll(delimiter,substitute);
	      matcher.appendReplacement(replacement,group);
	    }
	    matcher.appendTail(replacement);
	    
	    //Remove quotes, and split the substituted input around the delimiter
	    input = replacement.toString();
	    String boundryQuotePattern = "\"" + delimiter + "\"";
	    input = input.replaceAll(boundryQuotePattern,"");
	    delimited = input.split(delimiter);
	    
	    //Replace instances of the delimiter substitution with the delimiter
	    for (int i = 0; i < delimited.length; i++)
	    {
	      delimited[i] = delimited[i].replaceAll(substitute,delimiter);
	    }
	    
	    return removeNulls(delimited);
	  }
	  
	  /**
	   * Converts an array of Strings into a single String, with the tokens separated by the delimiting string.
	   * Null tokens and delimiters will be converted to the empty string, and a null array will be returned
	   * as null.
	   * 
	   * @param tokens Array of string tokens to be concatenated.
	   * @param delimiter String used to separate each token in the resulting string.
	   * @return The String that results from concatenating each string token around the delimiter.
	   */
	  public static String delimit(String[] tokens, String delimiter)
	  {
	    String retVal = null;
	    
	    if (tokens != null)
	    {
	      retVal = "";
	      if (delimiter == null)
	        delimiter = "";
	      
	      for (int i = 0; i < tokens.length; i++)
	      {
	        if (tokens[i] == null)
	          tokens[i] = "";
	        
	        if (tokens[i].indexOf(",") >= 0 && !tokens[i].startsWith("\""))
	          tokens[i] = "\"" + tokens[i] + "\"";
	        
	        retVal = retVal.concat(tokens[i]);
	        if (i < tokens.length - 1)
	        {
	          retVal = retVal.concat(delimiter);
	        }
	      }
	    }
	    
	    return retVal;
	  }
	  
	  /**
	   * Returns the result of replacing any null values in the given array with
	   * the empty string.
	   * 
	   * @param arg Array in which nulls will be removed.
	   * @return An array containing empty strings in place of null values.
	   */
	  public static String[] removeNulls(String[] arg)
	  {
	      if (arg == null)
	          return null;
	      String[] retVal = new String[arg.length];
	      System.arraycopy(arg, 0, retVal, 0, arg.length);
	      for (int i = 0; i < retVal.length; i++)
	      {
	          if (retVal[i] == null)
	              retVal[i] = "";
	      }
	      return retVal;
	  }
}
