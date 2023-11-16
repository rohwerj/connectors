package io.camunda.connector;

import java.util.Arrays;

public class Base64Request {

  private String[] filesNames;
  private byte[][] files;
  private Long idProceso;

  public Long getIdProceso() {
    return idProceso;
  }

  public void setIdProceso(Long idProceso) {
    this.idProceso = idProceso;
  }

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

  @Override
  public String toString() {
    return "Base64Request [filesNames="
        + Arrays.toString(filesNames)
        + ", files="
        + Arrays.toString(files)
        + ", idProceso="
        + idProceso
        + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(filesNames);
    result = prime * result + Arrays.deepHashCode(files);
    result = prime * result + ((idProceso == null) ? 0 : idProceso.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Base64Request other = (Base64Request) obj;
    if (!Arrays.equals(filesNames, other.filesNames)) return false;
    if (!Arrays.deepEquals(files, other.files)) return false;
    if (idProceso == null) {
      if (other.idProceso != null) return false;
    } else if (!idProceso.equals(other.idProceso)) return false;
    return true;
  }
}
