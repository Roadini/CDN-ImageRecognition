package service.storage.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import service.storage.model.Result;
import service.storage.service.StorageService;

@RestController
@RequestMapping("/api/v2")
public class CdnApiController {

	public static final Logger logger = LoggerFactory.getLogger(CdnApiController.class);
	
	private final StorageService storageService;
	
	/**
	* Creates a Controller for the API that uses the specified StorageService.
	* @param storageService Storage service to process the storage of images.
	*/
	@Autowired
	public CdnApiController(StorageService storageService) {
		this.storageService = storageService;
	}
	
	/**
	 * 
	 * @param img Image to be stored
	 * @return A JSON with the image id and an Http status
	 */
	@RequestMapping(value="/", method=RequestMethod.POST)
	public ResponseEntity<Result> addImg(@RequestParam("file") MultipartFile img){
		logger.info("Adding an Image to the Database.");
		try {
			Result result = new Result("Image id: "+storageService.store(img));
			return new ResponseEntity<Result>(result, HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Result>(new Result("Error: Could not store the requested Image."), HttpStatus.CONFLICT);
		}
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getImg(@PathVariable("id") String id, Model model){
		logger.info("Retrieving Image with id {} from Database.", id);
		if (!storageService.imgExists(id)) {
			logger.error("Image with id {} not found.",id);
			return new ResponseEntity<Result>(new Result("Error: Image with id "+id+" not found."),HttpStatus.NOT_FOUND);
		}
		try {
			Resource resource = storageService.load(id);
			return ResponseEntity
					.ok()
					.contentType(MediaType.IMAGE_JPEG)
					.body(new InputStreamResource(resource.getInputStream()));
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Result>(new Result("Error: Could not return the requested Image."), HttpStatus.CONFLICT);
		}
	}
	
	/**
	 * 
	 * @param img
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/{id}", method=RequestMethod.PUT)
	public ResponseEntity<Result> updateImg(@RequestParam("file") MultipartFile img, @PathVariable("id") String id){
		logger.info("Updating Image with id {} from Database.", id);
		if (!storageService.imgExists(id)) {
			logger.error("Image with id "+id+" not found.");
			return new ResponseEntity<Result>(new Result("Error: Image with id "+id+" not found."), HttpStatus.NOT_FOUND);
		}
		try {
			Result result = new Result("Image with id "+storageService.update(img, id)+" was successfully updated.");
			return new ResponseEntity<Result>(result, HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Result>(new Result("Error: Could not update the requested Image."), HttpStatus.CONFLICT);
		}
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<Result> deleteImg(@PathVariable("id") String id){
		logger.info("Deleting Image with id {} from Database.",id);
		if (!storageService.imgExists(id)) {
			logger.error("Image with id {} not found.", id);
			return new ResponseEntity<Result>(new Result("Error: Image with id "+id+" not found."),HttpStatus.NOT_FOUND);
		}
		try {
			String result = storageService.delete(id);
			return new ResponseEntity<Result>(new Result("Success: "+result), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Result>(new Result("Error: Could not delete the requested Image."), HttpStatus.CONFLICT);
		}
	}
}
