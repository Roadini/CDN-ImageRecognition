package service.recognizer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import service.recognizer.model.Information;
import service.recognizer.model.Result;
import service.recognizer.service.RecognitionService;

@RestController
@RequestMapping("/api/v1")
public class IRController {

	public static final Logger logger = LoggerFactory.getLogger(IRController.class);
	
	private final RecognitionService recon;
	
	@Autowired
	public IRController(RecognitionService recon) {
		this.recon = recon;
	}
	
	@RequestMapping(value="/recognize/", method=RequestMethod.POST)
	public ResponseEntity<?> recognizeImage(@RequestParam("file") MultipartFile img) {
		logger.info("Analysing the Image");
		
		if (img.isEmpty()) {
			logger.error("Image is empty.");
			return new ResponseEntity<Result>(new Result("Image is empty."), HttpStatus.BAD_REQUEST);
		}
		
		Information result = recon.recognizeThis(img);
		return new ResponseEntity<Information>(result,HttpStatus.OK);
	}
}