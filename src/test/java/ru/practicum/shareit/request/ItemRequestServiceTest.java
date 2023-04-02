package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ItemRequestServiceTest {

    @Mock
    ItemRequestRepository itemRequestRepository;

    @Mock
    Pageable pageable;

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRequestDtoMapper itemRequestDtoMapper;

    @InjectMocks
    ItemRequestService itemRequestService;

    private User requestor;
    private User owner;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        requestor = new User();
        requestor.setId(1L);
        requestor.setName("requestor");
        requestor.setEmail("requestor@email.com");

        owner = new User();
        owner.setId(2L);
        owner.setName("owner");
        owner.setEmail("owner@email.com");

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("description");
        itemRequest.setRequestorId(1L);

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("dto description");
    }

    @Test
    void addItemRequest() {
        when(userRepository.findById(any())).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);
        when(itemRequestDtoMapper.toDto(any())).then(Mockito.CALLS_REAL_METHODS);
        when(itemRequestDtoMapper.toRequest(any())).then(Mockito.CALLS_REAL_METHODS);
        ItemRequestDto testItemRequestDto = itemRequestService.addItemRequest(requestor.getId(), itemRequestDto);
        assertEquals(itemRequest.getId(), testItemRequestDto.getId());
        assertEquals(itemRequest.getDescription(), testItemRequestDto.getDescription());
    }

    @Test
    void getAllRequestsOfUser() {
        when(userRepository.findById(any())).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.findAllByUserId(any())).thenReturn(List.of(itemRequest));
        when(itemRequestDtoMapper.toDto(any())).then(Mockito.CALLS_REAL_METHODS);
        when(itemRequestDtoMapper.toRequest(any())).then(Mockito.CALLS_REAL_METHODS);
        List<ItemRequestDto> testItemRequestList = itemRequestService.getAllRequestsOfUser(requestor.getId());
        assertEquals(itemRequest.getId(), testItemRequestList.get(0).getId());
        assertEquals(itemRequest.getDescription(), testItemRequestList.get(0).getDescription());
    }

    @Test
    void getAllRequests() {
        List<ItemRequest> itemRequestList = new ArrayList<>();
        itemRequestList.add(itemRequest);
        when(userRepository.findById(any())).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.findAll()).thenReturn(itemRequestList);
        when(itemRequestRepository.findAll(PageRequest.of(1, 10))).thenReturn(Page.empty());
        when(itemRequestDtoMapper.toDto(any())).then(Mockito.CALLS_REAL_METHODS);
        when(itemRequestDtoMapper.toRequest(any())).then(Mockito.CALLS_REAL_METHODS);
        List<ItemRequestDto> testListForRequestor = itemRequestService.getAllRequests(requestor.getId(), 1, 10);
        List<ItemRequestDto> testListForRequestorNoPagination = itemRequestService.getAllRequests(requestor.getId(), null, null);
        List<ItemRequestDto> testListForUser = itemRequestService.getAllRequests(owner.getId(), 1, 10);
        List<ItemRequestDto> testListForUserNoPagination = itemRequestService.getAllRequests(owner.getId(), null, null);
        assertEquals(0, testListForRequestor.size());
        assertEquals(0, testListForRequestorNoPagination.size());
        assertEquals(0, testListForUser.size());
        assertEquals(1, testListForUserNoPagination.size());
        assertEquals(itemRequest.getId(), testListForUserNoPagination.get(0).getId());

    }

    @Test
    void getRequestById() {
        when(userRepository.findById(any())).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));
        when(itemRequestDtoMapper.toDto(any())).then(Mockito.CALLS_REAL_METHODS);
        when(itemRequestDtoMapper.toRequest(any())).then(Mockito.CALLS_REAL_METHODS);
        ItemRequestDto testItemRequestDto = itemRequestService.getRequestById(owner.getId(), itemRequest.getId());
        assertEquals(itemRequest.getId(), testItemRequestDto.getId());
    }
}