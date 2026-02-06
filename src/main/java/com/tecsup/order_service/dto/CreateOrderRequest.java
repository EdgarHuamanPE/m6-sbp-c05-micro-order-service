package com.tecsup.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CreateOrderRequest {
    private Long userId;
    private Set<CreateOrderItemRequest> items;


}
