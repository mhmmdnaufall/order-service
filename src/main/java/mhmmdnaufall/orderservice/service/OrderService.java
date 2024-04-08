package mhmmdnaufall.orderservice.service;

import mhmmdnaufall.orderservice.dto.OrderRequest;

public interface OrderService {

    String placeOrder(OrderRequest request);

}
