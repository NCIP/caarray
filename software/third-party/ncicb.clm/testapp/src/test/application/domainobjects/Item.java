package test.application.domainobjects;

import java.io.Serializable;

public class Item implements Serializable
{
	private int id;
	private String name;
	private String manufacturer;
	private float price;
	private int customerid;

	public Item()
	{
	}

	public Item(String name, String manufacturer, float price)
	{
		this.name = name;
		this.manufacturer = manufacturer;
		this.price = price;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getCustomerid()
	{
		return customerid;
	}

	public void setCustomerid(int id)
	{
		this.customerid = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public float getPrice()
	{
		return price;
	}

	public void setPrice(float price)
	{
		this.price = price;
	}

	public String getmanufacturer()
	{
		return this.manufacturer;
	}

	public void setmanufacturer(String manufacturer)
	{
		this.manufacturer = manufacturer;
	}

}