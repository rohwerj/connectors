package io.camunda.connector;

import io.camunda.connector.api.annotation.Secret;

public class Base64Request {

  private String inputs;
  private String model_id;
  
   public String getInputs() {
    return inputs;
  }

  public void setInputs(String inputs) {
    this.inputs = inputs;
  }

  public String getModel_id() {
    return model_id;
  }

  public void setModel_id(String model_id) {
    this.model_id = model_id;
  }

  @Override
  public String toString() {
    return "Base64Request [inputs=" + inputs + ", model_id=" + model_id + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((inputs == null) ? 0 : inputs.hashCode());
    result = prime * result + ((model_id == null) ? 0 : model_id.hashCode());
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
    Base64Request other = (Base64Request) obj;
    if (inputs == null) {
      if (other.inputs != null)
        return false;
    } else if (!inputs.equals(other.inputs))
      return false;
    if (model_id == null) {
      if (other.model_id != null)
        return false;
    } else if (!model_id.equals(other.model_id))
      return false;
    return true;
  }

}
