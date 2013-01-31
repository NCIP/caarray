//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package caarray.legacy.client.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author vaughng
 * Jul 31, 2009
 */
public class TestUtils
{

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
