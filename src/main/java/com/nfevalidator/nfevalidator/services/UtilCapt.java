package com.nfevalidator.nfevalidator.services;

import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.BadRequestException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;
import com.nfevalidator.nfevalidator.entity.CorpNFE;

import lombok.experimental.var;



@Service
public class UtilCapt {
	
	CorpNFE corp = new CorpNFE();
	
	public CorpNFE WebDriverManager(String xml) throws Exception  {
		
		System.setProperty("webdriver.chrome.driver","src/main/resources/chromedriver.exe");
		
		ChromeOptions options = new ChromeOptions();
		options.addArguments( "headless","--ignore-certificate-errors");
		WebDriver driver = new ChromeDriver(options);
		
		try {
			
			driver.get("https://www.sefaz.rs.gov.br/nfe/nfe-val.aspx");
			
			WebElement insertXML = driver.findElement(By.id("txtxml"));
			
			((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1]", insertXML, xml);
		
			WebElement validButton = driver.findElement(By.xpath("//*[@id=\"btnvalidar\"]"));
			validButton.click();
			
			String pageSource = driver.getPageSource();
			Document document = Jsoup.parse(pageSource);
			
			//REGRA DE NEGOCIOS:
			
			String titResult = document.selectXpath("//*[@id=\"resultado\"]/table//tr[1]/td").text();
			corp.setTitResult(titResult);
			
			//PARSE XML
			String respParserXml = document.selectXpath("//*[@id=\"resultado\"]/table//tr[2]/td/ul/li[1]").text();
			String editParseXml = respParserXml.substring(respParserXml.indexOf(": ") + 2);
			
			//TIPO DE MENSAGEM
			String tipMsmString = document.selectXpath("/html/body/div[2]/form/table//tr[1]/td/table//tr/td[2]/div/div[2]/div/table//tr[2]/td/div/div[1]/span[2]/table//tr[2]/td/ul/li[2]").text();
			String editTipMsm = tipMsmString.substring(tipMsmString.indexOf(": ") + 2);
			
			//SCHEMA XML
			String schValid = document.selectXpath("/html/body/div[2]/form/table//tr[1]/td/table//tr/td[2]/div/div[2]/div/table//tr[2]/td/div/div[1]/span[2]/table//tr[2]/td/ul/li[3]").text();											
			String ediSchValid = schValid.substring(schValid.indexOf(": ")+ 2);
			
			//VALIDAÇÃO NFE:
			String titValidNfe = document.selectXpath("/html/body/div[2]/form/table//tr[1]/td/table//tr/td[2]/div/div[2]/div/table//tr[2]/td/div/div[1]/span[2]/table//tr[2]/td/ul/li[4]/a/b").text();
			
			/*
			 * String certUser = document.selectXpath(
			 * "/html/body/div[2]/form/table//tr[1]/td/table//tr/td[2]/div/div[2]/div/table//tr[2]/td/div/div[1]/span[2]/table//tr[2]/td/ul/li[4]/ul/li[1]"
			 * ).text(); corp.setCertUser(certUser);
			 */
			//VALIDADE DA CHAVE DIGITAL
			String validCert = document.selectXpath("/html/body/div[2]/form/table//tr[1]/td/table//tr/td[2]/div/div[2]/div/table//tr[2]/td/div/div[1]/span[2]/table//tr[2]/td/ul/li[4]/ul/li[2]/b").text();
			String editValidCert = validCert.substring(validCert.indexOf(": ")+ 2);
			
			//REGRS DE NEG AMB PRODUÇÃO: 
			String regNegProd = document.selectXpath("/html/body/div[2]/form/table//tr[1]/td/table//tr/td[2]/div/div[2]/div/table//tr[2]/td/div/div[1]/span[2]/table//tr[2]/td/ul/li[4]/ul/li[3]/div").text();
			String editRegNegProd = regNegProd.substring(regNegProd.indexOf("] ")+ 2);
			
			if(editRegNegProd.contains("Produção")) {
				String regNegocios = editRegNegProd + " (Ambiente de produção)";
			}
				String regNegocios = editRegNegProd + " (Ambiente de homologação)";
			
			/*
			 * String probValida = document.selectXpath(
			 * "/html/body/div[2]/form/table//tr[1]/td/table//tr/td[2]/div/div[2]/div/table//tr[2]/td/div/div[1]/span[2]/table//tr[2]/td/ul/li[4]/ul/li[3]/ul"
			 * ).text(); corp.setProbValida(probValida);
			 */
			
			
			List<String> errList = getMovs(document) ;
			corp.setListErrs(errList);
			
			CorpNFE nfe = new CorpNFE(titResult,editParseXml,editTipMsm,ediSchValid,titValidNfe,editValidCert,regNegocios,errList);
			
			return nfe;
			
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
