package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongDataException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
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
        Sort sortByDate = Sort.by(Sort.Direction.DESC, "created");
        if (pageNum == null && pageSize == null) {
            return requestRepository.findAll().stream()
                    .map(requestDtoMapper::toDto)
                    .collect(Collectors.toList());
        }
        if (pageNum.intValue() < 0 || pageSize.intValue() <= 0) {
            throw new WrongDataException("Ошибка: неправильный размер или номер страницы");
        }
        Pageable page = PageRequest.of(pageNum, pageSize, sortByDate);
        return requestRepository.findAll(page).stream()
                .map(requestDtoMapper::toDto)
                .collect(Collectors.toList());
    }

//    public List<ItemRequestDto> getAllRequests(Long userId) {
//        return requestRepository.findAll().stream()
//                .map(requestDtoMapper::toDto)
//                .collect(Collectors.toList());
//    }

    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        return requestDtoMapper.toDto(requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос id=" + requestId + "не найден")));
    }

//    private List<Item> findItemsForRequests (List<ItemRequest> itemRequests) {
//        List<Long> idList = Collections.EMPTY_LIST;
//        for (ItemRequest itemRequest : itemRequests) {
//            idList.add(itemRequest.)
//        }
//    }

    private User getUserIfExists(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        return user;
    }

    private void validateItemRequestDto(ItemRequestDto requestDto) {
        if (requestDto.getDescription() == null || requestDto.getDescription().isBlank()) {
            throw new WrongDataException("Передан пустой текст запроса");
        }
    }
}
