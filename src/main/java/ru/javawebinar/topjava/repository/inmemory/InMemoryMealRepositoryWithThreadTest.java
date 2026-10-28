package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InMemoryMealRepositoryWithThreadTest implements MealRepository {
    private final Map<Integer, Meal> mealsMap = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);
    private final Map<Integer, Set<Integer>> users = new ConcurrentHashMap<>();

    {
//        MealsUtil.meals.forEach(meal -> save(meal, meal.getUserId()));
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

    private final CountDownLatch afterGetMealIdsCollection = new CountDownLatch(1);
    private final CountDownLatch afterDeleteMealFromCollection = new CountDownLatch(1);

    @Override
    public boolean delete(int id, int userId) {
        Meal meal = mealsMap.get(id);
        if (validateNotNullAndBelongToUser(meal, userId)) {
            try {
                afterGetMealIdsCollection.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            users.get(userId).remove(meal.getId());
            Meal result = mealsMap.remove(id);
            System.out.println("Remove meal " + id + " from mealsMap");
            afterDeleteMealFromCollection.countDown();
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
        System.out.println("getAll - Get ids collection");
        users.computeIfAbsent(userId, k -> new ConcurrentSkipListSet<>());
        Set<Integer> mealIds = new TreeSet<>(users.get(userId));
        System.out.println("mealIds: " + mealIds);
        afterGetMealIdsCollection.countDown();
        System.out.println("getAll - Add meals from mealsMap to meals");
        for (Integer mealId : mealIds) {
            try {
                afterDeleteMealFromCollection.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Meal meal = mealsMap.get(mealId);
            System.out.println("getAll - Add meal " + meal);
            meals.add(meal);
        }
        System.out.println("getAll - Start loop on meals");
        return meals.stream()
                .filter(Objects::nonNull)
                .filter(meal -> DateTimeUtil.isBetweenDates(meal.getDate(), startDate, endDate))
                .sorted(Comparator.comparing(Meal::getDate).reversed())
                .collect(Collectors.toList());
    }

    public static void main(String[] args) throws InterruptedException {
        MealRepository repository = new InMemoryMealRepositoryWithThreadTest();
        for (int j = 0; j < 10; j++) {
            repository.save(new Meal(LocalDateTime.now(), String.valueOf(j), j, null), 1);
        }
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Callable<Object> getAllTask = () -> {
            //repository.getAll(1);
            repository.getAll(3);
            return null;
        };
        Callable<Object> deleteTask = () -> repository.delete(1, 1);
        try {
            List<Future<Object>> futures = executorService.invokeAll(Arrays.asList(getAllTask, deleteTask));
            for (Future<Object> future : futures) {
                try {
                    future.get();
                } catch (ExecutionException e) {
                    e.getCause().printStackTrace();
                }
            }
        } finally {
            executorService.shutdown();
        }
    }


}
