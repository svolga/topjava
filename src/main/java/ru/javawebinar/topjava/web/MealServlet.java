package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.service.MealServiceImpl;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final int LIMIT_CALORIES_PER_DAY = 2000;
    private static final Logger log = getLogger(MealServlet.class);

    private static final String PAGE_LIST = "/meals.jsp";
    private static final String PAGE_NEW_EDIT = "/meal.jsp";
    private MealService mealService;

    @Override
    public void init() throws ServletException {
        mealService = new MealServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String forward;
        String action = request.getParameter("action");
        action = action == null ? "" : action;
        log.debug("redirect to meals, action -> {}", action);

        switch (action) {
            case "delete":
                int deletedId = Integer.parseInt(request.getParameter("id"));
                mealService.delete(deletedId);
                response.sendRedirect("meals");
                return;
            case "new":
                forward = PAGE_NEW_EDIT;
                Meal newMeal = new Meal(null, LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), null, 0);
                request.setAttribute("meal", newMeal);
                break;
            case "edit":
                forward = PAGE_NEW_EDIT;
                int editedId = Integer.parseInt(request.getParameter("id"));
                Meal meal = mealService.get(editedId);
                request.setAttribute("meal", meal);
                break;
            default:
                forward = PAGE_LIST;
                List<Meal> meals = mealService.getAll();
                List<MealTo> mealsTo = MealsUtil.filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), LIMIT_CALORIES_PER_DAY);
                request.setAttribute("meals", mealsTo);
        }
        RequestDispatcher view = request.getRequestDispatcher(forward);
        view.forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String paramId = request.getParameter("id");
        Integer id = paramId.isEmpty() ? null : Integer.parseInt(paramId);
        LocalDateTime dateTime = LocalDateTime.parse(request.getParameter("dateTime"));
        String description = request.getParameter("description");
        int calories = Integer.parseInt(request.getParameter("calories"));

        Meal meal = new Meal(id, dateTime, description, calories);
        if (meal.getId() == null) {
            mealService.add(meal);
        } else {
            mealService.update(meal);
        }

        response.sendRedirect("meals");
    }
}
