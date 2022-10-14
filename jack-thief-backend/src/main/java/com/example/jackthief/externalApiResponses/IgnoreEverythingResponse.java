package com.example.jackthief.externalApiResponses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IgnoreEverythingResponse extends DeckOfCardApiResponse {
    //nothing in this class
    // mainly created for the json ignore attribute
}
