package springboot.adminTenant.product;

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
public class EntityCategoryProduct {
	
	//define fields
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="category_id")
	private Integer categoryId;
	
	@Column(name="category_name")
	private String categoryName;
	
	@Column(name="parent_category_id")
	private Integer parentCategoryId;
	
	@Column(name="category_home_id")
	private Integer categoryHomeId;
	
	
	
	@JsonIgnore
	@ManyToMany(fetch=FetchType.EAGER, cascade= 
				{CascadeType.DETACH, CascadeType.MERGE,CascadeType.PERSIST, CascadeType.REFRESH}, 
				mappedBy="categories")	
	private List<EntityProduct> products;
	
	//define constructors
	public EntityCategoryProduct() {		
	}

	@Override
	public String toString() {
		return "EntityCategoryProduct [categoryId=" + categoryId + ", categoryName=" + categoryName
				+ ", parentCategoryId=" + parentCategoryId + ", categoryHomeId=" + categoryHomeId + "]";
	}

	public EntityCategoryProduct(String categoryName, Integer parentCategoryId, Integer categoryHomeId,
			List<EntityProduct> products) {
		super();
		this.categoryName = categoryName;
		this.parentCategoryId = parentCategoryId;
		this.categoryHomeId = categoryHomeId;
		this.products = products;
	}

	
}	


	