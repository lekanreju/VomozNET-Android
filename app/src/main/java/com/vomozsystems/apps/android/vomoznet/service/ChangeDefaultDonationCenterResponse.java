package com.vomozsystems.apps.android.vomoznet.service;

import com.vomozsystems.apps.android.vomoznet.entity.DonationCenter;

import java.util.List;

public class ChangeDefaultDonationCenterResponse extends BaseServiceResponse {

	private List<DonationCenter> responseData;

	public List<DonationCenter> getResponseData() {
		return responseData;
	}

	public void setResponseData(List<DonationCenter> responseData) {
		this.responseData = responseData;
	}
}
