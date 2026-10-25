package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Meal> mealsMap = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);
    private final Map<Integer, Set<Integer>> users = new ConcurrentHashMap<>();

    {
        MealsUtil.meals.forEach(meal -> save(meal, meal.getUserId()));
    }

    @Override
    public Meal save(Meal meal, int userId) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            meal.setUserId(userId);
            mealsMap.put(meal.getId(), meal);
            users.computeIfAbsent(userId, k -> new ConcurrentSkipListSet<>());
            users.get(userId).add(meal.getId());
            return meal;
        }

        if (!validateNotNullAndBelongToUser(mealsMap.get(meal.getId()), userId)) {
            return null;
        }

        // handle case: update, but not present in storage
        return mealsMap.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    public Meal get(int id, int userId) {
        Meal meal = mealsMap.get(id);
        if (validateNotNullAndBelongToUser(meal, userId)) {
            return meal;
        }
        return null;
    }

    private boolean validateNotNullAndBelongToUser(Meal meal, int userId) {
        return meal != null && meal.getUserId() == userId;
    }

    @Override
    public boolean delete(int id, int userId) {
        Meal meal = mealsMap.get(id);
        if (validateNotNullAndBelongToUser(meal, userId)) {
            users.get(userId).remove(meal.getId());
            Meal result = mealsMap.remove(id);
            System.out.println("Remove meal " + id + " from mealsMap");
            return result != null;
        }
        return false;
    }

    @Override
    public List<Meal> getAll(int userId) {
        return filterByDates(null, null, userId);
    }

    @Override
    public List<Meal> getFilteredByDates(LocalDate startDate, LocalDate endDate, int userId) {
        return filterByDates(startDate, endDate, userId);
    }

    private List<Meal> filterByDates(LocalDate startDate, LocalDate endDate, int userId) {
        List<Meal> meals = new ArrayList<>();
        Set<Integer> mealIds = new TreeSet<>(users.get(userId));
        for (Integer mealId : mealIds) {
            Meal meal = mealsMap.get(mealId);
            meals.add(meal);
        }
        return meals.stream()
                .filter(Objects::nonNull)
                .filter(meal -> DateTimeUtil.isBetweenDates(meal.getDate(), startDate, endDate))
                .sorted(Comparator.comparing(Meal::getDate).reversed())
                .collect(Collectors.toList());
    }
}
