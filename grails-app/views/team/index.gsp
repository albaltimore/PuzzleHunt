<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="shortcut icon" type="image/png" href="${assetPath(src: 'favicon.png')}"/>
    <asset:stylesheet src="teams/index.css"/>
    <asset:stylesheet src="teams/bootstrap.css"/>
    <asset:javascript src="teams/index.js"/>
    <title>PuzzleHunt</title>
</head>

<body>
<g:form useToken="true" style="visibility: hidden">
</g:form>
<div class="root-div">
    <div class="huntContainer"></div>

    <div class="puzzle-timer"></div>

    <div class="greeting-container">
        <label>Welcome</label>
        <label class="player-name"></label>
    </div>

    <div class="team-status">
        <label class="team-status-label">You are not on a team</label>
        <label class="team-status-team"></label>
        <label class="team-create puzzle-button">Create Team</label>

        <div class="team-members-container">
            <label>Members</label>
            <span class="team-members"></span>
        </div>

        <div class="team-invite-container">
            <label>Invite Key</label>
            <label class="team-invite-key emp"></label>
        </div>

        <div class="team-status-public"></div>

    </div>

    <div class="team-finalize-container">
        <label class="team-finalize-label"></label>
        <label class="team-finalize puzzle-button">Finalize Team</label>
    </div>

    <div><label class="team-leave puzzle-button">Leave Team</label></div>

    <div class="team-join">
        <label>Request to join</label>
        <input type="text"  placeholder="Paste invite key here" class="team-join-key"/>
        <label class="puzzle-button team-join-submit">Go</label>
    </div>

    <div class="team-request-container">
        <label>Requests to join</label>
        <div class="team-request-list"></div>
    </div>

    <div class="team-list-container">
        <label>Public Teams</label>

        <div class="team-list"></div>
    </div>
</div>

<div id="modal">
    <div id="modal-shade"></div>

    <div id="modal-root"></div>
</div>
</body>

</html>