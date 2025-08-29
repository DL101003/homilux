package com.hoangloc.homilux.services;

import com.hoangloc.homilux.annotation.AbstractPaginationService;
import com.hoangloc.homilux.config.VNPayConfig;
import com.hoangloc.homilux.dtos.paymentDto.PaymentRequest;
import com.hoangloc.homilux.dtos.paymentDto.PaymentResponse;
import com.hoangloc.homilux.dtos.userDto.UserResponse;
import com.hoangloc.homilux.entities.Booking;
import com.hoangloc.homilux.entities.Payment;
import com.hoangloc.homilux.entities.User;
import com.hoangloc.homilux.entities.enums.PaymentMethod;
import com.hoangloc.homilux.entities.enums.PaymentStatus;
import com.hoangloc.homilux.exceptions.InvalidRequestException;
import com.hoangloc.homilux.exceptions.ResourceNotFoundException;
import com.hoangloc.homilux.repositories.BookingRepository;
import com.hoangloc.homilux.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PaymentService extends AbstractPaginationService<Payment, PaymentResponse> {

    private final PaymentRepository paymentRepository;

    private final BookingRepository bookingRepository;

    private final VNPayConfig vnPayConfig;

    public PaymentService(PaymentRepository paymentRepository, BookingRepository bookingRepository, VNPayConfig vnPayConfig) {
        super(paymentRepository);
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.vnPayConfig = vnPayConfig;
    }


    @Transactional
    public PaymentResponse createPayment(Long bookingId, PaymentRequest request) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));

        // Kiểm tra logic nghiệp vụ: tổng số tiền đã trả không vượt quá tổng hóa đơn
        BigDecimal newTotalPaid = booking.getAmountPaid().add(request.amount());
        if (newTotalPaid.compareTo(booking.getTotalAmount()) > 0) {
            throw new InvalidRequestException("Payment amount exceeds the remaining balance.");
        }

        Payment payment = Payment.builder()
                .booking(booking)
                .amount(request.amount())
                .paymentMethod(request.paymentMethod())
                .paymentDate(Instant.now())
                .transactionId(request.transactionId())
                .notes(request.notes())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        // Cập nhật số tiền đã trả trong Booking
        BigDecimal currentPaid = booking.getAmountPaid() != null ? booking.getAmountPaid() : BigDecimal.ZERO;
        booking.setAmountPaid(currentPaid.add(savedPayment.getAmount()));
        bookingRepository.save(booking);

        log.info("Created payment {} for booking {}", savedPayment.getId(), booking.getId());
        return toResponse(savedPayment);
    }

    
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsForBooking(Long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new ResourceNotFoundException("Booking", bookingId);
        }
        return paymentRepository.findByBookingId(bookingId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));
        return toResponse(payment);
    }

    
    @Transactional
    public void deletePayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));

        Booking booking = payment.getBooking();

        // **Logic nghiệp vụ quan trọng**: Cập nhật lại số tiền đã trả trong Booking
        BigDecimal newAmountPaid = booking.getAmountPaid().subtract(payment.getAmount());
        booking.setAmountPaid(newAmountPaid);
        bookingRepository.save(booking);

        paymentRepository.delete(payment);
        log.info("Deleted payment {} and updated amountPaid for booking {}", paymentId, booking.getId());
    }

    @Override
    protected PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getPaymentDate(),
                payment.getTransactionId(),
                payment.getNotes()
        );
    }

@Transactional
public String createVNPayPayment(Long bookingId, String ipAddress) throws Exception {
    Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));
    
    // Calculate remaining amount to be paid
    BigDecimal remainingAmount = booking.getTotalAmount().subtract(booking.getAmountPaid() != null ? 
            booking.getAmountPaid() : BigDecimal.ZERO);
    
    if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
        throw new InvalidRequestException("This booking is already fully paid");
    }
    
    String transactionId = "VNP" + System.currentTimeMillis();

    Payment payment = Payment.builder()
            .booking(booking)
            .amount(remainingAmount)
            .transactionId(transactionId)
            .paymentDate(Instant.now())
            .status(PaymentStatus.PENDING)
            .paymentMethod(PaymentMethod.THIRD_PARTY_GATEWAY)
            .notes("VNPay Payment for Booking #" + bookingId)
            .build();
    
    paymentRepository.save(payment);

    Map<String, String> vnpParams = new TreeMap<>();
    vnpParams.put("vnp_Version", "2.1.0");
    vnpParams.put("vnp_Command", "pay");
    vnpParams.put("vnp_TmnCode", vnPayConfig.getTmnCode());
    vnpParams.put("vnp_Amount", String.valueOf(remainingAmount.multiply(new BigDecimal(100)).longValue()));
    vnpParams.put("vnp_CurrCode", "VND");
    vnpParams.put("vnp_TxnRef", transactionId);
    vnpParams.put("vnp_OrderInfo", "Thanh toan don hang: " + bookingId);
    vnpParams.put("vnp_OrderType", "250000");
    vnpParams.put("vnp_Locale", "vn");
    vnpParams.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
    vnpParams.put("vnp_IpAddr", ipAddress);
    vnpParams.put("vnp_CreateDate", DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()));
    vnpParams.put("vnp_ExpireDate", DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now().plusMinutes(15)));

    StringBuilder queryBuilder = new StringBuilder();
    for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
        if (!queryBuilder.isEmpty()) {
            queryBuilder.append("&");
        }
        queryBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
        queryBuilder.append("=");
        queryBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
    }
    
    String queryString = queryBuilder.toString();
    String checksum = generateChecksum(queryString);
    queryString += "&vnp_SecureHash=" + checksum;

    return vnPayConfig.getUrl() + "?" + queryString;
}

@Transactional
public boolean handleVNPayCallback(Map<String, String> params) throws Exception {
    // Create a copy of the params to avoid modifying the original
    Map<String, String> vnpParams = new TreeMap<>(params);
    String vnpSecureHash = vnpParams.remove("vnp_SecureHash");
    
    if (vnpSecureHash == null) {
        log.error("VNPay callback missing secure hash");
        return false;
    }

    // Build the hash data string
    StringBuilder hashData = new StringBuilder();
    for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
        if (!hashData.isEmpty()) {
            hashData.append("&");
        }
        hashData.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
    }
    
    String calculatedChecksum = generateChecksum(hashData.toString());
    if (!vnpSecureHash.equals(calculatedChecksum)) {
        log.error("VNPay checksum verification failed");
        return false;
    }

    String transactionId = params.get("vnp_TxnRef");
    String responseCode = params.get("vnp_ResponseCode");
    
    Payment payment = paymentRepository.findByTransactionId(transactionId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment with transactionId", transactionId));
    
    boolean isSuccessful = "00".equals(responseCode);
    payment.setStatus(isSuccessful ? PaymentStatus.SUCCESSFUL : PaymentStatus.FAILED);
    paymentRepository.save(payment);
    
    // Update booking's amountPaid if payment is successful
    if (isSuccessful) {
        Booking booking = payment.getBooking();
        BigDecimal currentPaid = booking.getAmountPaid() != null ? booking.getAmountPaid() : BigDecimal.ZERO;
        booking.setAmountPaid(currentPaid.add(payment.getAmount()));
        bookingRepository.save(booking);
        log.info("Payment successful: {} for booking {}", payment.getId(), booking.getId());
    } else {
        log.warn("Payment failed: {} with response code {}", payment.getId(), responseCode);
    }
    
    return isSuccessful;
}

    private String generateChecksum(String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA512");
        mac.init(new SecretKeySpec(vnPayConfig.getHashSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

}