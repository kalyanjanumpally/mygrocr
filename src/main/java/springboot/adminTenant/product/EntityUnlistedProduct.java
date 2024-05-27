package springboot.adminTenant.product;

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



@Getter
@Setter
@Entity
@Indexed
@Table(name="unlisted_products")
public class EntityUnlistedProduct {
	
	//define fields
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="unlisted_product_id")
	private Integer unlistedProductId;
	
	@Column(name="unlisted_product_home_id")
	private Integer unlistedProductHomeId;
	
	@Column(name="bulk_list")
	private Boolean bulkList;
	
	@Column(name="unlisted_product_name")
	@Field(termVector = TermVector.YES)
	private String unlistedProductName;
	
	@Column(name="unlisted_product_brand_id")
	private Integer unlistedProductBrandId;

	public EntityUnlistedProduct() {
		
	}

	@Override
	public String toString() {
		return "EntityUnlistedProduct [unlistedProductId=" + unlistedProductId + ", unlistedProductHomeId="
				+ unlistedProductHomeId + ", bulkList=" + bulkList + ", unlistedProductName=" + unlistedProductName
				+ ", unlistedProductBrandId=" + unlistedProductBrandId + "]";
	}

	public EntityUnlistedProduct(Integer unlistedProductHomeId, Boolean bulkList, String unlistedProductName,
			Integer unlistedProductBrandId) {
		super();
		this.unlistedProductHomeId = unlistedProductHomeId;
		this.bulkList = bulkList;
		this.unlistedProductName = unlistedProductName;
		this.unlistedProductBrandId = unlistedProductBrandId;
	}


}
