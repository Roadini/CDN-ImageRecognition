package service.storage.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import service.storage.model.Image;
import service.storage.model.Result;
import service.storage.service.StorageService;

@RestController
@RequestMapping("/api/v1")
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
	
	
	//---------------------------------Users--------------------------
	/**
	 * Add a User Image to the CDN.
	 * @param img Image to be stored.
	 * @return A String indicating the Public Id of the stored Image with the Http Status CREATED.
	 */
	@RequestMapping(value="/user/", method=RequestMethod.POST)
	public ResponseEntity<Result> addUserImg(@RequestParam("file") MultipartFile img){
        logger.info("Adding a User Image.");
        
        Result result = new Result("Id: "+storageService.store(img, "user").replaceAll("user/", ""));
        return new ResponseEntity<Result>(result, HttpStatus.CREATED);
    }
	
	
	/**
	 * Retrieve a User Image from the CDN.
	 * @param id The Public Id of the Image to be retrieved.
	 * @return A JSON with the Image's urls to access with the Http Status OK. In case the Image is not found, a String is returned instead with the Http Status NOT FOUND.
	 */
	@RequestMapping(value="/user/{id}", method=RequestMethod.GET)
	public ResponseEntity<?> getUserImg(@PathVariable String id){
        logger.info("Retrieving Image with id {}.", id);
        
        if (!storageService.imgExists(id)) {
            logger.error("Image with id {} not found",id);
            return new ResponseEntity<Result>(new Result("Error: Image with id "+id+" not found"),HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<Image>(storageService.load(id, "user"),HttpStatus.OK);
    }
	
	
	/**
	 * Update a User Image in the CDN.
	 * @param img Image to replace the older one.
	 * @param id Public Id of the Image to be replaced.
	 * @return A String indicating the Public Id of the updated Image with the Http Status CREATED. In case the Image is not found, a String is returned with the Http Status NOT FOUND.
	 */
	@RequestMapping(value="/user/{id}", method=RequestMethod.PUT)
	public ResponseEntity<Result> updateUserImg(@RequestParam("file") MultipartFile img, @PathVariable("id") String id){
        logger.info("Adding Image with id {}.", id);
        
        if (!storageService.imgExists(id)) {
            logger.error("Image with id "+id+" not found.");
            return new ResponseEntity<Result>(new Result("Error: Image with id "+id+" not found."), HttpStatus.NOT_FOUND);
        }
        
        Result result = new Result("Id: "+storageService.update(img, id, "user").replaceAll("user/", ""));
        return new ResponseEntity<Result>(result, HttpStatus.CREATED);
    }
	
	
	/**
	 * Delete a User Image in the CDN.
	 * @param id Public Id of the Image to be deleted.
	 * @return A String indicating the delete operation was a succes with the Http Status OK. In case the image is not found, a String is returned with the Http Status NOT FOUND.
	 */
	@RequestMapping(value="/user/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<Result> deleteUserImg(@PathVariable("id") String id){
        logger.info("Deleting Image with id {}.",id);
        
        if (!storageService.imgExists(id)) {
            logger.error("Image with id {} not found.", id);
            return new ResponseEntity<Result>(new Result("Error: Image with id "+id+" not found."),HttpStatus.NOT_FOUND);
        }
        
        String result = storageService.delete(id,"user");
        return new ResponseEntity<Result>(new Result("Info: "+result), HttpStatus.OK);
    }
	
	
	//----------------------Locations---------------------------
	/**
	 * Add a Location Image to the CDN.
	 * @param img Image to be stored.
	 * @return A String indicating the Public Id of the stored Image with the Http Status CREATED.
	 */
	@RequestMapping(value="/local/", method=RequestMethod.POST)
	public ResponseEntity<Result> addLocationImg(@RequestParam("file") MultipartFile img){
        logger.info("Adding a Location Image.");
        
        Result result = new Result("Id: "+storageService.store(img, "location").replaceAll("location/", ""));
        return new ResponseEntity<Result>(result,HttpStatus.CREATED);
    }
	
	
	/**
	 * Retrieve a Location Image from the CDN.
	 * @param id Public Id of the Image to be retrieved.
	 * @return A JSON with the Image's urls to access with the Http Status OK. In case the Image is not found, a String is returned instead with the Http Status NOT FOUND.
	 */
	@RequestMapping(value="/local/{id}", method=RequestMethod.GET)
	public ResponseEntity<?> getLocationImg(@PathVariable("id") String id){
        logger.info("Retrieving Image with id {}.", id);
        
        if (!storageService.imgExists(id)) {
            logger.error("Image with id {} not found.",id);
            return new ResponseEntity<Result>(new Result("Error: Image with id "+id+" not found."),HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<Image>(storageService.load(id,"location"), HttpStatus.OK);
    }
	
	
	/**
	 * Update a Location Image in the CDN.
	 * @param img Image to replace the older one.
	 * @param id Public Id of the Image to be replaced.
	 * @return A String indicating the Public Id of the updated Image with the Http Status CREATED. In case the Image is not found, a String is returned with the Http Status NOT FOUND.
	 */
	@RequestMapping(value="/local/{id}", method=RequestMethod.PUT)
	public ResponseEntity<Result> updateLocationImg(@RequestParam("file") MultipartFile img, @PathVariable("id") String id){
        logger.info("Updating Image with id {}.", id);
        
        if (!storageService.imgExists(id)) {
            logger.error("Image with id {} not found", id);
            return new ResponseEntity<Result>(new Result("Error: Image with id "+id+" not found"), HttpStatus.NOT_FOUND);
        }
        
        Result result = new Result("Id: "+storageService.update(img, id, "location").replaceAll("location/", ""));
        return new ResponseEntity<Result>(result, HttpStatus.CREATED);
    }
	
	
	/**
	 * Delete a Location Image in the CDN.
	 * @param id Public Id of the Image to be deleted.
	 * @return A String indicating the delete operation was a succes with the Http Status OK. In case the image is not found, a String is returned with the Http Status NOT FOUND.
	 */
	@RequestMapping(value="/local/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<Result> deleteLocationImg(@PathVariable String id){
        logger.info("Deleting Image with id {}.",id);
        
        if (!storageService.imgExists(id)) {
            logger.error("Image with id {} not found.", id);
            return new ResponseEntity<Result>(new Result("Error: Image with id "+id+" not found."),HttpStatus.NOT_FOUND);
        }
        
        String result = storageService.delete(id,"location");
        return new ResponseEntity<Result>(new Result("Info: "+result), HttpStatus.OK);
    }
}
