<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="meal" scope="request" type="ru.javawebinar.topjava.model.Meal"/>
<html>
<head>
    <title>Meal</title>

    <style>

        td {
            padding-left: 20px; /* Adds 20 pixels of space to the left side */
        }
    </style>

</head>
<body>

<h3><a href="index.html">Home</a></h3>
<hr>

<h2>${meal.id >0  ? 'Edit' : 'New'} Meal</h2>

<form method="POST" action='meals' name="formMeal">

    <table>
        <tr>
            <td></td>
            <td><input type="hidden" readonly="readonly" name="id"
                       value="<c:out value="${meal.id}" />"/> <br/></td>
        </tr>

        <tr>
            <td><label for="dateTime">DateTime :</label></td>
            <td><input type="datetime-local" id="dateTime" name="dateTime" value="<c:out value="${meal.dateTime}" />"/>
            </td>
        </tr>

        <tr>
            <td><label for="description">Description :</label></td>
            <td><input
                    type="text" id="description" name="description"
                    value="<c:out value="${meal.description}" />"/></td>
        </tr>
        <tr>
            <td><label for="calories">Calories :</label></td>
            <td><input type="number" id="calories" name="calories"
                       value="<c:out value="${meal.calories}" />"/></td>
        </tr>
        <tr>
            <td><input type="submit" value="Save"/>
                <button onclick="window.history.back()" type="button">Cancel</button>
            </td>
            <td></td>
        </tr>
    </table>
</form>
</body>
</html>
