package it.unisalento.sonoffbackend.restController;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
/*
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.token.TokenManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import model.AccessToken;
import model.Credential;
import okio.BufferedSink;
 */
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.admin.client.token.TokenManager;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.github.underscore.U;
import com.github.underscore.Json.JsonObject;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;


import it.unisalento.sonoffbackend.model.Credential;
import it.unisalento.sonoffbackend.model.User;

import java.io.IOException;
//import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

@org.springframework.web.bind.annotation.RestController
public class RestController {
	OkHttpClient client = new OkHttpClient();
	@Value("${keycloak.resource}")
	private String keycloakClient;

	@Value("${keycloak.auth-server-url}")
	private String keycloakUrl;

	@Value("${keycloak.realm}")
	private String keycloakRealm;

	@Value("${is.keycloak.admin.user}")
	private String keycloakAdminUser;

	@Value("${is.keycloak.admin.password}")
	private String keycloakAdminPassword;

	private Keycloak getKeycloakInstance() {
		return Keycloak.getInstance(
				keycloakUrl,
				"master",
				keycloakAdminUser,
				keycloakAdminPassword,
				"admin-cli");
	}


	//TODO: indirizzi ip
	String host = "http://localhost:8081/";
	//STUDIUM
	//String authAddress = "http://10.20.72.9:8180/auth/realms/master/protocol/openid-connect/token";
	//CASA
	//String authAddress = "http://192.168.1.100:8180/auth/realms/master/protocol/openid-connect/token";
	//HOTSPOT
	String authAddress = "http://172.20.10.4:8180/auth/realms/MyRealm/protocol/openid-connect/userinfo";
	String refreshAddress="http://172.20.10.4:8180/auth/realms/MyRealm/protocol/openid-connect/token";

	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "changeStatusOFF/{clientId}/{input}", method = RequestMethod.POST)
	public ResponseEntity<User> changeStatusOFF(@PathVariable("clientId") String clientId, @PathVariable("input") int input, @RequestBody User user) throws Exception {
		
		com.squareup.okhttp.MediaType JSON = com.squareup.okhttp.MediaType.parse("application/json; charset=utf-8");
		JSONObject jsonObj = new JSONObject();
		
		jsonObj.put("username", user.getUsername());
		jsonObj.put("role", user.getRole());
		jsonObj.put("token", user.getToken());
		jsonObj.put("refreshToken", user.getRefreshToken());
		
		com.squareup.okhttp.RequestBody body = com.squareup.okhttp.RequestBody.create(JSON, jsonObj.toString());
		
		Request request = new Request.Builder().url(host+"changeStatusOFF/"+clientId+"/"+input)
				.post(body)
				.build();
		Response response = client.newCall(request).execute();
		
		if(response.isSuccessful()) {
			JSONParser parser = new JSONParser();  
			try {
				JSONObject json = (JSONObject) parser.parse(response.body().string());  
				user.setToken(json.get("token").toString());
				user.setRefreshToken(json.get("refreshToken").toString());
				return new ResponseEntity<>(user, HttpStatus.valueOf(response.code()));

			}catch (Exception e) {
				return new ResponseEntity<>(null, HttpStatus.valueOf(response.code()));
			}	
		}
		else {
			return new ResponseEntity<>(null, HttpStatus.valueOf(response.code()));
		}
		
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "changeStatusON/{clientId}/{input}", method = RequestMethod.POST)
	public ResponseEntity<User> changeStatusON(@PathVariable("clientId") String clientId, @PathVariable("input") int input, @RequestBody User user) throws Exception {
		com.squareup.okhttp.MediaType JSON = com.squareup.okhttp.MediaType.parse("application/json; charset=utf-8");
		JSONObject jsonObj = new JSONObject();
		
		jsonObj.put("username", user.getUsername());
		jsonObj.put("role", user.getRole());
		jsonObj.put("token", user.getToken());
		jsonObj.put("refreshToken", user.getRefreshToken());
		
		com.squareup.okhttp.RequestBody body = com.squareup.okhttp.RequestBody.create(JSON, jsonObj.toString());
		
		Request request = new Request.Builder().url(host+"changeStatusON/"+clientId+"/"+input)
				.post(body)
				.build();
		Response response = client.newCall(request).execute();
		if(response.isSuccessful()) {
			JSONParser parser = new JSONParser();  
			try {
				JSONObject json = (JSONObject) parser.parse(response.body().string());  
				user.setToken(json.get("token").toString());
				user.setRefreshToken(json.get("refreshToken").toString());
				return new ResponseEntity<>(user, HttpStatus.valueOf(response.code()));

			}catch (Exception e) {
				return new ResponseEntity<>(null, HttpStatus.valueOf(response.code()));
			}	
		}
		else {
			return new ResponseEntity<>(null, HttpStatus.valueOf(response.code()));
		}
	}	

	@SuppressWarnings("unchecked")
	@RequestMapping(value="getStatus/{clientId}/{input}", method = RequestMethod.POST) 
	public ResponseEntity<String> getStatus(@PathVariable("clientId") String clientId, @PathVariable("input") int input, @RequestBody User user) throws Exception{
		
		com.squareup.okhttp.MediaType JSON = com.squareup.okhttp.MediaType.parse("application/json; charset=utf-8");
		JSONObject jsonObj = new JSONObject();
		
		jsonObj.put("username", user.getUsername());
		jsonObj.put("role", user.getRole());
		jsonObj.put("token", user.getToken());
		jsonObj.put("refreshToken", user.getRefreshToken());
		
		com.squareup.okhttp.RequestBody body = com.squareup.okhttp.RequestBody.create(JSON, jsonObj.toString());
		Request request = new Request.Builder().url(host+"getStatus/"+clientId+"/"+input)
				.post(body)
				.build();
		Response response = client.newCall(request).execute();
		if(response.isSuccessful()) {
			JSONParser parser = new JSONParser();
			JSONObject jsonResp = (JSONObject) parser.parse(response.body().string());
			System.out.println(jsonResp);
			
			/*if(status==null) {
				System.out.println("Something went wrong while getting status");
				return new ResponseEntity<String>(HttpStatus.valueOf(response.code()));
			}*/
			
			return new ResponseEntity<>(jsonResp.toString(), HttpStatus.valueOf(response.code()));
		}
		else {
			return new ResponseEntity<>(null, HttpStatus.valueOf(response.code()));

		}
	}

	@RequestMapping(value="auth", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> getAccessToken(@RequestBody Credential credential) {
		String accessToken = null;
		String refreshToken = null;
		try {
			Keycloak instance = Keycloak.getInstance(keycloakUrl, "MyRealm", credential.getUsername(), credential.getPassword(), "backend", "eLFYzBFFDlJrA9dTmNPnkTwhiipyB8x8");                                                                                                      
			TokenManager tokenmanager = instance.tokenManager();
			accessToken = tokenmanager.getAccessTokenString();
			if(accessToken == null) {
				return new ResponseEntity<User>(HttpStatus.FORBIDDEN);
			}
			refreshToken = tokenmanager.getAccessToken().getRefreshToken();
		}
		catch (javax.ws.rs.NotAuthorizedException e) {
		}
		User user = new User();
		
		String role = getRolesByUser(credential.getUsername());
		if(role!=null) {
			user.setUsername(credential.getUsername());
			user.setToken(accessToken);
			user.setRole(role);
			user.setRefreshToken(refreshToken);
			return new ResponseEntity<User>(user, HttpStatus.OK);
		}

		return new ResponseEntity<User>(HttpStatus.INTERNAL_SERVER_ERROR); 
	}

	public String getRolesByUser(String username){ 
		Keycloak keycloak = getKeycloakInstance();
		Optional<UserRepresentation> user = keycloak.realm(keycloakRealm).users().search(username).stream()
				.filter(u -> u.getUsername().equals(username)).findFirst();
		if (user.isPresent()) {
			UserRepresentation userRepresentation = user.get();
			UserResource userResource = keycloak.realm(keycloakRealm).users().get(userRepresentation.getId());
			ClientRepresentation clientRepresentation = keycloak.realm(keycloakRealm).clients().findByClientId(keycloakClient).get(0);
			List<RoleRepresentation> roles = userResource.roles().clientLevel(clientRepresentation.getId()).listAll();
			return roles.get(0).getName();

		} else {
			return null;
		}
	}

	@PostMapping("/createUser/{username}/{password}/{userRole}")
	public ResponseEntity<User> createUser(@PathVariable("username") String username, @PathVariable("password") String password, @PathVariable("userRole") String userRole, @RequestBody User user) {
		User tempUser;
		if((tempUser = checkToken(user))!=null && user.getRole().equals("admin")) { //checking token and role
			if (username.isBlank() || password.isBlank()) {
				
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
						//ResponseEntity.badRequest().body("Empty username or password");
			}
			CredentialRepresentation credentials = new CredentialRepresentation();
			credentials.setType(CredentialRepresentation.PASSWORD);
			credentials.setValue(password);
			credentials.setTemporary(false);
			UserRepresentation userRepresentation = new UserRepresentation();
			userRepresentation.setUsername(username);
			userRepresentation.setCredentials(Arrays.asList(credentials));
			userRepresentation.setEnabled(true);
			Map<String, List<String>> attributes = new HashMap<>();
			attributes.put("description", Arrays.asList("A test user"));
			userRepresentation.setAttributes(attributes);
			Keycloak keycloak = getKeycloakInstance();
			javax.ws.rs.core.Response result = keycloak.realm(keycloakRealm).users().create(userRepresentation);
			
			
			List<UserRepresentation> userList = keycloak.realm(keycloakRealm).users().search(username).stream()
	                .filter(userRep -> userRep.getUsername().equals(username)).collect(Collectors.toList());
			
	        userRepresentation = userList.get(0);
	        
			assignRoleToUser(userRepresentation.getId(), userRole);
			
			if(user == tempUser) {
				return new ResponseEntity<>(null, HttpStatus.valueOf(result.getStatus()));
			}
			else {
				return new ResponseEntity<>(user, HttpStatus.valueOf(result.getStatus()));

			}
		}
		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

	}
	
	private void assignRoleToUser(String userId, String role) {
        Keycloak keycloak = getKeycloakInstance();
        UsersResource usersResource = keycloak.realm(keycloakRealm).users();
        UserResource userResource = usersResource.get(userId);

        //getting client
        ClientRepresentation clientRepresentation = keycloak.realm(keycloakRealm).clients().findAll().stream().filter(client -> client.getClientId().equals(keycloakClient)).collect(Collectors.toList()).get(0);
        ClientResource clientResource = keycloak.realm(keycloakRealm).clients().get(clientRepresentation.getId());
        //getting role
        RoleRepresentation roleRepresentation = clientResource.roles().list().stream().filter(element -> element.getName().equals(role)).collect(Collectors.toList()).get(0);
        //assigning to user
        userResource.roles().clientLevel(clientRepresentation.getId()).add(Collections.singletonList(roleRepresentation));
    }

	private User checkToken(User user) {
		Request request = new Request.Builder()
				.url(authAddress)
				.header("Content-Type", "application/json")
				.header("Authorization","Bearer "+ user.getToken())
				.get()
				.build();
		Response response;
		try {
			response = client.newCall(request).execute();
			if (response.isSuccessful()) {
				return user;
			}
			else {
				user = executeRefresh(user);
				return user;
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public User executeRefresh(User user) {
		com.squareup.okhttp.RequestBody requestBody = new FormEncodingBuilder()
	    	     .add("grant_type", "refresh_token")
	    	     .add("refresh_token", user.getRefreshToken())
	    	     .add("client_id", keycloakClient)
	    	     .add("client_secret", "eLFYzBFFDlJrA9dTmNPnkTwhiipyB8x8")
	    	     .build();
	    
	    Request request = new Request.Builder()
	    		.url(refreshAddress)
	    		.post(requestBody)
	            .build();
	    Response response;
	    try {
	    	response = client.newCall(request).execute();
	    	if(response.isSuccessful()) {
	    		JSONParser parser = new JSONParser();  
	    		JSONObject json = (JSONObject) parser.parse(response.body().string());  
	    		System.out.println(json);
	    		user.setToken(json.get("access_token").toString());
	    		user.setRefreshToken(json.get("refresh_token").toString());  
		    	return user;
	    	}
	    	else {
	    		return null;
	    	}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
