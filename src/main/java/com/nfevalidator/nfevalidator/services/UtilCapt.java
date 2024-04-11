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


@Service
public class UtilCapt {
	
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
			String RespParserXml = document.selectXpath("/html/body/div[2]/form/table//tr[1]/td/table//tr/td[2]/div/div[2]/div/table//tr[2]/td/div/div[1]/span[2]/table//tr[2]/td/ul/li[1]").text();
			String tipMsmString = document.selectXpath("/html/body/div[2]/form/table//tr[1]/td/table//tr/td[2]/div/div[2]/div/table//tr[2]/td/div/div[1]/span[2]/table//tr[2]/td/ul/li[2]").text();
			String schValid = document.selectXpath("/html/body/div[2]/form/table//tr[1]/td/table//tr/td[2]/div/div[2]/div/table//tr[2]/td/div/div[1]/span[2]/table//tr[2]/td/ul/li[3]").text();											
			
			//VALIDAÇÃO NFE:
			String titValidNfe = document.selectXpath("/html/body/div[2]/form/table//tr[1]/td/table//tr/td[2]/div/div[2]/div/table//tr[2]/td/div/div[1]/span[2]/table//tr[2]/td/ul/li[4]/a/b").text();
			String certUser = document.selectXpath("/html/body/div[2]/form/table//tr[1]/td/table//tr/td[2]/div/div[2]/div/table//tr[2]/td/div/div[1]/span[2]/table//tr[2]/td/ul/li[4]/ul/li[1]").text();
			String validCert = document.selectXpath("/html/body/div[2]/form/table//tr[1]/td/table//tr/td[2]/div/div[2]/div/table//tr[2]/td/div/div[1]/span[2]/table//tr[2]/td/ul/li[4]/ul/li[2]/b").text();
			
			//REGRS DE NEG AMB PRODUÇÃO: 
			String regNegProd = document.selectXpath("/html/body/div[2]/form/table//tr[1]/td/table//tr/td[2]/div/div[2]/div/table//tr[2]/td/div/div[1]/span[2]/table//tr[2]/td/ul/li[4]/ul/li[3]/div").text();
			String probValida = document.selectXpath("/html/body/div[2]/form/table//tr[1]/td/table//tr/td[2]/div/div[2]/div/table//tr[2]/td/div/div[1]/span[2]/table//tr[2]/td/ul/li[4]/ul/li[3]/ul").text();	
			
			//RETIRA [Visualizar] DA VARIAVEL. 
			String NcertUser = null;
			if(certUser.contains("[Visualizar]"))
			NcertUser = certUser.replace("[Visualizar]", " ");
			
			//PEGAR ERROS DE VALIDAÇÃO
			String teString = document.selectXpath("/html/body/div[2]/form/table//tr[1]/td/table//tr/td[2]/div/div[2]/div/table//tr[2]/td/div/div[1]/span[2]/table//tr[2]/td/ul/li[4]/ul/li[3]/ul/li[1]").text();
			
			
			
			
			
			
			
			String montaResposta=(titResult+"\n"+RespParserXml+"\n"+tipMsmString+"\n"+schValid+"\n"+"\n"+titValidNfe+"\n"+NcertUser+"\n"+validCert+"\n"+"\n"+regNegProd+"\n");	
			return montaResposta+getMovs(document);	
			
		} catch (Exception e) {
			
			throw new BadRequestException("Não foi possível atender a solicitação no momento",e);
		}
		
		finally {
			driver.quit();
		}
		
	}
	
	 public List<String> getMovs(Document document) { 
		 //String movimenta = null;
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
