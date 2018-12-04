<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>

<body>

<div>
    <h1>${team.name}</h1>
    <h2>Members</h2>
    <ul>
        <g:each var="member" in="${team.members}">
            <li>${member.name}</li>
        </g:each>
    </ul>

    <g:link controller="teamInvite" action="newInvite" params="[teamId: team.id]">
        <button type="button">Invite another player</button>
    </g:link>

    <g:if test="${team.isFinalized}">
        <g:link controller="team" action="unfinalizeTeam" id="${team.id}">
            <button type="button">Unfinalize Team</button>
        </g:link>
    </g:if>
    <g:else>
        <g:link controller="team" action="finalizeTeam" id="${team.id}">
            <button type="button">Finalize Team</button>
        </g:link>
    </g:else>

    <g:link controller="team" action="leaveTeam" id="${team.id}">
        <button type="button">Leave Team</button>
    </g:link>
</div>


</body>
