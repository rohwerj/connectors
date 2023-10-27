package io.camunda.models;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

public class CustomMultipartFile implements MultipartFile {

	private byte[] bytes;
    String name;
    String originalFilename;
    String contentType;
    boolean isEmpty=false;
    long size;

	public CustomMultipartFile(byte[] bytes, String name, String originalFilename, String contentType, boolean isEmpty,
            long size) {
        this.bytes = bytes;
        this.name = name;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.isEmpty = isEmpty;
        this.size = size;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setEmpty(boolean isEmpty) {
        this.isEmpty = isEmpty;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}
	
	@Override
	public String getOriginalFilename() {
		// TODO Auto-generated method stub
		return name;
	}

    @Override
    public String getContentType() {
        return contentType;
    }

	@Override
	public boolean isEmpty() {
		return bytes == null || bytes.length == 0;
	}

	@Override
	public long getSize() {
		return bytes.length;
	}

	@Override
	public byte[] getBytes() throws IOException {
		return bytes;
	}

	@Override
	public void transferTo(File dest) throws IOException, IllegalStateException {
		try(FileOutputStream fos = new FileOutputStream(dest)) {
            fos.write(bytes);
        }
	}
}
