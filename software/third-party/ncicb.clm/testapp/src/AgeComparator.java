//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
import java.util.Comparator;


 class AgeComparator implements Comparator
{
	public int compare(Object o1, Object o2)
	{
		Person p1 = (Person)o1;
		Person p2 = (Person)o2;
		return p1.getAge() - p2.getAge();
	}
}
