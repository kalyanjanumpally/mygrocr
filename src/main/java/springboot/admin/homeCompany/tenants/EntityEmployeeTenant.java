package springboot.admin.homeCompany.tenants;

import java.util.HashSet;
import java.util.Set;

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

import lombok.Data;

@Data
@Entity
@Table(name="employees")
public class EntityEmployeeTenant {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="employee_id")
	private Integer employeeId;
	
	@Column(name="username")
	private String username;
	
	@Column(name="employee_active")
	private Boolean employeeActive;
	
	@Column(name="tenant_url")
	private String tenant;

	@Column(name="password")
	private String password;

	public EntityEmployeeTenant() {
	}

	public EntityEmployeeTenant(String username, String tenant, String password, Boolean employeeActive) {
		super();
		this.username = username;
		this.tenant = tenant;
		this.password = password;
		this.employeeActive = employeeActive;
	}

	

}
