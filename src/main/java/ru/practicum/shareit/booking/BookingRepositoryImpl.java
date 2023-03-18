package ru.practicum.shareit.booking;

import org.springframework.context.annotation.Lazy;

public class BookingRepositoryImpl implements BookingRepositoryCustom {

    private final BookingRepository bookingRepository;

    public BookingRepositoryImpl(@Lazy BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }
}
