package mhmmdnaufall.orderservice.service.impl;

import lombok.RequiredArgsConstructor;
import mhmmdnaufall.orderservice.dto.InventoryResponse;
import mhmmdnaufall.orderservice.dto.OrderLineItemDto;
import mhmmdnaufall.orderservice.dto.OrderRequest;
import mhmmdnaufall.orderservice.entity.Order;
import mhmmdnaufall.orderservice.entity.OrderLineItem;
import mhmmdnaufall.orderservice.repository.OrderRepository;
import mhmmdnaufall.orderservice.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final RestClient restClient;

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

        final var skuCodes = order.getOrderLineItemList().stream()
                .map(OrderLineItem::getSkuCode)
                .toList();

        // Call Inventory Service, and place order if product is in stock
        final var inventoryResponseArray = restClient.get()
                .uri(
                        "http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build()
                )
                .retrieve()
                .body(InventoryResponse[].class);

        final var allProductsInStock = Arrays.stream(Objects.requireNonNull(inventoryResponseArray))
                .allMatch(InventoryResponse::isInStock);

        if (allProductsInStock) orderRepository.save(order);
        else throw new IllegalArgumentException("Product is not in stock, please try again later");

    }

    private final Function<OrderLineItemDto, OrderLineItem> mapToDto = dto ->
            new OrderLineItem(dto.id(), dto.skuCode(), dto.price(), dto.quantity(), dto.order());

}
