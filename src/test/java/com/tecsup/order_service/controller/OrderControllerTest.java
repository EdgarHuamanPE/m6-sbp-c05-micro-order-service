package com.tecsup.order_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.order_service.client.Product;
import com.tecsup.order_service.client.User;
import com.tecsup.order_service.dto.CreateOrderItemRequest;
import com.tecsup.order_service.dto.CreateOrderRequest;
import com.tecsup.order_service.dto.Order;
import com.tecsup.order_service.dto.OrderItem;
import com.tecsup.order_service.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.Set;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getOrderById() throws Exception {

        Long ID = 1L;

        User user = User.builder()
                .id(2L)
                .name("Carlos")
                .email("carlos@test.com")
                .build();

        Product product1 = Product.builder()
                .id(100L)
                .name("Laptop")
                .price(BigDecimal.valueOf(10))
                .build();

        Product product2 = Product.builder()
                .id(200L)
                .name("Mouse")
                .price(BigDecimal.valueOf(5))
                .build();

        OrderItem item1 = OrderItem.builder()
                .product(product1)
                .quantity(2)
                .build();

        OrderItem item2 = OrderItem.builder()
                .product(product2)
                .quantity(1)
                .build();

        Order order = Order.builder()
                .id(ID)
                .user(user)
                .items(Set.of(item1, item2))
                .totalAmount(BigDecimal.valueOf(25))
                .build();

        when(orderService.findById(ID)).thenReturn(order);

        mockMvc.perform(get("/api/orders/{id}", ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.user.id").value(2))
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.items[*].product.id",
                        containsInAnyOrder(100, 200)));


        verify(orderService).findById(ID);
    }

    @Test
    void getAllOrders() throws Exception {

        User user1 = User.builder().id(1L).name("Carlos").build();
        User user2 = User.builder().id(2L).name("Ana").build();

        Product product = Product.builder()
                .id(100L)
                .name("Laptop")
                .price(BigDecimal.TEN)
                .build();

        OrderItem item = OrderItem.builder()
                .product(product)
                .quantity(1)
                .build();

        Order order1 = Order.builder()
                .id(10L)
                .user(user1)
                .items(Set.of(item))
                .totalAmount(BigDecimal.TEN)
                .build();

        Order order2 = Order.builder()
                .id(20L)
                .user(user2)
                .items(Set.of(item))
                .totalAmount(BigDecimal.TEN)
                .build();

        when(orderService.findAll())
                .thenReturn(List.of(order1, order2));

        // ===== HTTP =====
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[1].id").value(20));

        verify(orderService).findAll();
    }


    @Test
    void register() throws Exception {

        CreateOrderRequest request = CreateOrderRequest.builder()
                .userId(5L)
                .items(Set.of(
                        new CreateOrderItemRequest(100L, 2),
                        new CreateOrderItemRequest(200L, 1)
                ))
                .build();

        User user = User.builder()
                .id(5L)
                .name("Carlos")
                .build();

        Product product = Product.builder()
                .id(100L)
                .name("Laptop")
                .price(BigDecimal.TEN)
                .build();

        OrderItem item = OrderItem.builder()
                .product(product)
                .quantity(2)
                .build();

        Order orderResponse = Order.builder()
                .id(1L)
                .user(user)
                .items(Set.of(item))
                .totalAmount(BigDecimal.valueOf(20))
                .build();

        when(orderService.register(any(CreateOrderRequest.class)))
                .thenReturn(orderResponse);

        mockMvc.perform(post("/api/orders")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.user.id").value(5))
                .andExpect(jsonPath("$.totalAmount").value(20));

        verify(orderService).register(any(CreateOrderRequest.class));
    }


    @Test
    void deleteOrder_flow() throws Exception {

        Long ID = 1L;

        doNothing().when(orderService).deleteOrder(ID);

        mockMvc.perform(delete("/api/orders/{id}", ID))
                .andExpect(status().isOk())
                .andExpect(content().string("Order Deleted with ID: 1"));

        verify(orderService).deleteOrder(ID);

        mockMvc.perform(delete("/api/orders/{id}", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("bad ID "));

        verify(orderService, times(1)).deleteOrder(anyLong()); // no se vuelve a llamar

        doThrow(new RuntimeException())
                .when(orderService).deleteOrder(ID);

        mockMvc.perform(delete("/api/orders/{id}", ID))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error Delete the Order"));

        verify(orderService, times(2)).deleteOrder(ID);
    }

}