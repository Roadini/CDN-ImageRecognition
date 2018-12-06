package service.storage.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import service.storage.util.StorageException;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

@Service
public class ImgSystemStorageService implements StorageService{
	
	private Path rootLocation;
	
	/**
	 * Creates an instance of ImgSystemStorageService to handle the operations involved with Images.
	 */
	@Autowired
	public ImgSystemStorageService() {
		rootLocation = Paths.get("imgs");
	}
	
	@Override
	public void init() {
		try {
			Files.createDirectories(rootLocation);
		}catch (IOException e) {
			throw new StorageException("Could not initialize storage", e);
		}
	}
	
	@Override
	public String store(MultipartFile img) {
		String filename = StringUtils.cleanPath(img.getOriginalFilename());
		// Check if file is empty
        if (img.isEmpty())
            throw new StorageException("Failed to store empty Image " + filename + ".");
        // Security check
        if (filename.contains(".."))
            throw new StorageException("Failed to store Image " + filename +" with invalid name.");
        try {
            String imgId = RandomStringUtils.randomAlphanumeric(20);
            File destination = new File(rootLocation+"/"+imgId+".jpg");
            destination.createNewFile();
            FileOutputStream fos = new FileOutputStream(destination);
            fos.write(img.getBytes());
            fos.close();
            return imgId;
        } catch (Exception e) {
        	throw new StorageException("Failed to store Image.", e);
        }
	}
	
	@Override
	public Resource load(String imgId) {
		Resource result;
		try {	
			Path img = rootLocation.resolve(imgId+".jpg");
			result = new UrlResource(img.toUri());
		} catch (Exception e) {
			throw new StorageException("Image with id "+imgId+" could not be loaded.");
		}
		if (result.exists())
			if (result.isReadable())
				return result;
			else
				throw new StorageException("Image with id "+imgId+" could not be read.");
		else
			throw new StorageException("Image with id "+imgId+" not in local Database.");
	}
	
	@Override
	public String update(MultipartFile img, String imgId) {
		String filename = StringUtils.cleanPath(img.getOriginalFilename());
		// Check if image is empty
		if (img.isEmpty())
			throw new StorageException("Failed to store empty Image " + filename + ".");
		// Security check
		if (filename.contains(".."))
			throw new StorageException("Failed to store Image " + filename +" with invalid name.");
		try {
			File destination = new File(rootLocation+"/"+imgId+".jpg");
			if (destination.exists())
				destination.delete();
			destination.createNewFile();
            FileOutputStream fos = new FileOutputStream(destination);
            fos.write(img.getBytes());
            fos.close();
			return "Image with id "+imgId+" was successfully updated.";
		}catch (Exception e) {
			throw new StorageException("Failed to store image.",e);
		}
	}
	
	@Override
	public String delete(String imgId) {
		try {
			File file = new File(rootLocation+"/"+imgId+".jpg");
			if (file.delete())
				return "Image with id "+imgId+" was successfully deleted.";
			else
				throw new StorageException("Could not delete requested Image.");
		} catch (Exception e) {
			throw new StorageException("Could not delete requested Image.",e);
		}
	}	
	
	/**
	 * Checks if an Image exists.
	 * @param publicId Image's Public Id.
	 * @return True if the Image exists, False otherwise.
	 */
	@Override
	public boolean imgExists(String imgId) {
		// Check if there's image info in local database
		File file = new File(rootLocation+"/"+imgId+".jpg");
		return file.exists();
	}
}
