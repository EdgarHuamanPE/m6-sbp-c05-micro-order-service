package com.tecsup.order_service.mapper;

import com.tecsup.order_service.client.Product;
import com.tecsup.order_service.client.User;
import com.tecsup.order_service.dto.Order;
import com.tecsup.order_service.dto.OrderItem;
import com.tecsup.order_service.entity.OrderEntity;
import com.tecsup.order_service.entity.OrderItemEntity;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    OrderItemMapper INSTANCE = Mappers.getMapper(OrderItemMapper.class);

    OrderItem toDomain(OrderItemEntity entity);
    OrderItemEntity toEntity(OrderItem domain);

    default OrderItem toDomainWithProduct(OrderItemEntity entity, Product product) {
        OrderItem orderItem = toDomain(entity);
        orderItem.setProduct(product);
        return orderItem;
    }
}
