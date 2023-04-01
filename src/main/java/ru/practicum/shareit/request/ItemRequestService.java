package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongDataException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
        validateItemRequestDto(requestDto);
        User user = getUserIfExists(userId);
        requestDto.setRequestorId(userId);
        requestDto.setCreated(LocalDateTime.now().toString());
        ItemRequest request = requestRepository.save(requestDtoMapper.toRequest(requestDto));
        return requestDtoMapper.toDto(request);
    }

    public List<ItemRequestDto> getAllRequestsOfUser(Long userId) {
        User user = getUserIfExists(userId);
        return requestRepository.findAllByUserId(userId).stream()
               .map(requestDtoMapper::toDto)
               .collect(Collectors.toList());
    }

    public List<ItemRequestDto> getAllRequests(Long userId, Integer pageNum, Integer pageSize) {
        getUserIfExists(userId);
        if (pageNum == null || pageSize == null) {
            return requestRepository.findAll().stream()
                    .filter(itemRequest -> !itemRequest.getRequestorId().equals(userId))
                    .map(requestDtoMapper::toDto)
                    .collect(Collectors.toList());
        }
        validatePagesRequest(pageNum, pageSize);
        Pageable page = PageRequest.of(pageNum, pageSize);
        return requestRepository.findAll(page).stream()
                .filter(itemRequest -> !itemRequest.getRequestorId().equals(userId))
                .map(requestDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        getUserIfExists(userId);
        return requestDtoMapper.toDto(requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос id=" + requestId + "не найден")));
    }

    private User getUserIfExists(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        return user;
    }

    private void validatePagesRequest(Integer pageNum, Integer pageSize) {
        if (pageNum < 0 || pageSize <= 0) {
            throw new WrongDataException("Ошибка: неыерно указан начальный индекс или размер страницы");
        }
    }

    private void validateItemRequestDto(ItemRequestDto requestDto) {
        if (requestDto.getDescription() == null || requestDto.getDescription().isBlank()) {
            throw new WrongDataException("Передан пустой текст запроса");
        }
    }
}
