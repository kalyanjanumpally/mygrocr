package springboot.admin.homeCompany.categories;

import java.sql.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;


import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.TermVector;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import springboot.admin.homeCompany.brands.EntityBrandHome;
import springboot.admin.homeCompany.categories.EntityCategoryHome;





@Getter
@Setter
@Entity
@Indexed
@Table(name="products")
public class EntityProductCategory {
	
	//define fields
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="product_id")
	private Integer productId;
	
	@Column(name="product_name")
	@Field(termVector = TermVector.YES)
	private String productName;	
  
	/*
    @Column(name = "gst")
    private Integer gst;    
	

    @Column(name = "product_delete_status")
    private Boolean productDeleteStatus;
    
    @Column(name = "product_active_status")
    private Boolean productActiveStatus;
    
		
    @Column(name = "description")
    private String description;    
	
	@Column(name="has_expiry_date")
	private Boolean hasExpiryDate;
	
    @Column(name = "date_created")
    @CreationTimestamp
    private Date dateCreated;

    @Column(name = "last_updated")
    @UpdateTimestamp
    private Date lastUpdated;
	
    @Column(name = "hsn_code")
    private String hsnCode;
    
    @Column(name = "sort_order")
    private Integer sortOrder;
    
    @Column(name = "meta_title")
    private String metaTitle;
    
    @Column(name = "meta_description")
    private String metaDescription;
    
    @Column(name = "meta_keywords")
    private String metaKeywords;
    
    @Column(name = "seo_url")
    private String seoURL;
    
    //@Column(name= "images_order")
    //private String imagesOrder;
    */
	
    
	@ManyToMany(fetch=FetchType.LAZY, cascade= 
		{CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(
			name="product_category",
			joinColumns=@JoinColumn(name="product_id"),
			inverseJoinColumns=@JoinColumn(name="category_id")		
			)
	private List<EntityCategoryHome> categories;
	
	

	public EntityProductCategory() {
		
	}



	@Override
	public String toString() {
		return "EntityProductCategory [productId=" + productId + ", productName=" + productName + ", categories="
				+ categories + "]";
	}



	public EntityProductCategory(String productName, List<EntityCategoryHome> categories) {
		super();
		this.productName = productName;
		this.categories = categories;
	}
	
	 


}
