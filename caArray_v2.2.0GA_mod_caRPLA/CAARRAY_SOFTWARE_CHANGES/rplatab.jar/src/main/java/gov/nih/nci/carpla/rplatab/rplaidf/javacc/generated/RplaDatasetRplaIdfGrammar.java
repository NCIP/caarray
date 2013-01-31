//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.carpla.rplatab.rplaidf.javacc.generated;
import java.io.*;
import java.util.*;
import gov.nih.nci.carpla.rplatab.rplaidf.*;
import gov.nih.nci.carpla.rplatab.rplaidf.javacc.src.*;

public class RplaDatasetRplaIdfGrammar implements RplaDatasetRplaIdfGrammarConstants {


        private RplaIdfHelper   _helper = null;

        private Token                   _header  = null ;
        private PrintStream     _ps = null;

        public void setOutputStream(PrintStream ps){
        _ps = ps;
        }

  final public RplaIdfHelper parse() throws ParseException {
                       _helper = new RplaIdfHelper();
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
      case Comment:
        ;
        break;
      default:
        jj_la1[0] = jj_gen;
        break label_1;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
        jj_consume_token(CR_LF);
        break;
      case Comment:
        jj_consume_token(Comment);
        break;
      default:
        jj_la1[1] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    investigationTitle();
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
      case Comment:
        ;
        break;
      default:
        jj_la1[2] = jj_gen;
        break label_2;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
        jj_consume_token(CR_LF);
        break;
      case Comment:
        jj_consume_token(Comment);
        break;
      default:
        jj_la1[3] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    experimentalDesign();
    label_3:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
      case Comment:
        ;
        break;
      default:
        jj_la1[4] = jj_gen;
        break label_3;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
        jj_consume_token(CR_LF);
        break;
      case Comment:
        jj_consume_token(Comment);
        break;
      default:
        jj_la1[5] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    experimentalFactor();
    label_4:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
      case Comment:
        ;
        break;
      default:
        jj_la1[6] = jj_gen;
        break label_4;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
        jj_consume_token(CR_LF);
        break;
      case Comment:
        jj_consume_token(Comment);
        break;
      default:
        jj_la1[7] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    antibodyName();
    label_5:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
      case Comment:
        ;
        break;
      default:
        jj_la1[8] = jj_gen;
        break label_5;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
        jj_consume_token(CR_LF);
        break;
      case Comment:
        jj_consume_token(Comment);
        break;
      default:
        jj_la1[9] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    antibodyTargetGeneName();
    label_6:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
      case Comment:
        ;
        break;
      default:
        jj_la1[10] = jj_gen;
        break label_6;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
        jj_consume_token(CR_LF);
        break;
      case Comment:
        jj_consume_token(Comment);
        break;
      default:
        jj_la1[11] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    antibodyTargetGeneNameTermSourceRef();
    label_7:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
      case Comment:
        ;
        break;
      default:
        jj_la1[12] = jj_gen;
        break label_7;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
        jj_consume_token(CR_LF);
        break;
      case Comment:
        jj_consume_token(Comment);
        break;
      default:
        jj_la1[13] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    antibodySpecificity();
    label_8:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
      case Comment:
        ;
        break;
      default:
        jj_la1[14] = jj_gen;
        break label_8;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
        jj_consume_token(CR_LF);
        break;
      case Comment:
        jj_consume_token(Comment);
        break;
      default:
        jj_la1[15] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    antibodyEpitope();
    label_9:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
      case Comment:
        ;
        break;
      default:
        jj_la1[16] = jj_gen;
        break label_9;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
        jj_consume_token(CR_LF);
        break;
      case Comment:
        jj_consume_token(Comment);
        break;
      default:
        jj_la1[17] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    antibodyImmunogen();
    label_10:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
      case Comment:
        ;
        break;
      default:
        jj_la1[18] = jj_gen;
        break label_10;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
        jj_consume_token(CR_LF);
        break;
      case Comment:
        jj_consume_token(Comment);
        break;
      default:
        jj_la1[19] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    antibodyProvider();
    label_11:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
      case Comment:
        ;
        break;
      default:
        jj_la1[20] = jj_gen;
        break label_11;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
        jj_consume_token(CR_LF);
        break;
      case Comment:
        jj_consume_token(Comment);
        break;
      default:
        jj_la1[21] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    antibodyCatalogID();
    label_12:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
      case Comment:
        ;
        break;
      default:
        jj_la1[22] = jj_gen;
        break label_12;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
        jj_consume_token(CR_LF);
        break;
      case Comment:
        jj_consume_token(Comment);
        break;
      default:
        jj_la1[23] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    antibodyLotID();
    label_13:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
      case Comment:
        ;
        break;
      default:
        jj_la1[24] = jj_gen;
        break label_13;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
        jj_consume_token(CR_LF);
        break;
      case Comment:
        jj_consume_token(Comment);
        break;
      default:
        jj_la1[25] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    antibodyComment();
    label_14:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
      case Comment:
        ;
        break;
      default:
        jj_la1[26] = jj_gen;
        break label_14;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
        jj_consume_token(CR_LF);
        break;
      case Comment:
        jj_consume_token(Comment);
        break;
      default:
        jj_la1[27] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    label_15:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
      case Comment:
        ;
        break;
      default:
        jj_la1[28] = jj_gen;
        break label_15;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
        jj_consume_token(CR_LF);
        break;
      case Comment:
        jj_consume_token(Comment);
        break;
      default:
        jj_la1[29] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    rplArrayName();
    label_16:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
      case Comment:
        ;
        break;
      default:
        jj_la1[30] = jj_gen;
        break label_16;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
        jj_consume_token(CR_LF);
        break;
      case Comment:
        jj_consume_token(Comment);
        break;
      default:
        jj_la1[31] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case Dilution:
      dilution();
      label_17:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case CR_LF:
        case Comment:
          ;
          break;
        default:
          jj_la1[32] = jj_gen;
          break label_17;
        }
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case CR_LF:
          jj_consume_token(CR_LF);
          break;
        case Comment:
          jj_consume_token(Comment);
          break;
        default:
          jj_la1[33] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
      }
      dilutionValue();
      label_18:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case CR_LF:
        case Comment:
          ;
          break;
        default:
          jj_la1[34] = jj_gen;
          break label_18;
        }
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case CR_LF:
          jj_consume_token(CR_LF);
          break;
        case Comment:
          jj_consume_token(Comment);
          break;
        default:
          jj_la1[35] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
      }
      dilutionUnit();
      label_19:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case CR_LF:
        case Comment:
          ;
          break;
        default:
          jj_la1[36] = jj_gen;
          break label_19;
        }
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case CR_LF:
          jj_consume_token(CR_LF);
          break;
        case Comment:
          jj_consume_token(Comment);
          break;
        default:
          jj_la1[37] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
      }
      dilutionUnitTermSourceRef();
      break;
    default:
      jj_la1[38] = jj_gen;
      ;
    }
    label_20:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
      case Comment:
        ;
        break;
      default:
        jj_la1[39] = jj_gen;
        break label_20;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
        jj_consume_token(CR_LF);
        break;
      case Comment:
        jj_consume_token(Comment);
        break;
      default:
        jj_la1[40] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    person();
    label_21:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
      case Comment:
        ;
        break;
      default:
        jj_la1[41] = jj_gen;
        break label_21;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
        jj_consume_token(CR_LF);
        break;
      case Comment:
        jj_consume_token(Comment);
        break;
      default:
        jj_la1[42] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    qualityControl();
    label_22:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
      case Comment:
        ;
        break;
      default:
        jj_la1[43] = jj_gen;
        break label_22;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
        jj_consume_token(CR_LF);
        break;
      case Comment:
        jj_consume_token(Comment);
        break;
      default:
        jj_la1[44] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    replicate();
    label_23:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
      case Comment:
        ;
        break;
      default:
        jj_la1[45] = jj_gen;
        break label_23;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
        jj_consume_token(CR_LF);
        break;
      case Comment:
        jj_consume_token(Comment);
        break;
      default:
        jj_la1[46] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    normalization();
    label_24:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
      case Comment:
        ;
        break;
      default:
        jj_la1[47] = jj_gen;
        break label_24;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
        jj_consume_token(CR_LF);
        break;
      case Comment:
        jj_consume_token(Comment);
        break;
      default:
        jj_la1[48] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    dates();
    label_25:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
      case Comment:
        ;
        break;
      default:
        jj_la1[49] = jj_gen;
        break label_25;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
        jj_consume_token(CR_LF);
        break;
      case Comment:
        jj_consume_token(Comment);
        break;
      default:
        jj_la1[50] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    publication();
    label_26:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
      case Comment:
        ;
        break;
      default:
        jj_la1[51] = jj_gen;
        break label_26;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
        jj_consume_token(CR_LF);
        break;
      case Comment:
        jj_consume_token(Comment);
        break;
      default:
        jj_la1[52] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    experimentDescription();
    label_27:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
      case Comment:
        ;
        break;
      default:
        jj_la1[53] = jj_gen;
        break label_27;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
        jj_consume_token(CR_LF);
        break;
      case Comment:
        jj_consume_token(Comment);
        break;
      default:
        jj_la1[54] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    protocol();
    label_28:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
      case Comment:
        ;
        break;
      default:
        jj_la1[55] = jj_gen;
        break label_28;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
        jj_consume_token(CR_LF);
        break;
      case Comment:
        jj_consume_token(Comment);
        break;
      default:
        jj_la1[56] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    sradfFile();
    label_29:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
      case Comment:
        ;
        break;
      default:
        jj_la1[57] = jj_gen;
        break label_29;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
        jj_consume_token(CR_LF);
        break;
      case Comment:
        jj_consume_token(Comment);
        break;
      default:
        jj_la1[58] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    termSource();
    label_30:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
      case Comment:
        ;
        break;
      default:
        jj_la1[59] = jj_gen;
        break label_30;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CR_LF:
        jj_consume_token(CR_LF);
        break;
      case Comment:
        jj_consume_token(Comment);
        break;
      default:
        jj_la1[60] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
   {if (true) return _helper;}
    throw new Error("Missing return statement in function");
  }

  final public void antibodyTargetGeneName() throws ParseException {
                                _header= getToken(1);
    jj_consume_token(AntibodyTargetGeneName);
    label_31:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[61] = jj_gen;
        break label_31;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_32:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[62] = jj_gen;
        break label_32;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void antibodyTargetGeneNameTermSourceRef() throws ParseException {
                                             _header= getToken(1);
    jj_consume_token(AntibodyTargetGeneNameTermSourceRef);
    label_33:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[63] = jj_gen;
        break label_33;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_34:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[64] = jj_gen;
        break label_34;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void antibodySpecificity() throws ParseException {
                             _header= getToken(1);
    jj_consume_token(AntibodySpecificity);
    label_35:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[65] = jj_gen;
        break label_35;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_36:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[66] = jj_gen;
        break label_36;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void antibodyEpitope() throws ParseException {
                         _header= getToken(1);
    jj_consume_token(AntibodyEpitope);
    label_37:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[67] = jj_gen;
        break label_37;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_38:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[68] = jj_gen;
        break label_38;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void antibodyImmunogen() throws ParseException {
                           _header= getToken(1);
    jj_consume_token(AntibodyImmunogen);
    label_39:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[69] = jj_gen;
        break label_39;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_40:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[70] = jj_gen;
        break label_40;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void antibodyDataSheetFile() throws ParseException {
                               _header= getToken(1);
    jj_consume_token(AntibodyDataSheetFile);
    label_41:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[71] = jj_gen;
        break label_41;
      }
      jj_consume_token(TAB);
      readIt();
    }
    jj_consume_token(CR_LF);
  }

  final public void rplArrayName() throws ParseException {
                      _header= getToken(1);
    jj_consume_token(RPLArrayName);
    label_42:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[72] = jj_gen;
        break label_42;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_43:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[73] = jj_gen;
        break label_43;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public String readIt() throws ParseException {
                  Token head = null; Token tail = null;
    head = getToken(1);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case QUOTE:
      jj_consume_token(QUOTE);
      label_44:
      while (true) {
        jj_consume_token(QUOTED);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case QUOTED:
          ;
          break;
        default:
          jj_la1[74] = jj_gen;
          break label_44;
        }
      }
      jj_consume_token(QUOTE_END);
      break;
    case String:
      label_45:
      while (true) {
        jj_consume_token(String);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case String:
          ;
          break;
        default:
          jj_la1[75] = jj_gen;
          break label_45;
        }
      }
      break;
    default:
      jj_la1[76] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    TokenList tl = new TokenList(head, getToken(0));
    StringBuffer buffie = new StringBuffer();
    for (Token p = head; p != tail; p = p.next){
        if ( p.image.compareTo("\"") != 0 ) {
      buffie.append(p.image);
    }
    }
    String ret = "";
    ret = buffie.substring(0, buffie.length()).trim();
    //System.out.println("!!!@" + ret);
        _helper.addColumnString(_header.image, ret);
        {if (true) return (buffie.substring(0, buffie.length()).trim());}
    throw new Error("Missing return statement in function");
  }

  final public void investigationTitle() throws ParseException {
                           _header=getToken(1);
    jj_consume_token(InvestigationTitle);
    jj_consume_token(TAB);
    readIt();
    jj_consume_token(CR_LF);
  }

  final public void experimentalDesign() throws ParseException {
    experimentalDesignName();
    experimentalDesignTermSourceREF();
  }

  final public void experimentalDesignName() throws ParseException {
                               _header = getToken(1);
    jj_consume_token(ExperimentalDesign);
    label_46:
    while (true) {
      jj_consume_token(TAB);
      readIt();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[77] = jj_gen;
        break label_46;
      }
    }
    jj_consume_token(CR_LF);
  }

  final public void experimentalDesignTermSourceREF() throws ParseException {
                                        _header = getToken(1);
    jj_consume_token(ExperimentalDesignTermSourceRef);
    label_47:
    while (true) {
      jj_consume_token(TAB);
      readIt();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[78] = jj_gen;
        break label_47;
      }
    }
    label_48:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[79] = jj_gen;
        break label_48;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void experimentalFactor() throws ParseException {
    experimentalFactorName();
    experimentalFactorType();
    experimentalFactorTermSourceREF();
  }

  final public void experimentalFactorName() throws ParseException {
                               _header = getToken(1);
    jj_consume_token(ExperimentalFactorName);
    label_49:
    while (true) {
      jj_consume_token(TAB);
      readIt();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[80] = jj_gen;
        break label_49;
      }
    }
    jj_consume_token(CR_LF);
  }

  final public void experimentalFactorTermSourceREF() throws ParseException {
                                        _header = getToken(1);
    jj_consume_token(ExperimentalFactorTermSourceREF);
    label_50:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[81] = jj_gen;
        break label_50;
      }
      jj_consume_token(TAB);
      readIt();
    }
    jj_consume_token(CR_LF);
  }

  final public void experimentalFactorType() throws ParseException {
                               _header = getToken(1);
    jj_consume_token(ExperimentalFactorType);
    label_51:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[82] = jj_gen;
        break label_51;
      }
      jj_consume_token(TAB);
      readIt();
    }
    jj_consume_token(CR_LF);
  }

  final public void antibodyName() throws ParseException {
                     _header = getToken(1);
    jj_consume_token(AntibodyName);
    label_52:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[83] = jj_gen;
        break label_52;
      }
      jj_consume_token(TAB);
      readIt();
    }
    jj_consume_token(CR_LF);
  }

  final public void antibodyProvider() throws ParseException {
                         _header = getToken(1);
    jj_consume_token(AntibodyProvider);
    label_53:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[84] = jj_gen;
        break label_53;
      }
      jj_consume_token(TAB);
      readIt();
    }
    jj_consume_token(CR_LF);
  }

  final public void antibodyCatalogID() throws ParseException {
                          _header = getToken(1);
    jj_consume_token(AntibodyCatalogID);
    label_54:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[85] = jj_gen;
        break label_54;
      }
      jj_consume_token(TAB);
      readIt();
    }
    jj_consume_token(CR_LF);
  }

  final public void antibodyLotID() throws ParseException {
                      _header = getToken(1);
    jj_consume_token(AntibodyLotID);
    label_55:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[86] = jj_gen;
        break label_55;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_56:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[87] = jj_gen;
        break label_56;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void antibodyComment() throws ParseException {
                        _header = getToken(1);
    jj_consume_token(AntibodyComment);
    label_57:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[88] = jj_gen;
        break label_57;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_58:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[89] = jj_gen;
        break label_58;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void dilution() throws ParseException {
                 _header = getToken(1);
    jj_consume_token(Dilution);
    label_59:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[90] = jj_gen;
        break label_59;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_60:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[91] = jj_gen;
        break label_60;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void dilutionValue() throws ParseException {
                      _header = getToken(1);
    jj_consume_token(DilutionValue);
    label_61:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[92] = jj_gen;
        break label_61;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_62:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[93] = jj_gen;
        break label_62;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void dilutionUnit() throws ParseException {
                     _header = getToken(1);
    jj_consume_token(DilutionUnit);
    label_63:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[94] = jj_gen;
        break label_63;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_64:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[95] = jj_gen;
        break label_64;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void dilutionUnitTermSourceRef() throws ParseException {
                                  _header = getToken(1);
    jj_consume_token(DilutionUnitTermSourceRef);
    label_65:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[96] = jj_gen;
        break label_65;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_66:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[97] = jj_gen;
        break label_66;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void person() throws ParseException {
    personLastName();
    personFirstName();
    personMidInitials();
    personEmail();
    personPhone();
    personFax();
    personAddress();
    personAffiliation();
    personRoles();
    personRolesTermSourceREF();
  }

  final public void personLastName() throws ParseException {
                       _header = getToken(1);
    jj_consume_token(PersonLastName);
    label_67:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[98] = jj_gen;
        break label_67;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_68:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[99] = jj_gen;
        break label_68;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void personFirstName() throws ParseException {
                        _header = getToken(1);
    jj_consume_token(PersonFirstName);
    label_69:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[100] = jj_gen;
        break label_69;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_70:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[101] = jj_gen;
        break label_70;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void personMidInitials() throws ParseException {
                          _header = getToken(1);
    jj_consume_token(PersonMidInitials);
    label_71:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[102] = jj_gen;
        break label_71;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_72:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[103] = jj_gen;
        break label_72;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void personEmail() throws ParseException {
                    _header = getToken(1);
    jj_consume_token(PersonEmail);
    label_73:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[104] = jj_gen;
        break label_73;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_74:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[105] = jj_gen;
        break label_74;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void personPhone() throws ParseException {
                    _header = getToken(1);
    jj_consume_token(PersonPhone);
    label_75:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[106] = jj_gen;
        break label_75;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_76:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[107] = jj_gen;
        break label_76;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void personFax() throws ParseException {
                  _header = getToken(1);
    jj_consume_token(PersonFax);
    label_77:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[108] = jj_gen;
        break label_77;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_78:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[109] = jj_gen;
        break label_78;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void personAddress() throws ParseException {
                      _header = getToken(1);
    jj_consume_token(PersonAddress);
    label_79:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[110] = jj_gen;
        break label_79;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_80:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[111] = jj_gen;
        break label_80;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void personAffiliation() throws ParseException {
                          _header = getToken(1);
    jj_consume_token(PersonAffiliation);
    label_81:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[112] = jj_gen;
        break label_81;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_82:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[113] = jj_gen;
        break label_82;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  //System.out.println("found public release date:"+buffie.toString());}  final public void personRoles() throws ParseException {
                    _header = getToken(1);
    jj_consume_token(PersonRoles);
    label_83:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[114] = jj_gen;
        break label_83;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_84:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[115] = jj_gen;
        break label_84;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void personRolesTermSourceREF() throws ParseException {
                                 _header = getToken(1);
    jj_consume_token(PersonRolesTermSourceREF);
    label_85:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[116] = jj_gen;
        break label_85;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_86:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[117] = jj_gen;
        break label_86;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void qualityControl() throws ParseException {
    qualityControlType();
    qualityControlTermSourceREF();
  }

//-------------------------------------------------------------------------  final public void qualityControlType() throws ParseException {
                           _header = getToken(1);
    jj_consume_token(QualityControlType);
    label_87:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[118] = jj_gen;
        break label_87;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_88:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[119] = jj_gen;
        break label_88;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void qualityControlTermSourceREF() throws ParseException {
                                    _header = getToken(1);
    jj_consume_token(QualityControlTermSourceREF);
    label_89:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[120] = jj_gen;
        break label_89;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_90:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[121] = jj_gen;
        break label_90;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void replicate() throws ParseException {
    replicateType();
    replicateTermSourceREF();
  }

  final public void replicateType() throws ParseException {
                      _header = getToken(1);
    jj_consume_token(ReplicateType);
    label_91:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[122] = jj_gen;
        break label_91;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_92:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[123] = jj_gen;
        break label_92;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void replicateTermSourceREF() throws ParseException {
                               _header = getToken(1);
    jj_consume_token(ReplicateTermSourceREF);
    label_93:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[124] = jj_gen;
        break label_93;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_94:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[125] = jj_gen;
        break label_94;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void normalization() throws ParseException {
                      _header = getToken(1);
    normalizationType();
    normalizationTermSourceREF();
  }

//-------------------------------------------------------------------------  final public void normalizationType() throws ParseException {
                          _header = getToken(1);
    jj_consume_token(NormalizationType);
    label_95:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[126] = jj_gen;
        break label_95;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_96:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[127] = jj_gen;
        break label_96;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void normalizationTermSourceREF() throws ParseException {
                                   _header = getToken(1);
    jj_consume_token(NormalizationTermSourceREF);
    label_97:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[128] = jj_gen;
        break label_97;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_98:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[129] = jj_gen;
        break label_98;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void dates() throws ParseException {
    dateOfExperiment();
    publicReleaseDate();
  }

  final public void dateOfExperiment() throws ParseException {
  Token head = null;
  Token tail = null;
    head = getToken(1);
    jj_consume_token(DateofExperiment);
    jj_consume_token(TAB);
    jj_consume_token(DATE_START);
    jj_consume_token(DATE);
    jj_consume_token(DATE_END);
    jj_consume_token(CR_LF);
    TokenList tl = new TokenList(head, getToken(0));
    StringBuffer buffie = new StringBuffer();
    for (Token p = head;
    p != tail;
    p = p.next){
      buffie.append(p.image);
    }
  //System.out.println("found date:"+buffie.toString());

  }

  final public void publicReleaseDate() throws ParseException {
  Token head = null;
  Token tail = null;
    head = getToken(1);
    jj_consume_token(PublicReleaseDate);
    jj_consume_token(TAB);
    jj_consume_token(DATE_START);
    jj_consume_token(DATE);
    jj_consume_token(DATE_END);
    jj_consume_token(CR_LF);
    TokenList tl = new TokenList(head, getToken(0));
    StringBuffer buffie = new StringBuffer();
    for (Token p = head;
    p != tail;
    p = p.next){
      buffie.append(p.image);
    }
  }

//-------------------------------------------------------------------------  final public void protocol() throws ParseException {
                 _header = getToken(1);
    protocolName();
    protocolType();
    protocolDescription();
    protocolParameters();
    protocolHardware();
    protocolSoftware();
    protocolContact();
    protocolTermSourceREF();
  }

//----------------------------------------------------------------  final public void protocolName() throws ParseException {
                     _header = getToken(1);
    jj_consume_token(ProtocolName);
    label_99:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[130] = jj_gen;
        break label_99;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_100:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[131] = jj_gen;
        break label_100;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void protocolType() throws ParseException {
                     _header = getToken(1);
    jj_consume_token(ProtocolType);
    label_101:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[132] = jj_gen;
        break label_101;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_102:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[133] = jj_gen;
        break label_102;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void protocolDescription() throws ParseException {
                            _header = getToken(1);
    jj_consume_token(ProtocolDescription);
    label_103:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[134] = jj_gen;
        break label_103;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_104:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[135] = jj_gen;
        break label_104;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void protocolParameters() throws ParseException {
                           _header = getToken(1);
    jj_consume_token(ProtocolParameters);
    label_105:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[136] = jj_gen;
        break label_105;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_106:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[137] = jj_gen;
        break label_106;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void protocolHardware() throws ParseException {
                         _header = getToken(1);
    jj_consume_token(ProtocolHardware);
    label_107:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[138] = jj_gen;
        break label_107;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_108:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[139] = jj_gen;
        break label_108;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void protocolSoftware() throws ParseException {
                         _header = getToken(1);
    jj_consume_token(ProtocolSoftware);
    label_109:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[140] = jj_gen;
        break label_109;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_110:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[141] = jj_gen;
        break label_110;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void protocolContact() throws ParseException {
                        _header = getToken(1);
    jj_consume_token(ProtocolContact);
    label_111:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[142] = jj_gen;
        break label_111;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_112:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[143] = jj_gen;
        break label_112;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void protocolTermSourceREF() throws ParseException {
                              _header = getToken(1);
    jj_consume_token(ProtocolTermSourceREF);
    label_113:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[144] = jj_gen;
        break label_113;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_114:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[145] = jj_gen;
        break label_114;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void termSource() throws ParseException {
                   _header = getToken(1);
    termSourceName();
    termSourceFile();
    termSourceVersion();
  }

//-------------------------------------------------------------------------  final public void termSourceName() throws ParseException {
                       _header = getToken(1);
    jj_consume_token(TermSourceName);
    label_115:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[146] = jj_gen;
        break label_115;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_116:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[147] = jj_gen;
        break label_116;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);

  }

//-------------------------------------------------------------------------  final public void termSourceFile() throws ParseException {
                       _header = getToken(1);
    jj_consume_token(TermSourceFile);
    label_117:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[148] = jj_gen;
        break label_117;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_118:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[149] = jj_gen;
        break label_118;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void termSourceVersion() throws ParseException {
                          _header = getToken(1);
    jj_consume_token(TermSourceVersion);
    label_119:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[150] = jj_gen;
        break label_119;
      }
      jj_consume_token(TAB);
      readIt();
    }
    jj_consume_token(CR_LF);
  }

  final public void publication() throws ParseException {
    pubMedID();
    publicationDOI();
    publicationAuthorList();
    publicationTitle();
    publicationStatus();
    publicationStatusTermSourceREF();
  }

  final public void pubMedID() throws ParseException {
                 _header = getToken(1);
    jj_consume_token(PubMedID);
    label_120:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[151] = jj_gen;
        break label_120;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_121:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[152] = jj_gen;
        break label_121;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void publicationDOI() throws ParseException {
                       _header = getToken(1);
    jj_consume_token(PublicationDOI);
    label_122:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[153] = jj_gen;
        break label_122;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_123:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[154] = jj_gen;
        break label_123;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void publicationAuthorList() throws ParseException {
                              _header = getToken(1);
    jj_consume_token(PublicationAuthorList);
    label_124:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[155] = jj_gen;
        break label_124;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_125:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[156] = jj_gen;
        break label_125;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void publicationTitle() throws ParseException {
                         _header = getToken(1);
    jj_consume_token(PublicationTitle);
    label_126:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[157] = jj_gen;
        break label_126;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_127:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[158] = jj_gen;
        break label_127;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void publicationStatus() throws ParseException {
                          _header = getToken(1);
    jj_consume_token(PublicationStatus);
    label_128:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[159] = jj_gen;
        break label_128;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_129:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[160] = jj_gen;
        break label_129;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void publicationStatusTermSourceREF() throws ParseException {
                                       _header = getToken(1);
    jj_consume_token(PublicationStatusTermSourceREF);
    label_130:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[161] = jj_gen;
        break label_130;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_131:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[162] = jj_gen;
        break label_131;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

// FIXME need to know if eol in description is followed by "Protocol Name"//-------------------------------------------------------------------------  final public void experimentDescription() throws ParseException {
                              _header = getToken(1);
    jj_consume_token(ExperimentDescription);
    jj_consume_token(TAB);
    readIt();
    label_132:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[163] = jj_gen;
        break label_132;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  final public void sradfFile() throws ParseException {
                  _header = getToken(1);
    jj_consume_token(SRADFFile);
    label_133:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[164] = jj_gen;
        break label_133;
      }
      jj_consume_token(TAB);
      readIt();
    }
    label_134:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TAB:
        ;
        break;
      default:
        jj_la1[165] = jj_gen;
        break label_134;
      }
      jj_consume_token(TAB);
    }
    jj_consume_token(CR_LF);
  }

  /** Generated Token Manager. */
  public RplaDatasetRplaIdfGrammarTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[166];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static private int[] jj_la1_2;
  static {
      jj_la1_init_0();
      jj_la1_init_1();
      jj_la1_init_2();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x8000,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x0,0x0,0x0,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,0x2,};
   }
   private static void jj_la1_init_1() {
      jj_la1_1 = new int[] {0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x0,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x20000000,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x40000000,0x40000000,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,};
   }
   private static void jj_la1_init_2() {
      jj_la1_2 = new int[] {0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x0,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x1,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x10,0x0,0x8,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,};
   }

  /** Constructor with InputStream. */
  public RplaDatasetRplaIdfGrammar(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public RplaDatasetRplaIdfGrammar(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new RplaDatasetRplaIdfGrammarTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 166; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 166; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public RplaDatasetRplaIdfGrammar(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new RplaDatasetRplaIdfGrammarTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 166; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 166; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public RplaDatasetRplaIdfGrammar(RplaDatasetRplaIdfGrammarTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 166; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(RplaDatasetRplaIdfGrammarTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 166; i++) jj_la1[i] = -1;
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List jj_expentries = new java.util.ArrayList();
  private int[] jj_expentry;
  private int jj_kind = -1;

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[76];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 166; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
          if ((jj_la1_2[i] & (1<<j)) != 0) {
            la1tokens[64+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 76; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = (int[])jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

}
