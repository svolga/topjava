<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
<h2>${meal.id != null ? 'Edit' : 'New'} Meal</h2>

<form method="POST" action='meals' name="formMeal">

    <table>
        <tr>
            <td></td>
            <td><input type="hidden" readonly="readonly" name="id"
                       value="<c:out value="${meal.id}" />"/> <br/></td>
        </tr>

        <tr>
            <td>DateTime :</td>
            <td><input type="datetime-local" name="dateTime" value="<c:out value="${meal.dateTime}" />"/></td>
        </tr>
        <tr>
            <td>Description :</td>
            <td><input
                    type="text" name="description"
                    value="<c:out value="${meal.description}" />"/></td>
        </tr>
        <tr>
            <td>Calories :</td>
            <td><input type="number" name="calories"
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
