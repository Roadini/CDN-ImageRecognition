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
                throw new StorageException("Error: Failed to store empty image " + filename + ".");
            // Security check
            if (filename.contains(".."))
                throw new StorageException("Error: Failed to store image. Image " + filename +" has an invalid name.");
            
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
        	Image image = new Image(uploadResult.get("public_id").toString().replace(mode+"/", ""),uploadResult.get("url").toString(),uploadResult.get("secure_url").toString());
        	repository.save(image);
            
        	return uploadResult.get("public_id").toString();
        }catch (IOException e) {
            throw new StorageException("Error: Failed to store image " + filename + ".", e);
        }catch (RuntimeException r) {
        	throw new StorageException("Error: Failed to store image " + filename + ".", r);
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
		result = repository.findByPublicId(publicId);
		
		// Check if the result is null
		if (result != null)
			return result;
		else
			throw new StorageException("Error: Image with public id "+publicId+" not found.");
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
		
		try {
			// Check if image is empty
			if (img.isEmpty())
				throw new StorageException("Error: Failed to empty image " + filename + ".");
			// Security check
			if (filename.contains(".."))
				throw new StorageException("Error: Failed to store image. Image " + filename +" has an invalid name.");
			
			// Delete the image in Cloudinary
			if (mode.equals("user")) {
				cloudinary.uploader().destroy("user/"+publicId, ObjectUtils.asMap("invalidate",true));
				params = ObjectUtils.asMap("folder", "user", "public_id", ""+publicId);
			} else if (mode.equals("location")) {
				cloudinary.uploader().destroy("location/"+publicId, ObjectUtils.asMap("invalidate",true));
				params = ObjectUtils.asMap("folder", "location", "public_id", ""+publicId);
			} else {
				cloudinary.uploader().destroy(""+publicId, ObjectUtils.asMap("invalidate",true));
				params = ObjectUtils.emptyMap();
			}
			
			// Update the image in cloudinary
			if (img.getSize()<=1024000)
        		uploadResult = cloudinary.uploader().upload(img.getBytes(), params);
        	else 
        		uploadResult = cloudinary.uploader().uploadLarge(img.getBytes(), params);
			
			// Update the local database
			Image res = repository.findByPublicId(publicId);
			res.setPublicId(publicId);
			res.setUrl(uploadResult.get("url").toString());
			res.setSecureUrl(uploadResult.get("secure_url").toString());
			repository.save(res);
			
			return uploadResult.get("public_id").toString();
		} catch (IOException e) {
			throw new StorageException("Error: Failed to store image " + filename + ".",e);
		} catch (RuntimeException r) {
			throw new StorageException("Error: Failed to store image " + filename + ".",r);
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
			result = repository.findByPublicId(publicId);
			repository.delete(result);
		} catch (IOException e) {
			throw new StorageException("Error: Failed to delete image with id " + publicId + ".",e);
		} catch (RuntimeException r) {
			throw new StorageException("Error: Failed to delete image with id " + publicId + ".",r);
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
		if (repository.findByPublicId(publicId) != null)
			return true;
		else
			return false;
	}
}
