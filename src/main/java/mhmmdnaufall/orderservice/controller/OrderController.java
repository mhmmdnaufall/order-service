package mhmmdnaufall.orderservice.controller;

import lombok.RequiredArgsConstructor;
import mhmmdnaufall.orderservice.dto.OrderRequest;
import mhmmdnaufall.orderservice.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String placeOrder(@RequestBody OrderRequest request) {
        orderService.placeOrder(request);
        return "Order Placed Successfully";
    }

}
