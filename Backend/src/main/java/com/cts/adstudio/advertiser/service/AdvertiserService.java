package com.cts.adstudio.advertiser.service;

import java.util.List;

import com.cts.adstudio.advertiser.dto.request.AdvertiserRequest;
import com.cts.adstudio.advertiser.dto.response.AdvertiserResponse;

public interface AdvertiserService {

    // Create a new advertiser
    // Takes a request and returns a response
    AdvertiserResponse createAdvertiser(AdvertiserRequest request);

    // Get all advertisers
    // returns a list of responses
    List<AdvertiserResponse> getAllAdvertiser();

    // Get one advertiser by ID
    // takes just an ID and returns one response
    AdvertiserResponse getAdvertiserById(Integer id);

    // Update an advertiser
    // takes both and ID and a request
    AdvertiserResponse updateAdvertiser(Integer id, AdvertiserRequest request);

    // Delete an advertiser
    // nothing to return after deletion
    void deleteAdvertiser(Integer id);

}