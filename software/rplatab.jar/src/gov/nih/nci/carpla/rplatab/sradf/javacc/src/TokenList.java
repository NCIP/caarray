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
