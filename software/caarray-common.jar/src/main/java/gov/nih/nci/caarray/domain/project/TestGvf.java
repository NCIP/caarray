package gov.nih.nci.caarray.domain.project;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

@Entity
@Table(name = "testgvf")
public class TestGvf {
	private String	name;
	private Long				id;
	@NotNull
	@Length(min = 1, max = 176)
	public String getName () {
		return this.name;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId () {
		return id;
	}
	
	
	
	
	
}
