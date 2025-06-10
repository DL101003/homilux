package com.hoangloc.homilux.service;

import com.hoangloc.homilux.annotation.AbstractPaginationService;
import com.hoangloc.homilux.domain.Dish;
import com.hoangloc.homilux.domain.Menu;
import com.hoangloc.homilux.domain.dto.MenuDto;
import com.hoangloc.homilux.exception.ResourceAlreadyExistsException;
import com.hoangloc.homilux.exception.ResourceNotFoundException;
import com.hoangloc.homilux.repository.DishRepository;
import com.hoangloc.homilux.repository.MenuRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuService extends AbstractPaginationService<Menu, MenuDto> {

    private final MenuRepository menuRepository;
    private final DishRepository dishRepository;

    public MenuService(MenuRepository menuRepository, DishRepository dishRepository) {
        super(menuRepository);
        this.menuRepository = menuRepository;
        this.dishRepository = dishRepository;
    }

    public MenuDto createMenu(Menu menu) {
        if (menuRepository.existsByName(menu.getName())) {
            throw new ResourceAlreadyExistsException("Thực đơn", "tên", menu.getName());
        }

        List<Dish> dishes = menu.getDishes().stream()
                .map(d -> dishRepository.findById(d.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Món ăn", "ID", d.getId())))
                .collect(Collectors.toList());
        menu.setDishes(dishes);

        Menu savedMenu = menuRepository.save(menu);
        return toDto(savedMenu);
    }

    public MenuDto updateMenu(Menu updatedMenu) {
        if (updatedMenu.getId() == null) {
            throw new IllegalArgumentException("ID thực đơn không được để trống!");
        }
        Menu menu = menuRepository.findById(updatedMenu.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Thực đơn", "ID", updatedMenu.getId()));

        if (!menu.getName().equals(updatedMenu.getName()) &&
                menuRepository.existsByName(updatedMenu.getName())) {
            throw new ResourceAlreadyExistsException("Thực đơn", "tên", updatedMenu.getName());
        }

        menu.setName(updatedMenu.getName());
        menu.setDescription(updatedMenu.getDescription());

        if (updatedMenu.getDishes() != null) {
            List<Dish> dishes = updatedMenu.getDishes().stream()
                    .map(d -> dishRepository.findById(d.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Món ăn", "ID", d.getId())))
                    .collect(Collectors.toList());
            menu.setDishes(dishes);
        }

        Menu savedMenu = menuRepository.save(menu);
        return toDto(savedMenu);
    }

    public void deleteMenu(Long id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Thực đơn", "ID", id));
        if (!menu.getEvents().isEmpty()) {
            throw new IllegalStateException("Không thể xóa thực đơn đang được sử dụng bởi sự kiện!");
        }
        menuRepository.deleteById(id);
    }

    public MenuDto getMenuById(Long id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Thực đơn", "ID", id));
        return toDto(menu);
    }

    @Override
    protected MenuDto toDto(Menu menu) {
        MenuDto dto = new MenuDto();
        dto.setId(menu.getId());
        dto.setName(menu.getName());
        dto.setDescription(menu.getDescription());
        dto.setDishIds(menu.getDishes().stream()
                .map(Dish::getId)
                .collect(Collectors.toList()));
        dto.setCreatedAt(menu.getCreatedAt());
        dto.setUpdatedAt(menu.getUpdatedAt());
        return dto;
    }
}