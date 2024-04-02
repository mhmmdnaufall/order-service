package mhmmdnaufall.orderservice.service;

import mhmmdnaufall.orderservice.dto.OrderRequest;

public interface OrderService {

    void placeOrder(OrderRequest request);

}
