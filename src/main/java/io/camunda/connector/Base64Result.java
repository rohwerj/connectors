package io.camunda.connector;

import java.util.Objects;

public class Base64Result {
  private String result;
	
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.result == null) ? 0 : this.result.hashCode());
    return result;
  }
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Base64Result other = (Base64Result) obj;
    if (result == null) {
      if (other.result != null)
        return false;
    } else if (!result.equals(other.result))
      return false;
    return true;
  }
 	@Override
  public String toString() {
    return "Base64Result [result=" + result + "]";
  }
}
