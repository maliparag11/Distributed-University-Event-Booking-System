package com.booking_service.service;

import com.booking_service.client.EventClient;
import com.booking_service.client.PaymentClient;
import com.booking_service.dto.BookingRequestDto;
import com.booking_service.entity.Booking;
import com.booking_service.entity.BookingStatus;
import com.booking_service.entity.PaymentStatus;
import com.booking_service.exception.CustomException;
import com.booking_service.repository.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final EventClient eventClient;
    private final PaymentClient paymentClient;

    public BookingService(BookingRepository bookingRepository,
                          EventClient eventClient,
                          PaymentClient paymentClient) {
        this.bookingRepository = bookingRepository;
        this.eventClient = eventClient;
        this.paymentClient = paymentClient;
    }

    @Transactional
    public Booking createBooking(BookingRequestDto bookingRequestDto) {

        Booking booking = new Booking();
        booking.setUserId(bookingRequestDto.getUserId());
        booking.setEventId(bookingRequestDto.getEventId());
        booking.setBookingStatus(BookingStatus.PENDING);
        booking.setPaymentStatus(PaymentStatus.PENDING);

        booking = bookingRepository.save(booking);

        paymentClient.notifyPaymentService(booking.getId());

        return booking;
    }

    @Transactional
    public Booking confirmBooking(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new CustomException("Booking not found with id " + bookingId));

        boolean seatReserved = eventClient.reserveSeat(booking.getEventId());
        if (!seatReserved) {
            throw new CustomException("Seat reservation failed after payment");
        }

        booking.setPaymentStatus(PaymentStatus.COMPLETED);
        booking.setBookingStatus(BookingStatus.CONFIRMED);

        return bookingRepository.save(booking);
    }

    @Transactional(readOnly = true)
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() ->
                        new CustomException("Booking not found with id " + id));
    }

    @Transactional
    public Booking cancelBooking(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new CustomException("Booking not found with id " + bookingId));

        booking.setPaymentStatus(PaymentStatus.FAILED);
        booking.setBookingStatus(BookingStatus.CANCELLED);

        return bookingRepository.save(booking);
    }
}