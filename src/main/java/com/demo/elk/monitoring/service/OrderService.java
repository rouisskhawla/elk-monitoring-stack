package com.demo.elk.monitoring.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.demo.elk.monitoring.model.Order;

@Service
public class OrderService {

	private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

	private final Map<String, Order> db = new HashMap<>();

	public Order create(Order order) {
		try {
			order.setId(UUID.randomUUID().toString());
			db.put(order.getId(), order);
			logger.info("Order created: {}", order);
			return order;
		} catch (Exception e) {
			logger.error("Error creating Order", e);
			throw new RuntimeException("Unable to create order");
		}

	}

	public Order get(String id) {
		try {
			if (!db.containsKey(id)) {
				logger.warn("Order not found: {}", id);
				throw new NoSuchElementException("Order not found");
			}
			logger.info("Order retrieved: {}", id);
			return db.get(id);
		} catch (Exception e) {
			logger.error("Failed to retrieve order {}", id, e);
			throw e;
		}
	}

	public List<Order> list() {
		try {
			logger.info("Listing orders");
			return new ArrayList<>(db.values());
		} catch (Exception e) {
			logger.error("Failed to list orders", e);
			throw new RuntimeException("Unable to list orders");
		}
	}

	public void delete(String id) {
		try {
			if (!db.containsKey(id)) {
				logger.warn("Attempt to delete non-existent order: {}", id);
				throw new NoSuchElementException("Order not found");
			}
			db.remove(id);
			logger.warn("Order deleted: {}", id);
		} catch (Exception e) {
			logger.error("Failed to delete order {}", id, e);
			throw e;
		}
	}
}
