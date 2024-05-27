package springboot.admin.homeCompany.categories;

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



@Getter
@Setter

@Entity
@Table(name="categories")
public class EntityCategoryHome {
	
	//define fields
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="category_id")
	private int categoryId;
	
	@Column(name="category_name")
	private String categoryName;
	
	@Column(name="parent_category_id")
	private int parentCategoryId;
	
	@JsonIgnore
	@ManyToMany(fetch=FetchType.EAGER, cascade= 
				{CascadeType.DETACH, CascadeType.MERGE,CascadeType.PERSIST, CascadeType.REFRESH}, 
				mappedBy="categories")	
	private List<EntityProductCategory> products;
	
	//define constructors
	public EntityCategoryHome() {		
	}

	public EntityCategoryHome(String categoryName, int parentCategoryId, List<EntityProductCategory> products) {
		super();
		this.categoryName = categoryName;
		this.parentCategoryId = parentCategoryId;
		this.products = products;
	}

	@Override
	public String toString() {
		return "EntityCategoryHome [categoryId=" + categoryId + ", categoryName=" + categoryName + ", parentCategoryId="
				+ parentCategoryId + ", products=" + products + "]";
	}

	
}	


	