<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>

<body>

<div>
    <g:if test="${flash.message}">
        <label class="flash-message">${flash.message}</label>
    </g:if>

    <g:form useToken="true" action="createInvite" controller="teamInvite">
        <g:hiddenField name="teamId" value="${team.id}"/>
        <label>Player Name</label>
        <input type="text" name="playerName" placeholder="Username"/>

        <input type="submit"/>
    </g:form>

</div>


</body>
