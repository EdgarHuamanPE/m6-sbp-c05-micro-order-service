package com.tecsup.order_service.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.tecsup.order_service.client.User;
import com.tecsup.order_service.entity.OrderItemEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonPropertyOrder({"id", "orderNumber", "user","items","totalAmount","status","createdAt"})
@Builder
public class Order {

    private Long id;
    private String orderNumber;
    private Set<OrderItem> items;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;
    private User user;
    //private LocalDateTime updatedAt;


}
