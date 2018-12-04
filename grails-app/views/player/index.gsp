<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>

<body>

<div>
    <div>
        Note: You cannot create a team or accept invites while part of another team.
    </div>
    <div>
        <g:if test="${player.team}">
            <g:link controller="team" action="show" id="${player.team.id}">
                <button type="button">Go to team page</button>
            </g:link>
        </g:if>
        <g:else>
            <g:link controller="team" action="newTeam">
                <button type="button">Create new team</button>
            </g:link>
        </g:else>

        <g:link controller="teamInvite" action="index">
            <button type="button">View invites</button>
        </g:link>

    </div>
</div>


</body>
