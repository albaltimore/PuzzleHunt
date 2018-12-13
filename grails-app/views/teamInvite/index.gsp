<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>

<body>

<div>
    <h2>My Invites</h2>
    <ul>
        <g:each var="invite" in="${teamInvites}">
            <li>
                ${invite.team.name}
                <g:link controller="teamInvite" action="acceptInvite" id="${invite.id}">
                    <button type="button">Accept</button>
                </g:link>
                <g:link controller="teamInvite" action="declineInvite" id="${invite.id}">
                    <button type="button">Decline</button>
                </g:link>
            </li>
        </g:each>
    </ul>
</div>


</body>
