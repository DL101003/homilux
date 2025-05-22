package com.hoangloc.homilux.controller;

import com.hoangloc.homilux.domain.Dish;
import com.hoangloc.homilux.domain.dto.DishDto;
import com.hoangloc.homilux.service.DishService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class DishController {

    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @PostMapping("/dishes")
    public ResponseEntity<DishDto> createDish(@Valid @RequestBody Dish dish) {
        DishDto createdDish = dishService.createDish(dish);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDish);
    }

    @GetMapping("/dishes")
    public ResponseEntity<List<DishDto>> getAllDishes(@RequestParam(required = false) String category) {
        List<DishDto> dishes = category != null ? dishService.getDishesByCategory(category) : dishService.getAllDishes();
        return ResponseEntity.ok(dishes);
    }

    @GetMapping("/dishes/{id}")
    public ResponseEntity<DishDto> getDishById(@PathVariable Long id) {
        DishDto dish = dishService.getDishById(id);
        return ResponseEntity.ok(dish);
    }

    @PutMapping("/dishes")
    public ResponseEntity<DishDto> updateDish(@RequestBody Dish dish) {
        DishDto updatedDish = dishService.updateDish(dish);
        return ResponseEntity.ok(updatedDish);
    }

    @DeleteMapping("/dishes/{id}")
    public ResponseEntity<Void> deleteDish(@PathVariable Long id) {
        dishService.deleteDish(id);
        return ResponseEntity.noContent().build();
    }
}