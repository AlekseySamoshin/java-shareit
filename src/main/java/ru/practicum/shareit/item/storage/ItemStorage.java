package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Component
public interface ItemStorage {
    public List<Item> getItemsByUserId(Long userId);
    public List<Item> getItems();

    Item addItem(Long userId, Item mapDto);

    List<Item> searchItemsByText(Long userId, String text);
}
