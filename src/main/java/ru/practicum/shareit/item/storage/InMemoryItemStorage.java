package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Qualifier("InMemoryItemStorage")
@Slf4j
public class InMemoryItemStorage implements ItemStorage{
    private Long generatedId = 1L;
    private final HashMap<Long, List<Item>> items;
    private final UserStorage userStorage;

    @Autowired
    public InMemoryItemStorage(HashMap<Long, List<Item>> items, UserStorage userStorage) {
        this.items = items;
        this.userStorage = userStorage;
    }

    @Override
    public List<Item> getItemsByUserId(Long userId) {
        synchronizeUsersAndItemsLists();
        return items.get(userId);
    }

    @Override
    public List<Item> getItems() {
        synchronizeUsersAndItemsLists();
        List<Item> allItems = new ArrayList<>();
        for (List<Item> items : items.values()) {
            allItems.addAll(items);
        }
        return allItems;
    }

    @Override
    public Item addItem(Long userId, Item item) {
        synchronizeUsersAndItemsLists();
        if (!items.containsKey(userId)) throw new NotFoundException("Пользователь id=" + userId + " не найден.");
        if (item.getId() == null) item.setId(generateId());
        addItemToList(userId, item);
        log.info(String.format("Добавлена информация о вещи id=%d (%s) пользователя id=%s", item.getId(), item.getName(), userId));
        return item;
    }

    @Override
    public List<Item> searchItemsByText(Long userId, String text) {
        if (text.isBlank()) return new ArrayList<>();
        List<Item> foundItems = new ArrayList<>();
        if (items.get(userId) == null) throw new NotFoundException("У пользователя id=" + userId + "не найдены вещи");
        for (Item item : getItems()) {
            if (
                (item.getName().toLowerCase().contains(text.toLowerCase())
                || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                && item.isAvailable()) {
                foundItems.add(item);
            }
        }
        if (foundItems.isEmpty()) throw new NotFoundException("Вещи, содержащие \"" + "\" не найдены");
        return foundItems;
    }

    private void addItemToList(Long userId, Item newItem) {
        int indexItemSameId = -1;
        for (int i = 0; i < items.get(userId).size(); i++) {
            if (items.get(userId).get(i).getId().equals(newItem.getId())) {
                indexItemSameId = i;
            }
        }
        if (indexItemSameId >= 0) {
            items.get(userId).remove(indexItemSameId);
        }
        items.get(userId).add(newItem);
    }

    private void synchronizeUsersAndItemsLists() {
        for (User user : userStorage.getUsers()) {
            if (!items.containsKey(user.getId())) {
                items.put(user.getId(), new ArrayList<>());
            }
        }
    }

    private long generateId() {
        return generatedId++;
    }
}
