package io.camunda.connector;

import io.camunda.connector.api.annotation.Secret;

public class Base64Request {

  private String inputs;
  private String model_id;
  private String units;
  @Secret
  private String apiKey;

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

  public String getUnits() { return units; }

  public void setUnits(String units) { this.units = units; }

  public String getApiKey() { return apiKey; }

  public void setApiKey(String apiKey) { this.apiKey = apiKey; }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((inputs == null) ? 0 : inputs.hashCode());
    result = prime * result + ((model_id == null) ? 0 : model_id.hashCode());
    result = prime * result + ((units == null) ? 0 : units.hashCode());
    result = prime * result + ((apiKey == null) ? 0 : apiKey.hashCode());
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
    if (units == null) {
      if (other.units != null)
        return false;
    } else if (!units.equals(other.units))
      return false;
    if (apiKey == null) {
      if (other.apiKey != null)
        return false;
    } else if (!apiKey.equals(other.apiKey))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Base64Request [inputs=" + inputs + ", model_id=" + model_id + ", units=" + units + ", apiKey=" + apiKey
        + "]";
  }
}
