package springboot.admin.homeCompany.images;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.multipart.MultipartFile;


@CrossOrigin
@RepositoryRestResource(collectionResourceRel = "EntityAdminImage", path = "images")
public interface RepositoryImageHome extends JpaRepository<EntityImageHome, Integer> {



}
