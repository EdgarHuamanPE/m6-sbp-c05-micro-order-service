package com.tecsup.order_service.mapper;


import com.tecsup.order_service.client.User;
import com.tecsup.order_service.dto.Order;
import com.tecsup.order_service.entity.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    Order toDomain(OrderEntity entity);
    OrderEntity toEntity(Order domain);

    default Order toDomainWithUser(OrderEntity entity, User user) {
        Order order = toDomain(entity);
        order.setUser(user);
        return order;
    }


}
