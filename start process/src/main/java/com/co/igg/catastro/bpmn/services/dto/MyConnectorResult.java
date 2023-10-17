package com.co.igg.catastro.bpmn.services.dto;

public class MyConnectorResult {
	
	private String result;
	
	public MyConnectorResult() {
		// TODO Auto-generated constructor stub
	}
	public MyConnectorResult(String result) {
		super();
		this.result = result;
	}
	
	@Override
	public String toString() {
		return "MyConnectorResult [result=" + result + "]";
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
}
