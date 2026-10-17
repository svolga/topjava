package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkIsNew;

@Controller
public class MealRestController {

    Logger log = LoggerFactory.getLogger(getClass());

    private final MealService service;

    public MealRestController(MealService service) {
        this.service = service;
    }

    public Meal create(Meal meal) {
        int currentUserId = SecurityUtil.authUserId();
        log.info("create meal for user -> {}", currentUserId);
        checkIsNew(meal);
        return service.save(meal, currentUserId);
    }

    public Meal update(Meal meal, int id) {
        int currentUserId = SecurityUtil.authUserId();
        log.info("update meal for user -> {}", currentUserId);
        meal.setUserId(currentUserId);
        assureIdConsistent(meal, id);
        return service.save(meal, SecurityUtil.authUserId());
    }

    public Meal get(int id) {
        log.info("get meal for user -> {}", SecurityUtil.authUserId());
        return service.get(id, SecurityUtil.authUserId());
    }

    public void delete(int id) {
        int currentUserId = SecurityUtil.authUserId();
        log.info("delete currentUserId -> {}, meals -> {}", currentUserId, id);
        service.delete(id, currentUserId);
    }

    public List<MealTo> getAll() {
        int currentUserId = SecurityUtil.authUserId();
        List<Meal> meals = service.getAll(currentUserId);
        log.info("currentUserId -> {}, meals -> {}", currentUserId, meals);
        return MealsUtil.getTos(meals, MealsUtil.DEFAULT_CALORIES_PER_DAY);
    }

    public List<MealTo> getFiltered(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        int currentUserId = SecurityUtil.authUserId();
        List<Meal> meals = service.getFilteredByDates(startDate, endDate, currentUserId);
        log.info("Filtered by dates: startDate->{}, endDate->{}, currentUserId->{}, meals -> {}", startDate, endDate,
                currentUserId, meals);
        List<MealTo> mealTos = MealsUtil.getFilteredTos(meals, MealsUtil.DEFAULT_CALORIES_PER_DAY, startTime, endTime);
        log.info("Filtered by times: startTime -> {}, endTime -> {}, mealTos -> {}", startTime, endTime, mealTos);
        return mealTos;
    }


}