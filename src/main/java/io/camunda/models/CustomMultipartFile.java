package io.camunda.models;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

public class CustomMultipartFile implements MultipartFile {

	private byte[] bytes;
	private String name;
    private String originalFilename;
    private String contentType="image/jpeg";
    private boolean isEmpty=false;
    private long size=269592L;

    public CustomMultipartFile(byte[] bytes, String name, String originalFilename) {
        this.bytes = bytes;
        this.name = name;
        this.originalFilename = originalFilename;
    }

    @Override
	public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
    @Override
    public String getName() {
        return name;
    }
    @Override
    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }
    @Override
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    @Override
    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean isEmpty) {
        this.isEmpty = isEmpty;
    }
    @Override
    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
    
    public void setName(String name) {
		this.name = name;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(bytes);
	}

	@Override
	public void transferTo(File dest) throws IOException, IllegalStateException {
		try(FileOutputStream fos = new FileOutputStream(dest)) {
            fos.write(bytes);
        }
	}
}
