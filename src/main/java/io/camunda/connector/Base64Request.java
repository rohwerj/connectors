package io.camunda.connector;

import io.camunda.connector.api.annotation.Secret;

public class Base64Request {

	private String[] filesNames;
	private byte[][] files;
  
  	public String[] getFilesNames() {
  		return filesNames;
	}
	public void setFilesNames(String[] filesNames) {
		this.filesNames = filesNames;
	}
	public byte[][] getFiles() {
		return files;
	}
	public void setFiles(byte[][] files) {
		this.files = files;
	}

}
