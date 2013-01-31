//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.carpla.domain.rplarray;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;

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

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

@Entity
public class RplArrayGroup extends AbstractCaArrayObject {

	private String			name;
	private Set<RplArray>	arrays	= new HashSet<RplArray>();

	@NotNull
	@Length(min = 1, max = DEFAULT_STRING_COLUMN_SIZE)
	public String getName () {
		return name;
	}

	public void setName ( String name) {
		this.name = name;
	}

	@OneToMany
	public Set<RplArray> getRplArrays () {
		return arrays;
	}

	private void setRplArrays ( Set<RplArray> arrays) {
		this.arrays = arrays;
	}

}
