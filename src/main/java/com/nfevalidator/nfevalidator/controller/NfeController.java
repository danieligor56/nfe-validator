package com.nfevalidator.nfevalidator.controller;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.nfevalidator.nfevalidator.services.AiIntegration;
import com.nfevalidator.nfevalidator.services.AiIntegration;
import com.nfevalidator.nfevalidator.services.UtilCapt;

@RestController
@RequestMapping("/api/")
public class NfeController {
	
	@Autowired
	UtilCapt util;
	 
	
	 
	@PostMapping("/xmlnfe=/")
	@CrossOrigin(origins = "http://localhost:4200")
	public ResponseEntity recXmlNfe(@RequestBody String xml) throws BadRequestException, Exception {
		
		return  ResponseEntity.ok().body(util.WebDriverManager(xml));
	}
	
	
	/*
	 * @GetMapping("/prompt") public String getResponse(RequestBody String) { return
	 * aiIntegration.callApi(prompt);
	 * 
	 * }
	 */
	 
	

	
}
