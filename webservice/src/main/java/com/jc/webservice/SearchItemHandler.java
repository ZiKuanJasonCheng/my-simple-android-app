package com.jc.webservice;

import java.util.List;

public interface SearchItemHandler {
    void onSearchItemSuccess(int requestId, List<ProductItem> listProductItem);
    void onSearchItemFailure(int requestId);
}
