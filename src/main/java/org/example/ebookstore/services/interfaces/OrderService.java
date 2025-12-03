package org.example.ebookstore.services.interfaces;

import org.example.ebookstore.entities.Order;
import org.example.ebookstore.entities.dtos.OrderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;

public interface OrderService {
    Order createOrder(Long bookId, Model model);
    Page<OrderDto> findByUserId(Long userId, Pageable pageable);
}
