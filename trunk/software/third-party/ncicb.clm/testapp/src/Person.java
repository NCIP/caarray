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