package service.recognizer.service;

import org.springframework.web.multipart.MultipartFile;

import service.recognizer.model.Information;

public interface RecognitionService {
	public Information recognizeThis(MultipartFile file);
}
