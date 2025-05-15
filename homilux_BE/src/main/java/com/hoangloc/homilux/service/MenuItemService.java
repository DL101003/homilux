package com.hoangloc.homilux.service;

import com.hoangloc.homilux.domain.MenuItem;
import com.hoangloc.homilux.domain.dto.MenuItemCreateDto;
import com.hoangloc.homilux.domain.dto.MenuItemDto;
import com.hoangloc.homilux.domain.dto.MenuItemUpdateDto;
import com.hoangloc.homilux.exception.ResourceAlreadyExistsException;
import com.hoangloc.homilux.exception.ResourceNotFoundException;
import com.hoangloc.homilux.repository.MenuItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;

    public MenuItemService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public MenuItemCreateDto createMenuItem(MenuItem menuItem) {
        if (menuItemRepository.existsByNameAndDeletedFalse(menuItem.getName())) {
            throw new ResourceAlreadyExistsException("Món ăn", "tên", menuItem.getName());
        }
        menuItem.setDeleted(false);
        MenuItem savedMenuItem = menuItemRepository.save(menuItem);
        return toCreateDto(savedMenuItem);
    }

    public MenuItemDto getMenuItemById(Long id) {
        MenuItem menuItem = menuItemRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Món ăn", "ID", id));
        return toDto(menuItem);
    }

    public List<MenuItemDto> getAllMenuItems() {
        return menuItemRepository.findAllByDeletedFalse()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public MenuItemUpdateDto updateMenuItem(MenuItem updatedMenuItem) {
        if (updatedMenuItem.getId() == null) {
            throw new IllegalArgumentException("ID món ăn không được để trống!");
        }
        MenuItem menuItem = menuItemRepository.findByIdAndDeletedFalse(updatedMenuItem.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Món ăn", "ID", updatedMenuItem.getId()));
        if (updatedMenuItem.getName() != null && !updatedMenuItem.getName().equals(menuItem.getName()) &&
                menuItemRepository.existsByNameAndDeletedFalse(updatedMenuItem.getName())) {
            throw new ResourceAlreadyExistsException("Món ăn", "tên", updatedMenuItem.getName());
        }
        if (updatedMenuItem.getName() != null) {
            menuItem.setName(updatedMenuItem.getName());
        }
        if (updatedMenuItem.getDescription() != null) {
            menuItem.setDescription(updatedMenuItem.getDescription());
        }
        if (updatedMenuItem.getType() != null) {
            menuItem.setType(updatedMenuItem.getType());
        }
        if (updatedMenuItem.getPrice() != null) {
            menuItem.setPrice(updatedMenuItem.getPrice());
        }
        menuItem.setActive(updatedMenuItem.isActive());
        MenuItem savedMenuItem = menuItemRepository.save(menuItem);
        return toUpdateDto(savedMenuItem);
    }

    public void deleteMenuItem(Long id) {
        MenuItem menuItem = menuItemRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Món ăn", "ID", id));
        menuItem.setDeleted(true);
        menuItemRepository.save(menuItem);
    }

    private MenuItemDto toDto(MenuItem menuItem) {
        MenuItemDto dto = new MenuItemDto();
        dto.setId(menuItem.getId());
        dto.setName(menuItem.getName());
        dto.setDescription(menuItem.getDescription());
        dto.setType(menuItem.getType().name());
        dto.setPrice(menuItem.getPrice());
        dto.setActive(menuItem.isActive());
        dto.setCreatedAt(menuItem.getCreatedAt());
        dto.setUpdatedAt(menuItem.getUpdatedAt());
        return dto;
    }

    private MenuItemCreateDto toCreateDto(MenuItem menuItem) {
        MenuItemCreateDto dto = new MenuItemCreateDto();
        dto.setId(menuItem.getId());
        dto.setName(menuItem.getName());
        dto.setDescription(menuItem.getDescription());
        dto.setType(menuItem.getType().name());
        dto.setPrice(menuItem.getPrice());
        dto.setActive(menuItem.isActive());
        dto.setCreatedAt(menuItem.getCreatedAt());
        return dto;
    }

    private MenuItemUpdateDto toUpdateDto(MenuItem menuItem) {
        MenuItemUpdateDto dto = new MenuItemUpdateDto();
        dto.setId(menuItem.getId());
        dto.setName(menuItem.getName());
        dto.setDescription(menuItem.getDescription());
        dto.setType(menuItem.getType().name());
        dto.setPrice(menuItem.getPrice());
        dto.setActive(menuItem.isActive());
        dto.setUpdatedAt(menuItem.getUpdatedAt());
        return dto;
    }
}