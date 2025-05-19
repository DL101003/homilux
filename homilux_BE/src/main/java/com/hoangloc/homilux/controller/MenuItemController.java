package com.hoangloc.homilux.controller;

import com.hoangloc.homilux.annotation.ApiMessage;
import com.hoangloc.homilux.domain.MenuItem;
import com.hoangloc.homilux.domain.dto.MenuItemCreateDto;
import com.hoangloc.homilux.domain.dto.MenuItemDto;
import com.hoangloc.homilux.domain.dto.MenuItemUpdateDto;
import com.hoangloc.homilux.service.MenuItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class MenuItemController {

    private final MenuItemService menuItemService;

    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @PostMapping("/menu-items")
    @ApiMessage("Create a menu item")
    public ResponseEntity<MenuItemCreateDto> createMenuItem(@Valid @RequestBody MenuItem menuItem) {
        MenuItemCreateDto createdMenuItem = menuItemService.createMenuItem(menuItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMenuItem);
    }

    @GetMapping("/menu-items")
    @ApiMessage("Get all menu items")
    public ResponseEntity<List<MenuItemDto>> getAllMenuItems() {
        List<MenuItemDto> menuItems = menuItemService.getAllMenuItems();
        return ResponseEntity.ok(menuItems);
    }

    @GetMapping("/menu-items/{id}")
    @ApiMessage("Get a menu item by id")
    public ResponseEntity<MenuItemDto> getMenuItemById(@PathVariable Long id) {
        MenuItemDto menuItem = menuItemService.getMenuItemById(id);
        return ResponseEntity.ok(menuItem);
    }

    @PutMapping("/menu-items")
    @ApiMessage("Update a menu item")
    public ResponseEntity<MenuItemUpdateDto> updateMenuItem(@RequestBody MenuItem menuItem) {
        MenuItemUpdateDto updatedMenuItem = menuItemService.updateMenuItem(menuItem);
        return ResponseEntity.ok(updatedMenuItem);
    }

    @DeleteMapping("/menu-items/{id}")
    @ApiMessage("Delete a menu item by id")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }
}