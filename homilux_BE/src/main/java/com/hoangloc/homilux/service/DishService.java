package com.hoangloc.homilux.service;

import com.hoangloc.homilux.annotation.AbstractPaginationService;
import com.hoangloc.homilux.domain.Dish;
import com.hoangloc.homilux.domain.dto.DishDto;
import com.hoangloc.homilux.exception.ResourceAlreadyExistsException;
import com.hoangloc.homilux.exception.ResourceNotFoundException;
import com.hoangloc.homilux.repository.DishRepository;
import org.springframework.stereotype.Service;

@Service
public class DishService extends AbstractPaginationService<Dish, DishDto> {

    private final DishRepository dishRepository;

    public DishService(DishRepository dishRepository) {
        super(dishRepository);
        this.dishRepository = dishRepository;
    }

    public DishDto createDish(Dish dish) {
        if (dishRepository.existsByName(dish.getName())) {
            throw new ResourceAlreadyExistsException("Món ăn", "tên", dish.getName());
        }
        Dish savedDish = dishRepository.save(dish);
        return toDto(savedDish);
    }

    public DishDto updateDish(Dish updatedDish) {
        if (updatedDish.getId() == null) {
            throw new IllegalArgumentException("ID món ăn không được để trống!");
        }
        Dish dish = dishRepository.findById(updatedDish.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Món ăn", "ID", updatedDish.getId()));

        if (!dish.getName().equals(updatedDish.getName()) &&
                dishRepository.existsByName(updatedDish.getName())) {
            throw new ResourceAlreadyExistsException("Món ăn", "tên", updatedDish.getName());
        }

        dish.setName(updatedDish.getName());
        dish.setPrice(updatedDish.getPrice());
        dish.setDescription(updatedDish.getDescription());
        dish.setImageUrl(updatedDish.getImageUrl());
        dish.setCategory(updatedDish.getCategory());

        Dish savedDish = dishRepository.save(dish);
        return toDto(savedDish);
    }

    public void deleteDish(Long id) {
        dishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Món ăn", "ID", id));
        dishRepository.deleteById(id);
    }

    public DishDto getDishById(Long id) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Món ăn", "ID", id));
        return toDto(dish);
    }

    @Override
    protected DishDto toDto(Dish dish) {
        DishDto dto = new DishDto();
        dto.setId(dish.getId());
        dto.setName(dish.getName());
        dto.setPrice(dish.getPrice());
        dto.setDescription(dish.getDescription());
        dto.setImageUrl(dish.getImageUrl());
        dto.setCategory(dish.getCategory());
        dto.setCreatedAt(dish.getCreatedAt());
        dto.setUpdatedAt(dish.getUpdatedAt());
        return dto;
    }
}