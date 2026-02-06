package com.tecsup.order_service.repository;

import com.tecsup.order_service.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long > {
}
