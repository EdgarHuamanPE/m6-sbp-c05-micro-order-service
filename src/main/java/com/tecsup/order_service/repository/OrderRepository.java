package com.tecsup.order_service.repository;

import com.tecsup.order_service.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    //boolean existsBySalesSerial(String orderNumber);
    Optional<OrderEntity> findTopByOrderByIdDesc();

}
