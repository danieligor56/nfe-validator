package com.nfevalidator.nfevalidator.entity;


import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CorpNFE {
	
	private String titResult;
	private String RespParserXml;
	private String tipMsmString;
	private String schValid;
	private String titValidNfe;
	private String certUser;
	private String validCert;
	private String regNegProd;
	private String probValida;
	private List<String> listErrs;
	
	public String retiraVisual(String texto) {
		if(certUser.contains("[Visualizar]"))
		certUser = certUser.replace("[Visualizar]", " ");
		return certUser;
	}
	
	@Override
	public String toString() {
		return titResult+
				"\n"+RespParserXml+
				"\n"+tipMsmString+
				"\n"+schValid+
				"\n"+"\n"
				+titValidNfe+
				"\n"+retiraVisual(certUser)+
				"\n"+validCert+
				"\n"+"\n"+
				regNegProd+"\n"+
				listErrs;
	}
	
}
