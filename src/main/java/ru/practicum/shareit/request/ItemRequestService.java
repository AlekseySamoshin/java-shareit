package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

@Service
public class ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRequestDtoMapper requestDtoMapper;

    @Autowired
    public ItemRequestService(ItemRequestRepository requestRepository,
                              UserRepository userRepository, ItemRequestDtoMapper requestDtoMapper) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.requestDtoMapper = requestDtoMapper;
    }

    public ItemRequestDto addItemRequest(Long userId, ItemRequestDto requestDto) {
//        validateItemRequestDto(itemDto);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id=" + userId + " не найден")
        );
        ItemRequest request = requestRepository.save(requestDtoMapper.toRequest(requestDto));
        return requestDtoMapper.toDto(request);
    }

    public ItemRequestDto getAllRequestsOfUser(Long userId) {
        userRepository.findAllByUserId(userId);
    }
}
