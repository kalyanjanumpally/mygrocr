package springboot.adminTenant.categories;

import java.util.Date;
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
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="products")
public class EntityProductCategories {
	
	//define fields
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="product_id")
	private Integer productId;
	
	@Column(name="product_name")
	private String productName;
	
	/*
	
    @Column(name = "description")
    private String description;
    
    @Column(name = "sku")
    private String sku;
	
	@Column(name="display")
	private boolean display;
	
	@Column(name="product_MRP")
	private Integer productMrp;
	
	@Column(name="kg_pieces_per_unit")
	private Float kgPiecesPerUnit;
	
	@Column(name="kg_pieces_name")
	private String kgPiecesName;
	
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
    
    @Column(name = "gst")
    private Integer gst;
    
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
	*/
	 
	@JsonIgnore
	@ManyToMany(fetch=FetchType.LAZY, cascade= 
		{CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
		mappedBy="products")

	private List<EntityCategory> categories;
	
	

	
	
	//define constructors
	public EntityProductCategories() {		
	}

	@Override
	public String toString() {
		return "EntityProduct [productId=" + productId + ", productName=" + productName + ", categories=" + categories
				+ "]";
	}


	public EntityProductCategories(String productName, List<EntityCategory> categories) {
		this.productName = productName;
		this.categories = categories;
	}

	


	
	//getters & setters


}
