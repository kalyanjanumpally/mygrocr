package springboot.admin.homeCompany.products;

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
public class EntityCategoryProductHome {
	
	//define fields
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="category_id")
	private Integer categoryId;
	
	@Column(name="category_name")
	private String categoryName;
	
	@Column(name="parent_category_id")
	private Integer parentCategoryId;
	
	@JsonIgnore
	@ManyToMany(fetch=FetchType.EAGER, cascade= 
				{CascadeType.DETACH, CascadeType.MERGE,CascadeType.PERSIST, CascadeType.REFRESH}, 
				mappedBy="categories")	
	private List<EntityProductHome> products;
	
	//define constructors
	public EntityCategoryProductHome() {		
	}

	@Override
	public String toString() {
		return "EntityCategoryProductHome [categoryId=" + categoryId + ", categoryName=" + categoryName
				+ ", parentCategoryId=" + parentCategoryId + ", products=" + products + "]";
	}

	public EntityCategoryProductHome(String categoryName, Integer parentCategoryId, List<EntityProductHome> products) {
		super();
		this.categoryName = categoryName;
		this.parentCategoryId = parentCategoryId;
		this.products = products;
	}


	
	
}	


	