package com.nfevalidator.nfevalidator.services;

import java.util.LinkedList;
import java.util.Queue;

import org.springframework.stereotype.Service;

import com.nfevalidator.nfevalidator.entity.ValidBody;

@Service
public class FilaReq {
	
	private Queue<String> fila = new LinkedList<>();
	
	public void filaRequisicao(String xml) {
		fila.add(xml);
	}
	
	public String proximaRequisicao() {
        return fila.poll();
    }

}
