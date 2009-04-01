package gov.nih.nci.carpla.domain.rplarray;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table; 

@Entity
public class RplArrayGroup extends AbstractCaArrayObject {
	private String			name;
	private Set<RplArray>	arrays	= new HashSet<RplArray>();

	/**
	 * @return the name of the group
	 */
	public String getName () {
		return name;
	}

	/**
	 * @param name
	 *            new name for group
	 */
	public void setName ( String name) {
		this.name = name;
	}

	/**
	 * @return all arrays in this group
	 */
	@OneToMany(mappedBy = "arrayGroup")
	public Set<RplArray> getRplArrays () {
		return arrays;
	}

	@SuppressWarnings( { "unused", "PMD.UnusedPrivateMethod" })
	private void setRplArrays ( Set<RplArray> arrays) {
		this.arrays = arrays;
	}

}
