package mhmmdnaufall.orderservice.service.impl;

import lombok.RequiredArgsConstructor;
import mhmmdnaufall.orderservice.dto.OrderLineItemDto;
import mhmmdnaufall.orderservice.dto.OrderRequest;
import mhmmdnaufall.orderservice.entity.Order;
import mhmmdnaufall.orderservice.entity.OrderLineItem;
import mhmmdnaufall.orderservice.repository.OrderRepository;
import mhmmdnaufall.orderservice.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    @Override
    public void placeOrder(OrderRequest request) {
        final var order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        final var orderLineItemList = request.orderLineItemDtoList()
                .stream()
                .map(mapToDto)
                .toList();
        orderLineItemList.forEach(orderLineItem -> orderLineItem.setOrder(order));

        order.setOrderLineItemList(orderLineItemList);
        orderRepository.save(order);
    }

    private final Function<OrderLineItemDto, OrderLineItem> mapToDto = dto ->
            new OrderLineItem(dto.id(), dto.skuCode(), dto.price(), dto.quantity(), dto.order());

}
