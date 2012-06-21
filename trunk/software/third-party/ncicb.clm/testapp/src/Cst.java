import gov.nih.nci.logging.api.logger.hibernate.HibernateSessionFactoryHelper;


import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;


public class Cst
{


		public static void main(String[] args)
		{
			
//			Get the Collator for US English and set its strength to PRIMARY
			 
			
			List people = new ArrayList();
			people.add( new Person("Homer", 38) );
			people.add( new Person("homer", 35) );
			people.add( new Person("Bart", 15) );
			people.add( new Person("homer", 13) );
			System.out.println("\t" + people);
			// Sort by natural order
	 
			Collections.sort(people);
			System.out.println("Sort by Natural order");
			System.out.println("\t" + people);
	 
			// Sort by reverse natural order
	 
			Collections.sort(people, Collections.reverseOrder());
			System.out.println("Sort by reverse natural order");
			System.out.println("\t" + people);
	 
			//  Use a Comparator to sort by age
	 
			Collections.sort(people, new AgeComparator());
			System.out.println("Sort using Age Comparator");
			System.out.println("\t" + people);
		}

}
