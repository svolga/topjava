package ru.javawebinar.topjava.service;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MealServiceMemoryImpl implements MealService {

    private final Map<Integer, Meal> meals = new ConcurrentHashMap<>();
    private final AtomicInteger lastId = new AtomicInteger();

    public MealServiceMemoryImpl() {

        List<Meal> meals = Arrays.asList(
                new Meal(getNewId(), LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new Meal(getNewId(), LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new Meal(getNewId(), LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new Meal(getNewId(), LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new Meal(getNewId(), LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new Meal(getNewId(), LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new Meal(getNewId(), LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );
        meals.forEach(this::add);
    }

    private int getNewId() {
        return lastId.incrementAndGet();
    }

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(meals.values());
    }

    @Override
    public Meal get(int id) {
        return meals.get(id);
    }

    @Override
    public Meal add(Meal meal) {
        int newId = lastId.incrementAndGet();
        meal.setId(newId);
        meals.put(newId, meal);
        return meal;
    }

    @Override
    public void delete(int id) {
        meals.remove(id);
    }

    @Override
    public Meal update(Meal meal) {
        return meals.computeIfPresent(meal.getId(), (key, oldValue) -> meal);
    }
}
