package com.hoangloc.homilux.services;

import com.hoangloc.homilux.annotation.AbstractPaginationService;
import com.hoangloc.homilux.dtos.ResultPaginationDto;
import com.hoangloc.homilux.dtos.bookingDto.BookedServiceRequest;
import com.hoangloc.homilux.dtos.bookingDto.BookedServiceResponse;
import com.hoangloc.homilux.dtos.bookingDto.BookingCreationRequest;
import com.hoangloc.homilux.dtos.bookingDto.BookingResponse;
import com.hoangloc.homilux.dtos.eventTypeDto.EventTypeResponse;
import com.hoangloc.homilux.dtos.userDto.UserResponse;
import com.hoangloc.homilux.entities.*;
import com.hoangloc.homilux.entities.enums.BookingStatus;
import com.hoangloc.homilux.exceptions.ResourceNotFoundException;
import com.hoangloc.homilux.repositories.BookingRepository;
import com.hoangloc.homilux.repositories.EventTypeRepository;
import com.hoangloc.homilux.repositories.RentalServiceRepository;
import com.hoangloc.homilux.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingService extends AbstractPaginationService<Booking, BookingResponse> {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final EventTypeRepository eventTypeRepository;
    private final RentalServiceRepository rentalServiceRepository;
    private final EmailService emailService;

    public BookingService(BookingRepository bookingRepository, UserRepository userRepository, EventTypeRepository eventTypeRepository, RentalServiceRepository rentalServiceRepository, EmailService emailService) {
        super(bookingRepository);
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.eventTypeRepository = eventTypeRepository;
        this.rentalServiceRepository = rentalServiceRepository;
        this.emailService = emailService;
    }

    @Caching(put = {
            @CachePut(cacheNames = "bookingById", key = "#bookingId")
    }, evict = {
            @CacheEvict(cacheNames = "bookingListCurrentUser", allEntries = true)
    })
    public BookingResponse createBooking(BookingCreationRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        EventType eventType = eventTypeRepository.findById(request.eventTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("EventType", request.eventTypeId()));

        Booking booking = Booking.builder()
                .user(user)
                .eventType(eventType)
                .status(BookingStatus.PENDING)
                .eventDatetime(request.eventDatetime())
                .expectedGuests(request.expectedGuests())
                .locationType(request.locationType())
                .address(request.address())
                .customerNotes(request.customerNotes())
                .amountPaid(BigDecimal.ZERO)
                .bookedRentalServices(new HashSet<>())
                .build();

        BigDecimal totalAmount = calculateTotalAndLinkServices(booking, request.services());
        booking.setTotalAmount(totalAmount);

        Booking savedBooking = bookingRepository.save(booking);
        sendConfirmationEmail(savedBooking);
        return toResponse(savedBooking);
    }

    private void sendConfirmationEmail(Booking booking) {
        String to = booking.getUser().getEmail();
        String subject = "Homilux - Xác Nhận Đăng Ký Sự Kiện";
        String templateName = "booking_confirmation";
        String username = booking.getUser().getFullName();
        emailService.sendEmailFromTemplateSync(to, subject, templateName, username, booking);
    }

    private BigDecimal calculateTotalAndLinkServices(Booking booking, List<BookedServiceRequest> serviceRequests) {
        BigDecimal total = BigDecimal.ZERO;
        for (BookedServiceRequest req : serviceRequests) {
            RentalService service = rentalServiceRepository.findById(req.serviceId())
                    .orElseThrow(() -> new ResourceNotFoundException("RentalService", req.serviceId()));

            BookingRentalService bookingService = BookingRentalService.builder()
                    .booking(booking)
                    .rentalService(service)
                    .quantity(req.quantity())
                    .priceAtBooking(service.getPrice())
                    .build();

            booking.getBookedRentalServices().add(bookingService);

            total = total.add(
                    service.getPrice().multiply(BigDecimal.valueOf(req.quantity()))
            );
        }
        return total;
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "bookingById", key = "#id")
    public BookingResponse getBookingById(Long id) {
        return bookingRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", id));
    }

    public BookingResponse updateStatus(Long bookingId, BookingStatus newStatus) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));

        booking.setStatus(newStatus);
        if (newStatus == BookingStatus.COMPLETED && booking.getContractSigningDate() == null) {
            booking.setContractSigningDate(LocalDate.now());
        }

        return toResponse(bookingRepository.save(booking));
    }

    @Override
    protected BookingResponse toResponse(Booking booking) {
        if (booking == null) return null;

        Set<BookedServiceResponse> bookedServiceResponses = booking.getBookedRentalServices().stream()
                .map(this::toBookedServiceResponse)
                .collect(Collectors.toSet());

        return new BookingResponse(
                booking.getId(),
                booking.getStatus(),
                booking.getEventDatetime(),
                booking.getExpectedGuests(),
                booking.getLocationType(),
                booking.getAddress(),
                booking.getContractSigningDate(),
                booking.getTotalAmount(),
                booking.getAmountPaid(),
                booking.getCustomerNotes(),
                toUserSummaryResponse(booking.getUser()),
                toEventTypeResponse(booking.getEventType()),
                bookedServiceResponses,
                booking.getCreatedAt(),
                booking.getUpdatedAt()
        );
    }

    private BookedServiceResponse toBookedServiceResponse(BookingRentalService bookingService) {
        BigDecimal lineTotal = bookingService.getPriceAtBooking()
                .multiply(BigDecimal.valueOf(bookingService.getQuantity()));

        return new BookedServiceResponse(
                bookingService.getRentalService().getId(),
                bookingService.getRentalService().getName(),
                bookingService.getQuantity(),
                bookingService.getPriceAtBooking(),
                lineTotal
        );
    }

    private UserResponse toUserSummaryResponse(User user) {
        return new UserResponse(user.getId(), user.getFullName(), user.getEmail(), user.getAuthProvider(), user.getPhoneNumber(), user.getRole() != null ? user.getRole().getId() : null, user.getRole() != null ? user.getRole().getName() : null);
    }

    private EventTypeResponse toEventTypeResponse(EventType eventType) {
        return new EventTypeResponse(eventType.getId(), eventType.getName());
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "bookingById", key = "#id"),
            @CacheEvict(cacheNames = "bookingListCurrentUser", allEntries = true)
    })
    public void deleteBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", id));
        bookingRepository.delete(booking);
    }

    @Cacheable(cacheNames = "bookingListCurrentUser",
            key = "'p:'+ #pageable.pageNumber + ':s:'+ #pageable.pageSize")
    public ResultPaginationDto getBookingsForCurrentUser(Pageable pageable) {
        String currentUsername = SecurityUtil.getCurrentUser();

        User currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Page<Booking> bookingPage = bookingRepository.findByUser(currentUser, pageable);

        List<BookingResponse> bookingDtos = bookingPage.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta(
                bookingPage.getNumber(),
                bookingPage.getSize(),
                bookingPage.getTotalPages(),
                bookingPage.getTotalElements()
        );

        return new ResultPaginationDto(meta, bookingDtos);
    }
}