package test.application.domainobjects;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Customer implements Serializable
{
	private int id;
	private String first;
	private String last;
	private String street;
	private String city;
	private String state;
	private String zip;

	private Set items = new HashSet();

	public Customer()
	{
	}

	public Customer(String first, String last, String street, String city, String state, String zip)
	{
		this.first = first;
		this.last = last;
		this.street = street;
		this.city = city;
		this.state = state;
		this.zip = zip;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getFirst()
	{
		return first;
	}

	public void setFirst(String first)
	{
		this.first = first;
	}

	public String getLast()
	{
		return last;
	}

	public void setLast(String last)
	{
		this.last = last;
	}

	public String getStreet()
	{
		return street;
	}

	public void setStreet(String street)
	{
		this.street = street;
	}

	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public String getState()
	{
		return state;
	}

	public void setState(String state)
	{
		this.state = state;
	}

	public String getZip()
	{
		return zip;
	}

	public void setItems(Set newVal)
	{
		this.items = newVal;
	}

	public Set getItems()
	{
		return this.items;
	}

	public void setZip(String zip)
	{
		this.zip = zip;
	}

	/**
	 * Determines if a de-serialized file is compatible with this class.
	 * 
	 * Maintainers must change this value if and only if the new version of this
	 * class is not compatible with old versions. See Sun docs for <a
	 * href=http://java.sun.com/products/jdk/1.1/docs/guide
	 * /serialization/spec/version.doc.html> details. </a>
	 * 
	 * Not necessary to include in first version of the class, but included here
	 * as a reminder of its importance.
	 */
	private static final long serialVersionUID = 7526471155622776147L;

}
