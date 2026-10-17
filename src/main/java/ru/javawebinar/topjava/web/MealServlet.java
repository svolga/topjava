package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.StringUtils;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.web.meal.MealRestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MealServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(MealServlet.class);

    private ConfigurableApplicationContext applicationContext;
    private MealRestController mealRestController;

    private static final String START_DATE = "startDate";
    private static final String START_TIME = "startTime";
    private static final String END_DATE = "endDate";
    private static final String END_TIME = "endTime";
    private static final String DATE_TIME = "dateTime";
    private static final String CALORIES = "calories";
    private static final String DESCRIPTION = "description";

    @Override
    public void init() {
        applicationContext = new ClassPathXmlApplicationContext("spring/spring-app.xml");
        mealRestController = applicationContext.getBean(MealRestController.class);
        log.info("Bean definition names: {}", Arrays.toString(applicationContext.getBeanDefinitionNames()));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        Meal meal = getMealFromRequest(request);
        if (meal.isNew()) {
            log.info("Create meal: {}", meal);
            mealRestController.create(meal);
        } else {
            log.info("Update meal: {}", meal);
            mealRestController.update(meal, getId(request));
        }

        response.sendRedirect("meals");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        int currentUserId = SecurityUtil.authUserId();

        switch (action == null ? "all" : action) {
            case "delete":
                int id = getId(request);
                log.info("delete id -> {}", id);
                mealRestController.delete(id);
                response.sendRedirect("meals");
                break;
            case "create":
            case "update":
                final Meal meal = "create".equals(action) ?
                        new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000, null) :
                        mealRestController.get(getId(request));
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
                break;

            case "reset":
                log.info("reset");
                response.sendRedirect("meals");
                break;
            case "all":
            default:
                log.info("getAll");
                LocalDate startDate = getLocalDate(request, START_DATE);
                LocalTime startTime = getLocalTime(request, START_TIME);
                LocalDate endDate = getLocalDate(request, END_DATE);
                LocalTime endTime = getLocalTime(request, END_TIME);

                List<MealTo> mealTos = mealRestController.getFiltered(startDate, endDate, startTime, endTime);
                request.setAttribute("currentUserId", currentUserId);
                request.setAttribute("meals", mealTos);
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
        }
    }

    private Meal getMealFromRequest(HttpServletRequest request) {
        Integer id = request.getParameter("id").isEmpty() ? null : Integer.parseInt(request.getParameter("id"));
        LocalDateTime dateTime = LocalDateTime.parse(request.getParameter(DATE_TIME));
        String description = request.getParameter(DESCRIPTION);
        int calories = Integer.parseInt(request.getParameter(CALORIES));
        return new Meal(id, dateTime, description, calories, null);
    }

    private LocalDate getLocalDate(HttpServletRequest request, String parameter) {
        String value = request.getParameter(parameter);
        return StringUtils.hasLength(value) ? LocalDate.parse(value) : null;
    }

    private LocalTime getLocalTime(HttpServletRequest request, String parameter) {
        String value = request.getParameter(parameter);
        return StringUtils.hasLength(value) ? LocalTime.parse(value) : null;
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }

    @Override
    public void destroy() {
        applicationContext.close();
        super.destroy();
    }
}
