package com.hoangloc.homilux.service;

import com.hoangloc.homilux.annotation.AbstractPaginationService;
import com.hoangloc.homilux.config.VNPayConfig;
import com.hoangloc.homilux.domain.Dish;
import com.hoangloc.homilux.domain.Event;
import com.hoangloc.homilux.domain.Payment;
import com.hoangloc.homilux.domain.dto.PaymentDto;
import com.hoangloc.homilux.exception.ResourceAlreadyExistsException;
import com.hoangloc.homilux.exception.ResourceNotFoundException;
import com.hoangloc.homilux.repository.EventRepository;
import com.hoangloc.homilux.repository.PaymentRepository;
import com.hoangloc.homilux.util.PaymentMethod;
import com.hoangloc.homilux.util.PaymentStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;

@Service
public class PaymentService extends AbstractPaginationService<Payment, PaymentDto> {

    private final PaymentRepository paymentRepository;
    private final EventRepository eventRepository;
    private final VNPayConfig vnPayConfig;

    public PaymentService(PaymentRepository paymentRepository, EventRepository eventRepository, VNPayConfig vnPayConfig) {
        super(paymentRepository);
        this.paymentRepository = paymentRepository;
        this.eventRepository = eventRepository;
        this.vnPayConfig = vnPayConfig;
    }

    public PaymentDto createPayment(Payment payment) {
        Event event = eventRepository.findById(payment.getEvent().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Sự kiện", "ID", payment.getEvent().getId()));
        payment.setEvent(event);

        Payment savedPayment = paymentRepository.save(payment);
        return toDto(savedPayment);
    }

    public PaymentDto updatePayment(Payment updatedPayment) {
        if (updatedPayment.getId() == null) {
            throw new IllegalArgumentException("ID thanh toán không được để trống!");
        }
        Payment payment = paymentRepository.findById(updatedPayment.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Thanh toán", "ID", updatedPayment.getId()));

        if (!payment.getTransactionId().equals(updatedPayment.getTransactionId()) &&
                paymentRepository.existsByTransactionId(updatedPayment.getTransactionId())) {
            throw new ResourceAlreadyExistsException("Thanh toán", "mã thanh toán", updatedPayment.getTransactionId());
        }

        if (updatedPayment.getEvent() != null) {
            Event event = eventRepository.findById(updatedPayment.getEvent().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sự kiện", "ID", updatedPayment.getEvent().getId()));
            payment.setEvent(event);
        }

        payment.setTransactionId(updatedPayment.getTransactionId());
        payment.setAmount(updatedPayment.getAmount());
        payment.setPaymentDate(updatedPayment.getPaymentDate());
        payment.setStatus(updatedPayment.getStatus());

        Payment savedPayment = paymentRepository.save(payment);
        return toDto(savedPayment);
    }

    public void deletePayment(Long id) {
        paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Thanh toán", "ID", id));
        paymentRepository.deleteById(id);
    }

    public PaymentDto getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Thanh toán", "ID", id));
        return toDto(payment);
    }

    @Override
    protected PaymentDto toDto(Payment payment) {
        PaymentDto dto = new PaymentDto();
        dto.setId(payment.getId());
        dto.setEventId(payment.getEvent().getId());
        dto.setTransactionId(payment.getTransactionId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setStatus(payment.getStatus());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setPaymentProvider(payment.getPaymentProvider());
        dto.setPaymentUrl(payment.getPaymentUrl());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setUpdatedAt(payment.getUpdatedAt());
        return dto;
    }

    @Transactional
    public String createVNPayPayment(Long eventId, String ipAddress) throws Exception {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Sự kiện", "ID", eventId));
        double amount = event.getMenu().getDishes().stream().mapToDouble(Dish::getPrice).sum();
        String transactionId = "VNP" + System.currentTimeMillis();

        Payment payment = new Payment();
        payment.setEvent(event);
        payment.setAmount(amount);
        payment.setTransactionId(transactionId);
        payment.setPaymentDate(Instant.now());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentMethod(PaymentMethod.ONLINE);
        payment.setPaymentProvider("VNPAY");
        paymentRepository.save(payment);

        Map<String, String> vnpParams = new TreeMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", vnPayConfig.getTmnCode());
        vnpParams.put("vnp_Amount", String.valueOf((long) (amount * 100)));
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", transactionId);
        vnpParams.put("vnp_OrderInfo", "Thanh toan su kien: " + eventId);
        vnpParams.put("vnp_OrderType", "250000");
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
        vnpParams.put("vnp_IpAddr", ipAddress);
        vnpParams.put("vnp_CreateDate", DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()));
        vnpParams.put("vnp_ExpireDate", DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now().plusMinutes(15)));

        String hashData = vnpParams.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII))
                .reduce((a, b) -> a + "&" + b).orElse("");
        String queryString = vnpParams.entrySet().stream()
                .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.US_ASCII) + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII))
                .reduce((a, b) -> a + "&" + b).orElse("");
        String checksum = generateChecksum(hashData);
        queryString += "&vnp_SecureHash=" + checksum;

        String paymentUrl = vnPayConfig.getUrl() + "?" + queryString;
        payment.setPaymentUrl(paymentUrl);
        paymentRepository.save(payment);

        return paymentUrl;
    }

    @Transactional
    public boolean handleVNPayCallback(Map<String, String> params) throws Exception {
        String vnpSecureHash = params.remove("vnp_SecureHash");
        if (vnpSecureHash == null) {
            return false;
        }

        String calculatedChecksum = generateChecksum(params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII))
                .reduce((a, b) -> a + "&" + b).orElse(""));
        if (!vnpSecureHash.equals(calculatedChecksum)) {
            return false;
        }

        String transactionId = params.get("vnp_TxnRef");
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Thanh toán", "transactionId", transactionId));
        payment.setStatus("00".equals(params.get("vnp_ResponseCode")) ? PaymentStatus.COMPLETED : PaymentStatus.FAILED);
        paymentRepository.save(payment);
        return true;
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