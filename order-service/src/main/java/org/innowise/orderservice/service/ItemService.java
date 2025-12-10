package org.innowise.orderservice.service;

import lombok.RequiredArgsConstructor;
import org.innowise.orderservice.model.entity.Item;
import org.innowise.orderservice.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    public List<Item> getAll() {
        return itemRepository.findAll();
    }
}
