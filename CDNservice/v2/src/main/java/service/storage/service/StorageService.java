package service.storage.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

	String store(MultipartFile img);
	
	Resource load(String id);
	
	String update(MultipartFile img, String id);
	
	String delete(String id);

	boolean imgExists(String id);

	void init();
}
