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