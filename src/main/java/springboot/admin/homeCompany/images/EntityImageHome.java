package springboot.admin.homeCompany.images;

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
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.TermVector;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Indexed
@Entity
@Table(name="images")
public class EntityImageHome {
	
	//define fields
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="image_id")
	private int imageId;
	
	@Column(name="image_name")
	@Field(termVector = TermVector.YES)
	private String imageName;
	
	@Column(name="image_url")
	private String imageUrl;
	
	//define constructors
	public EntityImageHome() {		
	}
	
	public EntityImageHome(String imageName, String imageUrl) {
		this.imageName = imageName;
		this.imageUrl = imageUrl;
	}

	//define getters and setters

	@Override
	public String toString() {
		return "EntityImage [imageId=" + imageId + ", imageName=" + imageName + "]";
	}




	
}	