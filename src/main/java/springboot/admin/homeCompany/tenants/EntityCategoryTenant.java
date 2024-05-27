package springboot.admin.homeCompany.tenants;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

@Getter
@Setter

@Entity
@Table(name="categories")
public class EntityCategoryTenant {
	
	//define fields
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="category_id")
	private int categoryId;
	
	@Column(name="category_name")
	private String categoryName;
	
	@Column(name="parent_category_id")
	private int parentCategoryId;
	
	/*
	@JsonIgnore
	@ManyToMany(fetch=FetchType.EAGER, cascade= 
				{CascadeType.DETACH, CascadeType.MERGE,CascadeType.PERSIST, CascadeType.REFRESH}, 
				mappedBy="categories")	
	private List<EntityProductTenant> products;
	*/
	
	//define constructors
	public EntityCategoryTenant() {		
	}

	@Override
	public String toString() {
		return "EntityCategoryTenant [categoryId=" + categoryId + ", categoryName=" + categoryName
				+ ", parentCategoryId=" + parentCategoryId + "]";
	}

	public EntityCategoryTenant(String categoryName, int parentCategoryId) {
		super();
		this.categoryName = categoryName;
		this.parentCategoryId = parentCategoryId;
	}

	
}	


	