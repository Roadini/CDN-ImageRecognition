package service.storage.service;

import java.io.IOException;
import java.util.Map;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import service.storage.model.Image;
import service.storage.repository.ImageRepository;
import service.storage.util.StorageException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImgSystemStorageService implements StorageService{

	private final Cloudinary cloudinary;
	@Autowired
	private ImageRepository repository;
	
	@SuppressWarnings("rawtypes")
	private Map uploadResult;
	@SuppressWarnings("rawtypes")
	private Map params;
	
	/**
	 * Creates an instance of ImgSystemStorageService to handle the operations involved with Images.
	 */
	@Autowired
	public ImgSystemStorageService() {
		this.cloudinary = new Cloudinary("cloudinary://891399153441365:jBHIk_nBDM38RCp8WQPZdhoQ_dU@jm-es-cloud");
	}	
	
	/**
	 * Store the Image in the CDN and its information in the local database.
	 * @param img Image to be stored.
	 * @param mode Indicates where the Image should be stored.
	 * @throws StorageException If the image is empty or contains illegal characters.
	 * @return The stored Image's Public Id.
	 */
	@Override
	public String store(MultipartFile img, String mode) {
		String filename = img.getOriginalFilename();
		
		try {
			// Check if file is empty
            if (img.isEmpty())
                throw new StorageException("Failed to store empty image " + filename + ".");
            // Security check
            if (filename.contains(".."))
                throw new StorageException("Failed to store image. Image " + filename +" has an invalid name.");
            
            // Store the image in Cloudinary
        	if (mode.equals("user"))
        		params = ObjectUtils.asMap("folder", "user");
        	else if (mode.equals("location"))
        		params = ObjectUtils.asMap("folder", "location");
        	else
        		params = ObjectUtils.emptyMap();
        	
        	if (img.getSize()<=1024000)
        		uploadResult = cloudinary.uploader().upload(img.getBytes(), params);
        	else 
        		uploadResult = cloudinary.uploader().uploadLarge(img.getBytes(), params);
        	
        	// Store info in local database
        	Image image = new Image(uploadResult.get("public_id").toString(),uploadResult.get("url").toString(),uploadResult.get("secure_url").toString());
        	repository.save(image);
            
        	return uploadResult.get("public_id").toString();
        }catch (IOException e) {
            throw new StorageException("Failed to store image " + filename + ".", e);
        }catch (RuntimeException r) {
        	throw new StorageException("Failed to store image " + filename + ".", r);
        }
	}
	
	
	/**
	 * Retrieves an Image's information from the local database.
	 * @param publicId Image's Public Id.
	 * @param mode Indicates where the Image should be stored.
	 * @returns An instance of Image.
	 */
	@Override
	public Image load(String publicId, String mode) {
		Image result;
		
		// Find image in local database
		if (mode == "user")
			result = repository.findByPublicId("user/"+publicId);
		else if (mode == "location")
			result = repository.findByPublicId("location/"+publicId);
		else
			result = repository.findByPublicId(publicId);
		
		// Check if the result is null
		if (result != null)
			return result;
		else
			throw new StorageException("Image with public id "+publicId+" not found.");
	}
	

	/**
	 * Updates the specified Image.
	 * @param img Image to be replace the older one.
	 * @param publicId Image's Public Id to be replaced.
	 * @param mode Indicates where the Image should be stored.
	 * @throws StorageException If the image is empty or contains illegal characters.
	 * @return The new Image's Public Id.
	 */
	@Override
	public String update(MultipartFile img, String publicId, String mode) {
		String filename = img.getOriginalFilename();
		String pId;
		
		try {
			// Check if image is empty
			if (img.isEmpty())
				throw new StorageException("Failed to empty image " + filename + ".");
			// Security check
			if (filename.contains(".."))
				throw new StorageException("Failed to store image. Image " + filename +" has an invalid name.");
			
			// Delete the image in Cloudinary
			if (mode.equals("user")) {
				cloudinary.uploader().destroy("user/"+publicId, ObjectUtils.asMap("invalidate",true));
				params = ObjectUtils.asMap("folder", "user", "public_id", ""+publicId);
				pId = "user/"+publicId;
			} else if (mode.equals("location")) {
				cloudinary.uploader().destroy("location/"+publicId, ObjectUtils.asMap("invalidate",true));
				params = ObjectUtils.asMap("folder", "location", "public_id", ""+publicId);
				pId = "location/"+publicId;
			} else {
				cloudinary.uploader().destroy(""+publicId, ObjectUtils.asMap("invalidate",true));
				params = ObjectUtils.emptyMap();
				pId = ""+publicId;
			}
			
			// Update the image in cloudinary
			if (img.getSize()<=1024000)
        		uploadResult = cloudinary.uploader().upload(img.getBytes(), params);
        	else 
        		uploadResult = cloudinary.uploader().uploadLarge(img.getBytes(), params);
			
			// Update the local database
			Image res = repository.findByPublicId(pId);
			res.setPublicId(uploadResult.get("public_id").toString());
			res.setUrl(uploadResult.get("url").toString());
			res.setSecureUrl(uploadResult.get("secure_url").toString());
			repository.save(res);
			
			return uploadResult.get("public_id").toString();
		} catch (IOException e) {
			throw new StorageException("Failed to store image " + filename + ".",e);
		} catch (RuntimeException r) {
			throw new StorageException("Failed to store image " + filename + ".",r);
		}
	}
	
	
	/**
	 * Delete an Image from the CDN and the local database.
	 * @param publicId Image's Public Id to be deleted.
	 * @param mode Indicates where the Image should be stored.
	 * @return String indicating the operation was successful.
	 */
	@Override
	public String delete(String publicId, String mode) {
		try {
			// Delete the image in Cloudinary
			if (mode.equals("user")) {
				cloudinary.uploader().destroy("user/"+publicId, ObjectUtils.asMap("invalidate",true));
			} else if (mode.equals("location")) {
				cloudinary.uploader().destroy("location/"+publicId, ObjectUtils.asMap("invalidate",true));
			} else {
				cloudinary.uploader().destroy(""+publicId, ObjectUtils.asMap("invalidate",true));
			}
			
			// Delete image info in local database
			Image result;
			if (mode == "user")
				result = repository.findByPublicId("user/"+publicId);
			else if (mode == "location")
				result = repository.findByPublicId("location/"+publicId);
			else
				result = repository.findByPublicId(publicId);
			repository.delete(result);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RuntimeException r) {
			r.printStackTrace();
		}
		return "Image with public id "+publicId+" was successfully deleted.";
	}
	
	
	/**
	 * Checks if an Image exists.
	 * @param publicId Image's Public Id.
	 * @return True if the Image exists, False otherwise.
	 */
	@Override
	public boolean imgExists(String publicId) {
		// Check if there's image info in local database
		if (repository.findByPublicId(publicId) != null || repository.findByPublicId("location/"+publicId) != null || repository.findByPublicId("user/"+publicId) != null)
			return true;
		else
			return false;
	}
}
