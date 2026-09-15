package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserMealsUtil {

    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }


    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> totalCaloriesPerDay = getTotalCaloriesByDateWithCycles(meals);

        List<UserMealWithExcess> mealsWithExcess = new ArrayList<>();
        meals.forEach(meal -> {
            if (TimeUtil.isBetweenHalfOpen(meal.getTime(), startTime, endTime)) {
                UserMealWithExcess userMealWithExcess = getUserMealWithExcess(meal, totalCaloriesPerDay, caloriesPerDay);
                mealsWithExcess.add(userMealWithExcess);
            }
        });

        return mealsWithExcess;
    }

    private static Map<LocalDate, Integer> getTotalCaloriesByDateWithCycles(List<UserMeal> meals) {
        Map<LocalDate, Integer> totalCaloriesPerDay = new HashMap<>();
        meals.forEach(meal -> {
            LocalDate localDate = meal.getDate();
            totalCaloriesPerDay.put(localDate, totalCaloriesPerDay.getOrDefault(localDate, 0) + meal.getCalories());
        });
        return totalCaloriesPerDay;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> totalCaloriesPerDay = getCachedTotalCaloriesByDateWithStreams(meals);

        return meals.stream().filter(meal -> TimeUtil.isBetweenHalfOpen(meal.getTime(), startTime, endTime))
                .map(meal -> getUserMealWithExcess(meal, totalCaloriesPerDay, caloriesPerDay))
                .collect(Collectors.toList());
    }

    private static UserMealWithExcess getUserMealWithExcess(UserMeal meal, Map<LocalDate, Integer> totalCaloriesPerDay, int caloriesPerDay) {
        LocalDate localDate = meal.getDate();
        boolean isExxess = totalCaloriesPerDay.getOrDefault(localDate, 0) > caloriesPerDay;
        return new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(),
                isExxess);
    }

    private static Map<LocalDate, Integer> getCachedTotalCaloriesByDateWithStreams(List<UserMeal> meals) {
        return meals.stream()
                .collect(Collectors.groupingBy(UserMeal::getDate, Collectors.summingInt(UserMeal::getCalories)));
    }

}
