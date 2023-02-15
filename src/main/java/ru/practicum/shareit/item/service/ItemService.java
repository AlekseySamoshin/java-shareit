package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongDataException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemService {

    ItemStorage itemStorage;
    ItemDtoMapper itemDtoMapper;

    @Autowired
    public ItemService (@Qualifier("InMemoryItemStorage") ItemStorage itemStorage, ItemDtoMapper itemDtoMapper) {
        this.itemStorage = itemStorage;
        this.itemDtoMapper = itemDtoMapper;
    }

    public List<ItemDto> getItemsByUserId(Long userId) {
        return itemStorage.getItemsByUserId(userId).stream()
                .map(itemDtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public ItemDto addItem(Long userId, ItemDto itemDto) {
        validateItemDto(itemDto);
        return itemDtoMapper.mapToDto(itemStorage.addItem(userId, itemDtoMapper.mapToItem(itemDto)));
    }

    public ItemDto getItemById(Long itemId) throws NotFoundException {
        for (Item item : itemStorage.getItems()) {
            if (item.getId().equals(itemId)) {
                return itemDtoMapper.mapToDto(item);
            }
        }
        String message = "Вещь id=" + itemId + " не найдена";
        log.error(message);
        throw new NotFoundException(message);
    }

    public ItemDto getItemByIdAndUserId(Long userId, Long itemId) {
        for (Item item : itemStorage.getItemsByUserId(userId)) {
            if (item.getId().equals(itemId)) {
                return itemDtoMapper.mapToDto(item);
            }
        }
        String message = "Вещь id=" + itemId + " не найдена у пользователя id=" + userId;
        log.error(message);
        throw new NotFoundException(message);
    }

    public List<ItemDto> searchItemsByText(Long userId, String text) {
        List<ItemDto> itemsDto = new ArrayList<>();
        List<Item> items = itemStorage.searchItemsByText(userId, text);
        for (Item item : items) {
            itemsDto.add(itemDtoMapper.mapToDto(item));
        }
        return itemsDto;
    }

    public ItemDto updateItem(Long userId, Long itemId, ItemDto newItemDto) {
        ItemDto itemDto = getItemByIdAndUserId(userId, itemId);
        if (newItemDto.getName() != null) itemDto.setName(newItemDto.getName());
        if (newItemDto.getDescription() != null) itemDto.setDescription(newItemDto.getDescription());
        if (newItemDto.getAvailable() != null) itemDto.setAvailable(newItemDto.getAvailable());
        return itemDtoMapper.mapToDto(
                itemStorage.addItem(userId, itemDtoMapper.mapToItem(itemDto))
        );
    }

    public void validateItemDto(ItemDto itemDto) throws WrongDataException {
        StringBuilder message = new StringBuilder();
        if (itemDto.getDescription() == null || itemDto.getName().isBlank()) message.append("Не указано название. ");
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) message.append("Нет описания вещи. ");
        if (itemDto.getAvailable() == null) message.append("Не указана доступность вещи для заказа.");
        if (!message.toString().isBlank()) {
            log.warn("Ошибка валидации вещи: " + message.toString());
            throw new WrongDataException(message.toString());
        }
    }
}
