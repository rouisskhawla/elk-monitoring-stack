package com.demo.elk.monitoring.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.elk.monitoring.model.Order;
import com.demo.elk.monitoring.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
	
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService service;

    @PostMapping
    public Order create(@RequestBody Order order) {
    	logger.info("API: create order");
        return service.create(order);
    }

    @GetMapping("/{id}")
    public Order get(@PathVariable String id) {
    	logger.info("API: get {}", id);
        return service.get(id);
    }

    @GetMapping
    public List<Order> list() {
    	logger.info("API: list orders");
        return service.list();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
    	logger.info("API: delete {}", id);
        service.delete(id);
    }
}