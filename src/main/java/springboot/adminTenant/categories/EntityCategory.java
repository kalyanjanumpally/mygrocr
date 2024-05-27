package springboot.adminTenant.categories;

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

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="categories")
public class EntityCategory {
	
	//define fields
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="category_id")
	private Integer categoryId;
	
	@Column(name="category_name")
	private String categoryName;
	
	@Column(name="parent_category_id")
	private Integer parentCategoryId;
	
	@ManyToMany(fetch=FetchType.LAZY, cascade= 
		{CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(
			name="product_category",
			joinColumns=@JoinColumn(name="category_id"),
			inverseJoinColumns=@JoinColumn(name="product_id")		
			)
	private List<EntityProductCategories> products;
	
	//define constructors
	public EntityCategory() {		
	}

	public EntityCategory(String categoryName, int parentCategoryId, List<EntityProductCategories> products) {
		this.categoryName = categoryName;
		this.parentCategoryId = parentCategoryId;
		this.products = products;
	}

	

	//define getters and setters

	@Override
	public String toString() {
		return "EntityCategory [categoryId=" + categoryId + ", categoryName=" + categoryName + ", parentCategoryId="
				+ parentCategoryId + ", products=" + products + "]";
	}







}
