package springboot.product;

import java.sql.Date;
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
@Table(name="product_variant_by_unit")
public class WebEntityProductVariantByUnit {
	
	//define fields
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="variant_id")
	private Integer variantId;
	
	@Column(name="variant_home_id")
	private Integer variantHomeId;
	
	@Column(name="unit")
	private String unit;
	
	@Column(name="quantity")
	private Float quantity;
	
	@Column(name = "sku")
    private String sku;
	
	@Column(name="variant_display")
	private boolean variantDisplay;
	
	@Column(name="display_out_of_stock_variant")
	private boolean displayOutOfStockVariant;	
	
	@Column(name="variant_MRP")
	private Integer variantMrp;
	
    @Column(name= "images_order")
    private String imagesOrder;
	
	@Column(name="kg_pieces_per_unit")
	private Float kgPiecesPerUnit;
	
	@Column(name="kg_pieces_name")
	private String kgPiecesName;
	
	@JsonIgnore
	@ManyToOne(cascade= 
	{CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name="variant_product_id")
	private WebEntityProduct product;
	
	
	
	
	@ManyToMany(fetch=FetchType.EAGER, cascade= 
		{CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(
			name="product_variant_by_unit_image",
			joinColumns=@JoinColumn(name="variant_id"),
			inverseJoinColumns=@JoinColumn(name="image_id")		
			)
	private List<WebEntityImageProduct> images;

	public WebEntityProductVariantByUnit() {
		
	}

	@Override
	public String toString() {
		return "WebEntityProductVariantByUnit [variantId=" + variantId + ", variantHomeId=" + variantHomeId + ", unit="
				+ unit + ", quantity=" + quantity + ", sku=" + sku + ", variantDisplay=" + variantDisplay
				+ ", displayOutOfStockVariant=" + displayOutOfStockVariant + ", variantMrp=" + variantMrp
				+ ", imagesOrder=" + imagesOrder + ", kgPiecesPerUnit=" + kgPiecesPerUnit + ", kgPiecesName="
				+ kgPiecesName + ", product=" + product + ", images=" + images + "]";
	}

	public WebEntityProductVariantByUnit(Integer variantHomeId, String unit, Float quantity, String sku,
			boolean variantDisplay, boolean displayOutOfStockVariant, Integer variantMrp, String imagesOrder,
			Float kgPiecesPerUnit, String kgPiecesName, WebEntityProduct product, List<WebEntityImageProduct> images) {
		super();
		this.variantHomeId = variantHomeId;
		this.unit = unit;
		this.quantity = quantity;
		this.sku = sku;
		this.variantDisplay = variantDisplay;
		this.displayOutOfStockVariant = displayOutOfStockVariant;
		this.variantMrp = variantMrp;
		this.imagesOrder = imagesOrder;
		this.kgPiecesPerUnit = kgPiecesPerUnit;
		this.kgPiecesName = kgPiecesName;
		this.product = product;
		this.images = images;
	}



}
