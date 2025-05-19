package com.hoangloc.homilux.service;

import com.hoangloc.homilux.domain.Booking;
import com.hoangloc.homilux.domain.MenuItem;
import com.hoangloc.homilux.domain.ServicePackage;
import com.hoangloc.homilux.domain.User;
import com.hoangloc.homilux.domain.dto.BookingCreateDto;
import com.hoangloc.homilux.domain.dto.BookingDto;
import com.hoangloc.homilux.domain.dto.BookingUpdateDto;
import com.hoangloc.homilux.exception.ResourceAlreadyExistsException;
import com.hoangloc.homilux.exception.ResourceNotFoundException;
import com.hoangloc.homilux.repository.BookingRepository;
import com.hoangloc.homilux.repository.MenuItemRepository;
import com.hoangloc.homilux.repository.ServicePackageRepository;
import com.hoangloc.homilux.repository.UserRepository;
import com.hoangloc.homilux.util.BookingStatus;
import com.hoangloc.homilux.util.PaymentStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ServicePackageRepository servicePackageRepository;
    private final MenuItemRepository menuItemRepository;

    public BookingService(BookingRepository bookingRepository, UserRepository userRepository,
                          ServicePackageRepository servicePackageRepository, MenuItemRepository menuItemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.servicePackageRepository = servicePackageRepository;
        this.menuItemRepository = menuItemRepository;
    }

    public BookingCreateDto createBooking(Booking booking) {
        User user = userRepository.findById(booking.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "ID", booking.getUser().getId()));
        ServicePackage servicePackage = servicePackageRepository.findById(booking.getServicePackage().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Gói dịch vụ", "ID", booking.getServicePackage().getId()));
        List<MenuItem> menuItems = booking.getMenuItems().stream()
                .map(item -> menuItemRepository.findById(item.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Món ăn", "ID", item.getId())))
                .collect(Collectors.toList());
        booking.setUser(user);
        booking.setServicePackage(servicePackage);
        booking.setMenuItems(menuItems);
        booking.setStatus(BookingStatus.CHO_XAC_NHAN);
        booking.setPaymentStatus(PaymentStatus.CHUA_THANH_TOAN);
        Booking savedBooking = bookingRepository.save(booking);
        return toCreateDto(savedBooking);
    }

    public BookingDto getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đặt lịch", "ID", id));
        return toDto(booking);
    }

    public List<BookingDto> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public BookingUpdateDto updateBooking(Booking updatedBooking) {
        if (updatedBooking.getId() == null) {
            throw new IllegalArgumentException("ID đặt lịch không được để trống!");
        }
        Booking booking = bookingRepository.findById(updatedBooking.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Đặt lịch", "ID", updatedBooking.getId()));
        if (updatedBooking.getUser() != null && updatedBooking.getUser().getId() != null) {
            User user = userRepository.findById(updatedBooking.getUser().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "ID", updatedBooking.getUser().getId()));
            booking.setUser(user);
        }
        if (updatedBooking.getEventType() != null) {
            booking.setEventType(updatedBooking.getEventType());
        }
        if (updatedBooking.getLocationType() != null || updatedBooking.getCustomLocationAddress() != null) {
            String locationType = updatedBooking.getLocationType() != null ? updatedBooking.getLocationType().name() : booking.getLocationType().name();
            String address = updatedBooking.getCustomLocationAddress() != null ? updatedBooking.getCustomLocationAddress() : booking.getCustomLocationAddress();
            if (bookingRepository.existsByEventDateAndLocationType(booking.getEventDate(), locationType) ||
                    bookingRepository.existsByEventDateAndCustomLocationAddress(booking.getEventDate(), address)) {
                throw new ResourceAlreadyExistsException("Đặt lịch", "ngày và địa điểm",
                        booking.getEventDate() + ", " + address);
            }
            if (updatedBooking.getLocationType() != null) {
                booking.setLocationType(updatedBooking.getLocationType());
            }
            if (updatedBooking.getCustomLocationAddress() != null) {
                booking.setCustomLocationAddress(updatedBooking.getCustomLocationAddress());
            }
        }
        if (updatedBooking.getEventDate() != null) {
            if (bookingRepository.existsByEventDateAndLocationType(
                    updatedBooking.getEventDate(), booking.getLocationType().name()) ||
                    bookingRepository.existsByEventDateAndCustomLocationAddress(
                            updatedBooking.getEventDate(), booking.getCustomLocationAddress())) {
                throw new ResourceAlreadyExistsException("Đặt lịch", "ngày và địa điểm",
                        updatedBooking.getEventDate() + ", " + booking.getCustomLocationAddress());
            }
            booking.setEventDate(updatedBooking.getEventDate());
        }
        if (updatedBooking.getServicePackage() != null && updatedBooking.getServicePackage().getId() != null) {
            ServicePackage servicePackage = servicePackageRepository.findById(updatedBooking.getServicePackage().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Gói dịch vụ", "ID", updatedBooking.getServicePackage().getId()));
            booking.setServicePackage(servicePackage);
        }
        if (updatedBooking.getMenuItems() != null) {
            List<MenuItem> menuItems = updatedBooking.getMenuItems().stream()
                    .map(item -> menuItemRepository.findById(item.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Món ăn", "ID", item.getId())))
                    .collect(Collectors.toList());
            booking.setMenuItems(menuItems);
        }
        if (updatedBooking.getDescription() != null) {
            booking.setDescription(updatedBooking.getDescription());
        }
        if (updatedBooking.getTotalPrice() != null) {
            booking.setTotalPrice(updatedBooking.getTotalPrice());
        }
        if (updatedBooking.getPaymentStatus() != null) {
            booking.setPaymentStatus(updatedBooking.getPaymentStatus());
        }
        if (updatedBooking.getStatus() != null) {
            booking.setStatus(updatedBooking.getStatus());
        }
        Booking savedBooking = bookingRepository.save(booking);
        return toUpdateDto(savedBooking);
    }

    public void deleteBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đặt lịch", "ID", id));
        bookingRepository.save(booking);
    }

    private BookingDto toDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setUserId(booking.getUser().getId());
        dto.setEventType(booking.getEventType().name());
        dto.setLocationType(booking.getLocationType().name());
        dto.setCustomLocationAddress(booking.getCustomLocationAddress());
        dto.setServicePackageId(booking.getServicePackage().getId());
        dto.setMenuItemIds(booking.getMenuItems().stream().map(MenuItem::getId).collect(Collectors.toList()));
        dto.setEventDate(booking.getEventDate());
        dto.setDescription(booking.getDescription());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setPaymentStatus(booking.getPaymentStatus().name());
        dto.setStatus(booking.getStatus().name());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setUpdatedAt(booking.getUpdatedAt());
        return dto;
    }

    private BookingCreateDto toCreateDto(Booking booking) {
        BookingCreateDto dto = new BookingCreateDto();
        dto.setId(booking.getId());
        dto.setUserId(booking.getUser().getId());
        dto.setEventType(booking.getEventType().name());
        dto.setLocationType(booking.getLocationType().name());
        dto.setCustomLocationAddress(booking.getCustomLocationAddress());
        dto.setServicePackageId(booking.getServicePackage().getId());
        dto.setMenuItemIds(booking.getMenuItems().stream().map(MenuItem::getId).collect(Collectors.toList()));
        dto.setEventDate(booking.getEventDate());
        dto.setDescription(booking.getDescription());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setPaymentStatus(booking.getPaymentStatus().name());
        dto.setStatus(booking.getStatus().name());
        dto.setCreatedAt(booking.getCreatedAt());
        return dto;
    }

    private BookingUpdateDto toUpdateDto(Booking booking) {
        BookingUpdateDto dto = new BookingUpdateDto();
        dto.setId(booking.getId());
        dto.setUserId(booking.getUser().getId());
        dto.setEventType(booking.getEventType().name());
        dto.setLocationType(booking.getLocationType().name());
        dto.setCustomLocationAddress(booking.getCustomLocationAddress());
        dto.setServicePackageId(booking.getServicePackage().getId());
        dto.setMenuItemIds(booking.getMenuItems().stream().map(MenuItem::getId).collect(Collectors.toList()));
        dto.setEventDate(booking.getEventDate());
        dto.setDescription(booking.getDescription());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setPaymentStatus(booking.getPaymentStatus().name());
        dto.setStatus(booking.getStatus().name());
        dto.setUpdatedAt(booking.getUpdatedAt());
        return dto;
    }
}