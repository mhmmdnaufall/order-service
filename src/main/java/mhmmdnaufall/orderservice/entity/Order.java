package mhmmdnaufall.orderservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity @Table(name = "orders")
@Data @NoArgsConstructor @AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String orderNumber;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderLineItem> orderLineItemList;

}
