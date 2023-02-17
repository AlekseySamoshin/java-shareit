package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    List<Item> getItemsByUserId(Long userId);

    List<Item> getItems();

    Item addItem(Long userId, Item mapDto);

    List<Item> searchItemsByText(Long userId, String text);
}
