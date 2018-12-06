package service.storage.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "images")
public class Image {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String publicId;
	private String url;
	private String secure_url;
	
	public Image() {
	}
	
	/**
	* Creates an instance of Image with the specified Public Id and URLs.
	* @param publicId Public Id of the Image.
	* @param url URL to the Image.
	* @param secure_url URL to the Image, pointing to a link with HTTPS protocol.
	*/
	public Image(String publicId, String url, String secure_url) {
		this.publicId = publicId;
		this.url = url;
		this.secure_url = secure_url;
	}
	
	/**
	* Returns the Image's Id.
	* @returns Image's Id.
	*/
	public Long getId() {
		return id;
	}
	
	/**
	* Sets the Image's Id.
	* @param id Image's Id.
	*/
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	* Returns the Image's Public Id.
	* @returns Image's Public Id.
	*/
	public String getPublicId() {
		return publicId;
	}
	
	/**
	* Sets the Image's Public Id.
	* @param publicId Image's Public Id.
	*/
	public void setPublicId(String publicId) {
		this.publicId = publicId;
	}
	
	/**
	* Returns the Image's URL.
	* @returns Image's URL.
	*/
	public String getUrl() {
		return url;
	}
	
	/**
	* Sets the Image's URL.
	* @param url Image's URL.
	*/	
	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	* Returns the Image's URL, pointing to a link with HTTPS protocol.
	* @returns Image's URL, pointing to a link with HTTPS protocol.
	*/	
	public String getSecureUrl() {
		return secure_url;
	}
	
	/**
	* Sets the Image's URL, pointing to a link with HTTPS protocol.
	* @param secure_url Image's URL, pointing to a link with HTTPS protocol.
	*/	
	public void setSecureUrl(String secure_url) {
		this.secure_url = secure_url;
	}
	
	@Override
	public String toString() {
		return "Image{pubicId="+publicId+",url="+url+",secure_url="+secure_url+"}";
	}
}
