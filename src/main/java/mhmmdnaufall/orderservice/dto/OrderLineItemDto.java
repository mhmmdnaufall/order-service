package mhmmdnaufall.orderservice.dto;

import mhmmdnaufall.orderservice.entity.Order;

import java.math.BigDecimal;

public record OrderLineItemDto(Long id, String skuCode, BigDecimal price, Integer quantity, Order order) { }
