package com.hoangloc.homilux.service;

import com.hoangloc.homilux.annotation.AbstractPaginationService;
import com.hoangloc.homilux.domain.Event;
import com.hoangloc.homilux.domain.EventType;
import com.hoangloc.homilux.domain.Menu;
import com.hoangloc.homilux.domain.User;
import com.hoangloc.homilux.domain.dto.EventDto;
import com.hoangloc.homilux.exception.ResourceNotFoundException;
import com.hoangloc.homilux.repository.EventRepository;
import com.hoangloc.homilux.repository.EventTypeRepository;
import com.hoangloc.homilux.repository.MenuRepository;
import com.hoangloc.homilux.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class EventService extends AbstractPaginationService<Event, EventDto> {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventTypeRepository eventTypeRepository;
    private final MenuRepository menuRepository;
    private final EmailService emailService;

    public EventService(EventRepository eventRepository, UserRepository userRepository, EventTypeRepository eventTypeRepository, MenuRepository menuRepository, EmailService emailService) {
        super(eventRepository);
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.eventTypeRepository = eventTypeRepository;
        this.menuRepository = menuRepository;
        this.emailService = emailService;
    }

    public EventDto createEvent(Event event) {
        User user = userRepository.findById(event.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "ID", event.getUser().getId()));
        event.setUser(user);

        EventType eventType = eventTypeRepository.findById(event.getEventType().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Loại sự kiện", "ID", event.getEventType().getId()));
        event.setEventType(eventType);

        Menu menu = menuRepository.findById(event.getMenu().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Thực đơn", "ID", event.getMenu().getId()));
        event.setMenu(menu);

        Event savedEvent = eventRepository.save(event);
        sendConfirmationEmail(savedEvent);
        return toDto(savedEvent);
    }

    private void sendConfirmationEmail(Event event) {
        String to = event.getUser().getEmail();
        String subject = "Homilux - Xác Nhận Đăng Ký Sự Kiện";
        String templateName = "event_confirmation";
        String username = event.getUser().getName();
        emailService.sendEmailFromTemplateSync(to, subject, templateName, username, event);
    }

    public EventDto updateEvent(Event updatedEvent) {
        if (updatedEvent.getId() == null) {
            throw new IllegalArgumentException("ID sự kiện không được để trống!");
        }
        Event event = eventRepository.findById(updatedEvent.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Sự kiện", "ID", updatedEvent.getId()));

        if (updatedEvent.getUser() != null) {
            User user = userRepository.findById(updatedEvent.getUser().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "ID", updatedEvent.getUser().getId()));
            event.setUser(user);
        }

        if (updatedEvent.getEventType() != null) {
            EventType eventType = eventTypeRepository.findById(updatedEvent.getEventType().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Loại sự kiện", "ID", updatedEvent.getEventType().getId()));
            event.setEventType(eventType);
        }

        if (updatedEvent.getMenu() != null) {
            Menu menu = menuRepository.findById(updatedEvent.getMenu().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Thực đơn", "ID", updatedEvent.getMenu().getId()));
            event.setMenu(menu);
        }

        event.setLocationType(updatedEvent.getLocationType());
        event.setCustomLocation(updatedEvent.getCustomLocation());
        event.setEstimatedAttendees(updatedEvent.getEstimatedAttendees());
        event.setEventDate(updatedEvent.getEventDate());
        event.setContractDate(updatedEvent.getContractDate());
        event.setServices(updatedEvent.getServices());

        Event savedEvent = eventRepository.save(event);
        return toDto(savedEvent);
    }

    public void deleteEvent(Long id) {
        eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sự kiện", "ID", id));
        eventRepository.deleteById(id);
    }

    public EventDto getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sự kiện", "ID", id));
        return toDto(event);
    }

    @Override
    protected EventDto toDto(Event event) {
        EventDto dto = new EventDto();
        dto.setId(event.getId());
        dto.setUserId(event.getUser().getId());
        dto.setEventTypeId(event.getEventType().getId());
        dto.setMenuId(event.getMenu().getId());
        dto.setLocationType(event.getLocationType());
        dto.setCustomLocation(event.getCustomLocation());
        dto.setEstimatedAttendees(event.getEstimatedAttendees());
        dto.setEventDate(event.getEventDate());
        dto.setContractDate(event.getContractDate());
        dto.setCreatedAt(event.getCreatedAt());
        dto.setUpdatedAt(event.getUpdatedAt());
        return dto;
    }

}