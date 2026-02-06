package com.tecsup.order_service.service;

import com.tecsup.order_service.client.Product;
import com.tecsup.order_service.client.ProductClient;
import com.tecsup.order_service.client.User;
import com.tecsup.order_service.client.UserClient;
import com.tecsup.order_service.dto.CreateOrderItemRequest;
import com.tecsup.order_service.dto.CreateOrderRequest;
import com.tecsup.order_service.dto.Order;
import com.tecsup.order_service.entity.OrderEntity;
import com.tecsup.order_service.entity.OrderItemEntity;
import com.tecsup.order_service.mapper.OrderMapper;
import com.tecsup.order_service.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class OrderServiceTest {

    @MockitoBean
    private OrderRepository orderRepository;

    @MockitoBean
    private UserClient userClient;

    @MockitoBean
    private ProductClient productClient;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderMapper orderMapper;

    @Test
    void findById() {

        Long ID = 1L;

        OrderEntity entity = OrderEntity.builder()
                .id(ID)
                .userId(2L)
                .items(Set.of(
                         OrderItemEntity.builder().productId(1L).quantity(2).build(),
                        OrderItemEntity.builder().productId(3L).quantity(1).build()
                ))
                .build();

        User userMock = User.builder()
                .id(2L)
                .name("Carlos")
                .build();

        Product product1 = Product.builder()
                .id(1L)
                .name("Laptop")
                .price(BigDecimal.valueOf(1000))
                .build();

        Product product3 = Product.builder()
                .id(3L)
                .name("Mouse")
                .price(BigDecimal.valueOf(50))
                .build();

        when(orderRepository.findById(ID))
                .thenReturn(Optional.of(entity));

        when(userClient.getUserById(2L))
                .thenReturn(userMock);

        when(productClient.getProductById(1L)).thenReturn(product1);
        when(productClient.getProductById(3L)).thenReturn(product3);

        Order result = orderService.findById(ID);

        assertNotNull(result);
        assertEquals(ID, result.getId());
        assertEquals(2L, result.getUser().getId());
        assertEquals(2, result.getItems().size());

        verify(orderRepository).findById(ID);
        verify(userClient).getUserById(2L);
        verify(productClient).getProductById(1L);
        verify(productClient).getProductById(3L);
    }

    @Test
    void findAll() {
        OrderEntity order1 = OrderEntity.builder()
                .id(1L)
                .userId(10L)
                .items(Set.of(
                        OrderItemEntity.builder().productId(100L).quantity(2).build()
                ))
                .build();

        OrderEntity order2 = OrderEntity.builder()
                .id(2L)
                .userId(20L)
                .items(Set.of(
                        OrderItemEntity.builder().productId(200L).quantity(1).build()
                ))
                .build();

        when(orderRepository.findAll())
                .thenReturn(List.of(order1, order2));

        when(userClient.getUserById(10L))
                .thenReturn(User.builder().id(10L).name("Juan").build());

        when(userClient.getUserById(20L))
                .thenReturn(User.builder().id(20L).name("Maria").build());

        when(productClient.getProductById(100L))
                .thenReturn(Product.builder().id(100L).name("Laptop").price(BigDecimal.TEN).build());

        when(productClient.getProductById(200L))
                .thenReturn(Product.builder().id(200L).name("Mouse").price(BigDecimal.ONE).build());

        List<Order> result = orderService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(10L, result.get(0).getUser().getId());
        assertEquals(20L, result.get(1).getUser().getId());

        verify(orderRepository).findAll();
        verify(userClient, times(2)).getUserById(anyLong());
        verify(productClient, times(2)).getProductById(anyLong());
    }

    @Test
    void register() {

        CreateOrderRequest request = CreateOrderRequest.builder()
                .userId(5L)
                .items(Set.of(   // ✅ ES SET
                        new CreateOrderItemRequest(100L, 2),
                        new CreateOrderItemRequest(200L, 1)
                ))
                .build();

        OrderEntity last = OrderEntity.builder()
                .orderNumber("ORD-2026-001")
                .build();

        when(orderRepository.findTopByOrderByIdDesc())
                .thenReturn(Optional.of(last));

        Product p1 = Product.builder()
                .id(100L)
                .name("Laptop")
                .price(BigDecimal.valueOf(10))
                .build();
        Product p2 = Product.builder()
                .id(200L)
                .name("Mouse")
                .price(BigDecimal.valueOf(5))
                .build();

        when(productClient.getProductById(100L)).thenReturn(p1);
        when(productClient.getProductById(200L)).thenReturn(p2);

        User user = User.builder()
                .id(5L)
                .name("Carlos")
                .build();

        when(userClient.getUserById(5L)).thenReturn(user);

        when(orderRepository.save(any(OrderEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.register(request);

        assertNotNull(result);
        assertEquals(5L, result.getUser().getId());
        assertEquals(2, result.getItems().size());

        assertEquals(BigDecimal.valueOf(25), result.getTotalAmount());

        verify(orderRepository).findTopByOrderByIdDesc();
        verify(orderRepository).save(any());
        verify(userClient).getUserById(5L);

        verify(productClient, times(4)).getProductById(anyLong());
    }

    @Test
    void deleteOrder() {
        Long ID = 10L;
        OrderEntity entity = OrderEntity.builder()
                .id(ID)
                .build();

        when(orderRepository.findById(ID))
                .thenReturn(Optional.of(entity));

        orderService.deleteOrder(ID);

        verify(orderRepository, times(1)).findById(ID);
        verify(orderRepository, times(1)).delete(entity);
    }

}