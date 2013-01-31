//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
import java.io.*;

class Test
{
   public static void main(String[] args) throws Exception
   {
      Reader fr = null;
      if (args.length == 2)
         fr = new InputStreamReader(new FileInputStream(new File(args[0])), args[1]);
      else
         fr = new InputStreamReader(new FileInputStream(new File(args[0])));
      JavaParser jp = new JavaParser(fr);
      jp.CompilationUnit();
   }
}
