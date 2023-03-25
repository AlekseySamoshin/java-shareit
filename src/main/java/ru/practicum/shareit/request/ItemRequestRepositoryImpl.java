package ru.practicum.shareit.request;

import org.springframework.context.annotation.Lazy;

public class ItemRequestRepositoryImpl implements ItemRequestRepositoryCustom {
    private final ItemRequestRepository itemRequestRepository;

    public ItemRequestRepositoryImpl(@Lazy ItemRequestRepository itemRequestRepository) {
        this.itemRequestRepository = itemRequestRepository;
    }
}
