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
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.search.annotations.Indexed;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
@Entity
@Indexed
@Table(name="product_variant_by_unit")
public class EntityProductVariantByUnitProductHome {

	//define fields
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="variant_id")
	private Integer variantId;

	@Column(name="unit")
	private String unit;

	//@Column(name="quantity")
	//private Float quantity;

	@Column(name = "sku")
    private String sku;

    @Column(name = "variant_active_status")
    private Boolean variantActiveStatus;

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
	private EntityProductHome product;


	@ManyToMany(fetch=FetchType.LAZY, cascade=
		{CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(
			name="product_variant_by_unit_image",
			joinColumns=@JoinColumn(name="variant_id"),
			inverseJoinColumns=@JoinColumn(name="image_id")
			)
	private List<EntityImageProductHome> images;

	public EntityProductVariantByUnitProductHome() {

	}

	@Override
	public String toString() {
		return "EntityProductVariantByUnitProductHome [variantId=" + variantId + ", unit=" + unit + ", sku=" + sku
				+ ", variantActiveStatus=" + variantActiveStatus + ", variantMrp=" + variantMrp + ", imagesOrder="
				+ imagesOrder + ", kgPiecesPerUnit=" + kgPiecesPerUnit + ", kgPiecesName=" + kgPiecesName + ", product="
				+ product + ", images=" + images + "]";
	}

	public EntityProductVariantByUnitProductHome(String unit, String sku, Boolean variantActiveStatus,
			Integer productMrp, String imagesOrder, Float kgPiecesPerUnit, String kgPiecesName,
			EntityProductHome product, List<EntityImageProductHome> images) {
		super();
		this.unit = unit;
		this.sku = sku;
		this.variantActiveStatus = variantActiveStatus;
		this.variantMrp = productMrp;
		this.imagesOrder = imagesOrder;
		this.kgPiecesPerUnit = kgPiecesPerUnit;
		this.kgPiecesName = kgPiecesName;
		this.product = product;
		this.images = images;
	}




}
