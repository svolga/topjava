package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {
    public static final int MEAL_START_ID = START_SEQ + 3;
    public static final int MEAL_NOT_FOUND = START_SEQ - 1;

    public static final int USER_ID = START_SEQ;
    public static final int ADMIN_ID = START_SEQ + 1;

    public static final Meal userMeal1 = new Meal(MEAL_START_ID, LocalDateTime.of(2026, Month.JANUARY, 30, 10, 0), "Завтрак", 500);
    public static final Meal userMeal2 = new Meal(MEAL_START_ID + 1, LocalDateTime.of(2026, Month.JANUARY, 30, 13, 0), "Обед", 1000);
    public static final Meal userMeal3 = new Meal(MEAL_START_ID + 2, LocalDateTime.of(2026, Month.JANUARY, 30, 20, 0), "Ужин", 500);
    public static final Meal userMeal4 = new Meal(MEAL_START_ID + 3, LocalDateTime.of(2026, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100);
    public static final Meal userMeal5 = new Meal(MEAL_START_ID + 4, LocalDateTime.of(2026, Month.JANUARY, 31, 10, 0), "Завтрак", 1000);
    public static final Meal userMeal6 = new Meal(MEAL_START_ID + 5, LocalDateTime.of(2026, Month.JANUARY, 31, 13, 0), "Обед", 500);
    public static final Meal userMeal7 = new Meal(MEAL_START_ID + 6, LocalDateTime.of(2026, Month.JANUARY, 31, 20, 0), "Ужин", 410);
    public static final Meal adminMeal8 = new Meal(MEAL_START_ID + 7, LocalDateTime.of(2026, Month.JANUARY, 31, 21, 0), "Админ Ужин", 1500);
    public static final Meal adminMeal9 = new Meal(MEAL_START_ID + 8, LocalDateTime.of(2026, Month.JANUARY, 31, 14, 0), "Админ ланч", 510);

    public static Meal getNew() {
        return new Meal(LocalDateTime.of(2006, Month.JANUARY, 1, 10, 0),
                "NewMeal", 470);
    }

    public static Meal getUpdated() {

        Meal updated = new Meal(userMeal1);
        updated.setDescription("UpdatedUser Завтрак");
        updated.setCalories(356);

        return updated;
    }

    public static List<Meal> getSortedUserMeals(){
        return Arrays.asList(userMeal7, userMeal6, userMeal5, userMeal4, userMeal3, userMeal2, userMeal1);
    }

    public static List<Meal> getSortedAdminMeals(){
        return Arrays.asList(adminMeal8, adminMeal9);
    }

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).isEqualTo(expected);
    }
}