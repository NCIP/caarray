//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
import java.util.*;
 
public class Person implements Comparable
{
	String name;
	int age;
 
	public Person(String name, int age)
	{
	   	this.name = name;
	   	this.age = age;
	}
 
	public String getName()
	{
		return name;
	}
 
	public int getAge()
	{
		return age;
	}
 
	public String toString()
	{
		return name + " : " + age;
	}
 
	/*
	**  Implement the natural order for this class
	*/
	public int compareTo(Object o)
	{
		return getName().compareTo(((Person)o).getName());
	}
 
	
 
	
}
