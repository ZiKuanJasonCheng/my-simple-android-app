package com.jc.webservice;

import java.util.List;

public interface SearchSeggestionHandler {
    void onGetSearchSeggestionSuccess(int requestId, List<String> suggestionList);
    void onGetSearchSeggestionFailure(int requestId);
}
