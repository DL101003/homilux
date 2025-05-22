package com.hoangloc.homilux.controller;

import com.hoangloc.homilux.domain.Menu;
import com.hoangloc.homilux.domain.dto.MenuDto;
import com.hoangloc.homilux.domain.dto.ResultPaginationDTO;
import com.hoangloc.homilux.service.MenuService;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @PostMapping("/menus")
    public ResponseEntity<MenuDto> createMenu(@Valid @RequestBody Menu menu) {
        MenuDto createdMenu = menuService.createMenu(menu);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMenu);
    }

    @GetMapping("/menus")
    public ResponseEntity<ResultPaginationDTO> getAllMenus(@Filter Specification<Menu> spec, Pageable pageable) {
        return ResponseEntity.ok(menuService.getAll(spec, pageable));
    }

    @GetMapping("/menus/{id}")
    public ResponseEntity<MenuDto> getMenuById(@PathVariable Long id) {
        MenuDto menu = menuService.getMenuById(id);
        return ResponseEntity.ok(menu);
    }

    @PutMapping("/menus")
    public ResponseEntity<MenuDto> updateMenu(@RequestBody Menu menu) {
        MenuDto updatedMenu = menuService.updateMenu(menu);
        return ResponseEntity.ok(updatedMenu);
    }

    @DeleteMapping("/menus/{id}")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return ResponseEntity.noContent().build();
    }
}