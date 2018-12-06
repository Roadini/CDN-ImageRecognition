package service.storage.service;

import org.springframework.web.multipart.MultipartFile;

import service.storage.model.Image;

public interface StorageService {

	String store(MultipartFile img, String mode);
	
	Image load(String id, String mode);
	
	String update(MultipartFile img, String id, String mode);
	
	String delete(String id, String mode);

	boolean imgExists(String id);
}
