package service.recognizer.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageAnnotatorSettings;
import com.google.cloud.vision.v1.LocationInfo;
import com.google.cloud.vision.v1.WebDetection;
import com.google.cloud.vision.v1.WebDetection.WebLabel;
import com.google.common.collect.Lists;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.protobuf.ByteString;

import service.recognizer.model.Information;

@Service
public class Recognizer implements RecognitionService{
	
	private final ImageAnnotatorClient vision;
	private final GoogleCredentials credentials;
	
	@Autowired
	public Recognizer() throws Exception{
		//Set Cloud Vision credentials
		credentials = GoogleCredentials.fromStream(new FileInputStream("src/main/resources/credentials.json"))
				.createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
		ImageAnnotatorSettings imageAnnotatorSettings = ImageAnnotatorSettings.newBuilder()
	         .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
	         .build();
		//Create Cloud Vision client
		vision = ImageAnnotatorClient.create(imageAnnotatorSettings);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Information recognizeThis(MultipartFile file) {
		Information info = new Information();
		try {
			//Create list of requests
			List<AnnotateImageRequest> requests = new ArrayList<>();
			//Retrieve Bytes from Image
			ByteString imgBytes = ByteString.copyFrom(file.getBytes());
			Image img = Image.newBuilder().setContent(imgBytes).build();
			//Specify which info to retrieve from Image
			Feature featLand = Feature.newBuilder().setType(Type.LANDMARK_DETECTION).build();
			Feature featWeb = Feature.newBuilder().setType(Type.WEB_DETECTION).build();
			//Build the request
			AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
					.addFeatures(featLand)
					.addFeatures(featWeb)
					.setImage(img)
					.build();
			//Add request to list of requests
			requests.add(request);
			
			//Send list of requests to Cloud Vision
			BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
			//Retrieve info from Cloud Vision
			List<AnnotateImageResponse> responses = response.getResponsesList();
			
			//Process info from Cloud Vision
			for (AnnotateImageResponse res : responses) {
				String subject = "";
				//Retrieve Best Guess label
				WebDetection web = res.getWebDetection();
				for(WebLabel w: web.getBestGuessLabelsList())
					info.setLabel(w.getLabel());
				//Retrieve most probable Landmark info
				EntityAnnotation landmark = res.getLandmarkAnnotationsList().get(0);
				//Retrieve Description info
				subject = landmark.getDescription();
				info.setDescription(subject);
				//Retrieve Location info
				for (LocationInfo l : landmark.getLocationsList()) {
					info.setLatitude(l.getLatLng().getLatitude());
					info.setLongitude(l.getLatLng().getLongitude());
				}
				//Create an url to the wikipedia page
				URL url = new URL("https://en.wikipedia.org/w/api.php?action=query&prop=extracts&format=json&exsentences=1&exintro=&explaintext=&exsectionformat=plain&titles=" + subject.replace(" ", "%20"));
				String text = "";
				//Store the contents from the wikipedia page
				try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()))) {
				    String line = null;
				    //Read until the end
				    while ((line = br.readLine()) != null) {
				    	//Trim the initial and final white spaces
				        text += line.trim();
				    }
				}
				//Process the json received from wikipedia
				JSONObject json = new JSONObject(text);
				//Retrieve the pages information
				JSONObject pages = json.getJSONObject("query").getJSONObject("pages");
				//Create an iterator for the keys
				Iterator<String> keys = pages.keys();
				while(keys.hasNext()) {
				    String extract = pages.getJSONObject(keys.next()).getString("extract");
				    info.setExtract(extract);
				}
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return info;
	}
}
