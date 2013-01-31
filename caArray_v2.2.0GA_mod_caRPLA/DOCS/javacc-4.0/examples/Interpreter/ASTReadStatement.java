//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================

/*
 * Copyright © 2002 Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * California 95054, U.S.A. All rights reserved.  Sun Microsystems, Inc. has
 * intellectual property rights relating to technology embodied in the product
 * that is described in this document. In particular, and without limitation,
 * these intellectual property rights may include one or more of the U.S.
 * patents listed at http://www.sun.com/patents and one or more additional
 * patents or pending patent applications in the U.S. and in other countries.
 * U.S. Government Rights - Commercial software. Government users are subject
 * to the Sun Microsystems, Inc. standard license agreement and applicable
 * provisions of the FAR and its supplements.  Use is subject to license terms.
 * Sun,  Sun Microsystems,  the Sun logo and  Java are trademarks or registered
 * trademarks of Sun Microsystems, Inc. in the U.S. and other countries.  This
 * product is covered and controlled by U.S. Export Control laws and may be
 * subject to the export or import laws in other countries.  Nuclear, missile,
 * chemical biological weapons or nuclear maritime end uses or end users,
 * whether direct or indirect, are strictly prohibited.  Export or reexport
 * to countries subject to U.S. embargo or to entities identified on U.S.
 * export exclusion lists, including, but not limited to, the denied persons
 * and specially designated nationals lists is strictly prohibited.
 */


/* JJT: 0.2.2 */




public class ASTReadStatement extends SimpleNode {
  String name;

  ASTReadStatement(int id) {
    super(id);
  }


  public void interpret()
  {
     Object o;
     byte[] b = new byte[64];
     int i;

     if ((o = symtab.get(name)) == null)
        System.err.println("Undefined variable : " + name);

     if (o instanceof Boolean)
     {
        System.out.print("Enter a value for \'" + name + "\' (boolean) : ");
        System.out.flush();
        try
        {
           i = System.in.read(b);
           symtab.put(name, new Boolean((new String(b, 0, 0, i - 1)).trim()));
        } catch(Exception e) { System.exit(1); }
     }
     else if (o instanceof Integer)
     {
        System.out.print("Enter a value for \'" + name + "\' (int) : ");
        System.out.flush();
        try
        {
           i = System.in.read(b);
           symtab.put(name, new Integer((new String(b, 0, 0, i - 1)).trim()));
        } catch(Exception e) {
           System.out.println("Exceptio : " + e.getClass().getName());
           System.exit(1);
        }
     }
  }
}
