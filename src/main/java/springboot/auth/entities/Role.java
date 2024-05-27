package springboot.auth.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "roles")
public class Role {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="role_id")
	private Integer roleId;

	@Enumerated(EnumType.STRING)
	@Column(length = 20, name="role_name")
	private ERole roleName;

	public Role() {

	}

	public Role(ERole roleName) {
		this.roleName = roleName;
	}
	
	

}
