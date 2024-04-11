package com.nfevalidator.nfevalidator.services;

import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.BadRequestException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;
import com.nfevalidator.nfevalidator.entity.CorpNFE;

@Service
public class UtilCapt {
	
	CorpNFE corp = new CorpNFE();
	
	public String WebDriverManager(String xml) throws Exception  {
		
		System.setProperty("webdriver.chrome.driver","src/main/resources/chromedriver.exe");
		
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless", "--ignore-certificate-errors");
		WebDriver driver = new ChromeDriver(options);
		
		try {
			
			driver.get("https://www.sefaz.rs.gov.br/nfe/nfe-val.aspx");
			
			WebElement insertXML = driver.findElement(By.id("txtxml"));
			insertXML.sendKeys(xml);
		
			WebElement validButton = driver.findElement(By.xpath("//*[@id=\"btnvalidar\"]"));
			validButton.click();
			
			String pageSource = driver.getPageSource();
			Document document = Jsoup.parse(pageSource);
			
			//REGRA DE NEGOCIOS:
			String titResult = document.selectXpath("//*[@id=\"resultado\"]/table//tr[1]/td").text();
			corp.setTitResult(titResult);
			String RespParserXml = document.selectXpath("/html/body/div[2]/form/table//tr[1]/td/table//tr/td[2]/div/div[2]/div/table//tr[2]/td/div/div[1]/span[2]/table//tr[2]/td/ul/li[1]").text();
			corp.setRespParserXml(RespParserXml);
			String tipMsmString = document.selectXpath("/html/body/div[2]/form/table//tr[1]/td/table//tr/td[2]/div/div[2]/div/table//tr[2]/td/div/div[1]/span[2]/table//tr[2]/td/ul/li[2]").text();
			corp.setTipMsmString(tipMsmString);
			String schValid = document.selectXpath("/html/body/div[2]/form/table//tr[1]/td/table//tr/td[2]/div/div[2]/div/table//tr[2]/td/div/div[1]/span[2]/table//tr[2]/td/ul/li[3]").text();											
			corp.setSchValid(schValid);
			
			//VALIDAÇÃO NFE:
			String titValidNfe = document.selectXpath("/html/body/div[2]/form/table//tr[1]/td/table//tr/td[2]/div/div[2]/div/table//tr[2]/td/div/div[1]/span[2]/table//tr[2]/td/ul/li[4]/a/b").text();
			corp.setTitValidNfe(titValidNfe);
			String certUser = document.selectXpath("/html/body/div[2]/form/table//tr[1]/td/table//tr/td[2]/div/div[2]/div/table//tr[2]/td/div/div[1]/span[2]/table//tr[2]/td/ul/li[4]/ul/li[1]").text();
			corp.setCertUser(certUser);
			String validCert = document.selectXpath("/html/body/div[2]/form/table//tr[1]/td/table//tr/td[2]/div/div[2]/div/table//tr[2]/td/div/div[1]/span[2]/table//tr[2]/td/ul/li[4]/ul/li[2]/b").text();
			corp.setValidCert(validCert);
			
			//REGRS DE NEG AMB PRODUÇÃO: 
			String regNegProd = document.selectXpath("/html/body/div[2]/form/table//tr[1]/td/table//tr/td[2]/div/div[2]/div/table//tr[2]/td/div/div[1]/span[2]/table//tr[2]/td/ul/li[4]/ul/li[3]/div").text();
			corp.setRegNegProd(regNegProd);
			String probValida = document.selectXpath("/html/body/div[2]/form/table//tr[1]/td/table//tr/td[2]/div/div[2]/div/table//tr[2]/td/div/div[1]/span[2]/table//tr[2]/td/ul/li[4]/ul/li[3]/ul").text();	
			corp.setProbValida(probValida);
			
			
			List<String> errList = getMovs(document) ;
			corp.setListErrs(errList);
			
			CorpNFE nfe = new CorpNFE(titResult,RespParserXml,tipMsmString,schValid,titValidNfe,certUser,validCert,
			regNegProd,probValida,errList);
			//+corp.getMovs(document)
			return nfe.toString();
			
		} catch (Exception e) {
			
			throw new BadRequestException("Não foi possível atender a solicitação no momento",e);
		}
		
		finally {
			driver.quit();
		}
		
	}
	
	public List<String> getMovs(Document document) { 
		 
		List<String>movsList = new ArrayList<>();
		 
		 int listElement = 1; 
		 String linkString = "/html/body/div[2]/form/table//tr[1]/td/table//tr/td[2]/div/div[2]/div/table//tr[2]/td/div/div[1]/span[2]/table//tr[2]/td/ul/li[4]/ul/li[3]/ul/li[";
		  
		  while (true) { 
		  String listMov = document.selectXpath(linkString+listElement+"]").text(); 
		  String recebe = document.selectXpath(linkString+(listElement+1)+"]").text();
		 
		  movsList.add(listMov+"\n");
		  
		  if( recebe.isEmpty()) {
			break;
		   
		  }
		  
		  listElement++;
		  
		  }
		  return movsList; 
		  }
	
	
	
}
