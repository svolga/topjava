package ru.javawebinar.topjava.service;

import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.exception.NotFoundException;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFound;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFoundWithId;

@Service
public class MealService {

    private final MealRepository repository;

    public Meal save(Meal meal, int userId) {
        return repository.save(meal, userId);
    }

    public void delete(int id, int userId) {
        checkNotFoundWithId(repository.delete(id, userId), id);
    }

    public Meal get(int id, int userId) {
        return checkNotFoundWithId(repository.get(id, userId), id);
    }

    public void update(Meal meal, int userId) {
        int currentUserId = SecurityUtil.authUserId();
        Meal existingMeal = repository.get(currentUserId, userId);
        if (existingMeal == null) {
            throw new NotFoundException("Meal not found for user " + userId);
        }
        meal.setId(existingMeal.getId());
        checkNotFound(repository.save(meal, userId), meal.getUserId());
    }

    public MealService(MealRepository repository) {
        this.repository = repository;
    }

    public List<Meal> getAll(int userId) {
        return repository.getAll(userId);
    }

    public List<Meal> getFilteredByDates(LocalDate dateStart, LocalDate dateEnd, int userId) {
        return repository.getFilteredByDates(dateStart, dateEnd, userId);
    }

}