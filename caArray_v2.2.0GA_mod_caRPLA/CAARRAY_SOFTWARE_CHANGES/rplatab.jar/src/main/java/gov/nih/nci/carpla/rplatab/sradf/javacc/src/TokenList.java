//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.carpla.rplatab.sradf.javacc.src;
import java.io.*;

import gov.nih.nci.carpla.rplatab.sradf.javacc.generated.*;

public class TokenList {
	private Token head;

	private Token tail;

	public TokenList(Token head, Token tail) {

		this.head = head;
		this.tail = tail;
	}

	int getLength() {
		int i = 0;
		for (Token p = head; p != tail; p = p.next) {
			i++;

		}
		return i;
	}

	void print(PrintStream os) {

		for (Token p = head; p != tail; p = p.next) {

			os.print(p.image);
		}
	}

}
