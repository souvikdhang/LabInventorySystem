package com.lis.others;

import com.lis.model.EquipmentDetails;
import com.lis.model.Requests;

public class EquipmentAndRequest {

  private Requests request;
  private EquipmentDetails equipmentDetails;
public Requests getRequest() {
	return request;
}
public void setRequest(Requests request) {
	this.request = request;
}
public EquipmentDetails getEquipmentDetails() {
	return equipmentDetails;
}
public void setEquipmentDetails(EquipmentDetails equipmentDetails) {
	this.equipmentDetails = equipmentDetails;
}
@Override
public String toString() {
	return "EquipmentAndRequest [request=" + request + ", equipmentDetails=" + equipmentDetails + "]";
}
  


}