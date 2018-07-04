package com.github.megachucky.kafka.streams.machinelearning;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.UriTemplate;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.discovery.Discovery;
import com.google.api.services.discovery.model.JsonSchema;
import com.google.api.services.discovery.model.RestDescription;
import com.google.api.services.discovery.model.RestMethod;

import com.google.api.gax.paging.Page;
import com.google.auth.appengine.AppEngineCredentials;
import com.google.auth.oauth2.ComputeEngineCredentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.common.collect.Lists;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Kafka_Streams_TensorFlow_Serving_Google_ML_Engine_Example {

	
//	static void authExplicit(String jsonPath) throws IOException {
//		  // You can specify a credential file by providing a path to GoogleCredentials.
//		  // Otherwise credentials are read from the GOOGLE_APPLICATION_CREDENTIALS environment variable.
//		  credentials = GoogleCredentials.fromStream(new FileInputStream(jsonPath))
//		        .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
//		  Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
//
//		  System.out.println("Buckets:");
//		  Page<Bucket> buckets = storage.list();
//		  for (Bucket bucket : buckets.iterateAll()) {
//		    System.out.println(bucket.toString());
//		  }
//		}
	
	
	static void authImplicit() {
		  // If you don't specify credentials when constructing the client, the client library will
		  // look for credentials via the environment variable GOOGLE_APPLICATION_CREDENTIALS.
		  Storage storage = StorageOptions.getDefaultInstance().getService();

		  System.out.println("Buckets:");
		  Page<Bucket> buckets = storage.list();
		  for (Bucket bucket : buckets.iterateAll()) {
		    System.out.println(bucket.toString());
		  }
		}
	
	public static void main(String[] args) throws Exception {
//		authExplicit("/Users/kai.waehner/Google Drive/Confluent_Kai/kai-waehner-project-8aad9356ffa2.json");
		authImplicit();
		
		
		HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
	    JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
	    Discovery discovery = new Discovery.Builder(httpTransport, jsonFactory, null).build();

	    RestDescription api = discovery.apis().getRest("ml", "v1").execute();
	    RestMethod method = api.getResources().get("projects").getMethods().get("predict");

	    JsonSchema param = new JsonSchema();
	    String projectId = "kai-waehner-project-mlengine";
	    // String projectId = "kai-waehner-project";
	    
	    // You should have already deployed a model and a version.
	    // For reference, see https://cloud.google.com/ml-engine/docs/deploying-models.
	    String modelId = "census";
	    String versionId = "v1";
	    param.set(
	        "name", String.format("projects/%s/models/%s/versions/%s", projectId, modelId, versionId));

	    GenericUrl url =
	        new GenericUrl(UriTemplate.expand(api.getBaseUrl() + method.getPath(), param, true));
	    System.out.println(url);

	    String contentType = "application/json";
	    File requestBodyFile = new File("src/main/resources/generatedModels/TensorFlow_Census/test.json");
	    HttpContent content = new FileContent(contentType, requestBodyFile);
	    System.out.println(content.getLength());

	    GoogleCredential credential = GoogleCredential.getApplicationDefault();
	    HttpRequestFactory requestFactory = httpTransport.createRequestFactory(credential);
	    HttpRequest request = requestFactory.buildRequest(method.getHttpMethod(), url, content);

	    String response = request.execute().parseAsString();
	    System.out.println(response);
	  }
	
}
