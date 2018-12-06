package service.storage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import service.storage.model.Image;

/**
* Repository to store the Images database.
*/
@Repository
public interface ImageRepository extends JpaRepository<Image, Long>{

	public static final String FIND_PUBLIC_ID = "SELECT * FROM images where public_id = :id";
	
	@Query(value=FIND_PUBLIC_ID, nativeQuery=true)
	Image findByPublicId(@Param("id") String publicId);
}
