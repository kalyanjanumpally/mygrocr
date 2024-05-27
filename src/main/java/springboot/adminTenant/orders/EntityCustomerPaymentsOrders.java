package springboot.adminTenant.orders;

import java.sql.Date;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name="customer_payments")
public class EntityCustomerPaymentsOrders {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="payment_id")
	private Long paymentId;
	
	@Column(name="amount")
	private Float amount;
	
	@Column(name="payment_mode")
	private String paymentMode;
	
	@Column(name="payment_date")
	private Date paymentDate;
	
	
	@JsonIgnore
	@ManyToOne(fetch=FetchType.EAGER, cascade= 
		{CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name="payment_order_id")
	private EntityOrder order;


	public EntityCustomerPaymentsOrders(Float amount, String paymentMode, Date paymentDate,
			EntityOrder order) {
		this.amount = amount;
		this.paymentMode = paymentMode;
		this.paymentDate = paymentDate;
		this.order = order;
	}


	@Override
	public String toString() {
		return "EntityCustomerPaymentsOrders [paymentId=" + paymentId + ", amount=" + amount + ", paymentMode="
				+ paymentMode + ", paymentDate=" + paymentDate +  ", order="
				+ order + "]";
	}



}
