package springboot.categories;

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
public class WebEntityCategory {
	
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
	@ManyToMany(fetch=FetchType.LAZY, cascade= 
		{CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(
			name="product_category",
			joinColumns=@JoinColumn(name="category_id"),
			inverseJoinColumns=@JoinColumn(name="product_id")		
			)
	private List<WebEntityProductCategories> products;
	
	//define constructors
	public WebEntityCategory() {		
	}

	public WebEntityCategory(String categoryName, int parentCategoryId, List<WebEntityProductCategories> products) {
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
