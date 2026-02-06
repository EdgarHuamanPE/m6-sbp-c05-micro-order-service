package com.tecsup.order_service.service;

import com.tecsup.order_service.client.Product;
import com.tecsup.order_service.client.ProductClient;
import com.tecsup.order_service.client.User;
import com.tecsup.order_service.client.UserClient;
import com.tecsup.order_service.dto.CreateOrderItemRequest;
import com.tecsup.order_service.dto.CreateOrderRequest;
import com.tecsup.order_service.dto.Order;
import com.tecsup.order_service.dto.OrderItem;
import com.tecsup.order_service.entity.OrderEntity;
import com.tecsup.order_service.entity.OrderItemEntity;
import com.tecsup.order_service.enums.OrderStatus;
import com.tecsup.order_service.mapper.OrderItemMapper;
import com.tecsup.order_service.mapper.OrderMapper;
import com.tecsup.order_service.repository.OrderRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackFor = Exception.class)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private  final UserClient userClient;
    private final ProductClient productClient;


    @Transactional(readOnly = true)
    public Order findById(Long id) {
        log.info("Finding order by ID: {}", id);
        OrderEntity entity = orderRepository.findById(id).orElse(null);

        User user = userClient.getUserById(entity.getUserId());

        Set<OrderItem> orderItems = entity.getItems().stream()
                .map(item -> {
                    Product product = productClient.getProductById(item.getProductId());
                    return orderItemMapper.toDomainWithProduct(item, product);
                })
                .collect(Collectors.toSet());

        Order order=orderMapper.toDomainWithUser(entity,user);
        order.setItems(orderItems);
        return order;
    }

    @Transactional(readOnly = true)
    public List<Order> findAll() {
        List<Order> orders=orderRepository.findAll()
                        .stream()
                        .map(orderEntity -> {
                                    User user = userClient.getUserById(orderEntity.getUserId());
                                    Order order=orderMapper.toDomainWithUser(orderEntity, user);
                                    Set<OrderItem> enrichedItems = orderEntity.getItems().stream()
                                    .map(item -> {
                                        Product product = productClient.getProductById(item.getProductId());
                                        return orderItemMapper.toDomainWithProduct(item, product);
                                    })
                                    .collect(Collectors.toSet());

                                    order.setItems(enrichedItems);

                                    return order;

                                })
                        .collect(Collectors.toList());

        return orders;
    }


    public Order register(@Valid CreateOrderRequest createOrderRequest) {
        final OrderEntity orderEntity = new OrderEntity();
        orderEntity.setUserId(createOrderRequest.getUserId());
        orderEntity.setOrderNumber(generateNextOrderNumber());
        orderEntity.setStatus(OrderStatus.PENDING);
        BigDecimal total=BigDecimal.ZERO;
        for(CreateOrderItemRequest item  : createOrderRequest.getItems()){
                final OrderItemEntity orderItemEntity = new OrderItemEntity();
                Product product = productClient.getProductById(item.getProductId());

                orderItemEntity.setProductId(item.getProductId());
                orderItemEntity.setQuantity(item.getQuantity());
                orderItemEntity.setUnitPrice(product.getPrice());

                orderItemEntity.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

                orderItemEntity.setOrderEntity(orderEntity);

                orderEntity.getItems().add(orderItemEntity);
                total=total.add(orderItemEntity.getSubtotal());
        }

        orderEntity.setTotalAmount(total);
        OrderEntity orderSaved = orderRepository.save(orderEntity);

        Set<OrderItem> orderItems=orderSaved.getItems().stream()
                .map(item -> {Product product = productClient.getProductById(item.getProductId());
                    return orderItemMapper.toDomainWithProduct(item, product);
                })
                .collect(Collectors.toSet());

        Order order = orderMapper.toDomain(orderSaved);
        User user = userClient.getUserById(createOrderRequest.getUserId());
        order.setUser(user);
        order.setItems(orderItems);
        return order;
    }

    private String generateNextOrderNumber() {
        String prefix = "ORD";
        int year = LocalDate.now().getYear();

        // find last OrderEntity
        Optional<OrderEntity> lastOrderEntity = orderRepository.findTopByOrderByIdDesc();

        int numberId = 1;
        if (lastOrderEntity.isPresent()) {
            String lastOrderNumber = lastOrderEntity.get().getOrderNumber();
            String[] parts = lastOrderNumber.split("-");
            if (parts.length == 3 && parts[1].equals(String.valueOf(year))) {
                numberId = Integer.parseInt(parts[2]) + 1;
            }
        }

        return String.format("%s-%d-%03d", prefix, year, numberId);
    }


    public void deleteOrder(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        orderRepository.delete(order);
    }


}
