<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="shortcut icon" type="image/png" href="${assetPath(src: 'favicon.png')}"/>
    <asset:stylesheet src="player/index.css"/>
    <asset:stylesheet src="player/bootstrap.css"/>
    <asset:javascript src="player/index.js"/>
    <title>PuzzleHunt</title>
</head>

<body>
<g:form useToken="true" style="visibility: hidden">
</g:form>
<div class="root-div">
    <div id="rootPane">
        <div id="linksPane" class="greeting greeting-links"></div>

        <div id="huntPane" class="greeting greeting-hunt"></div>

        <div id="titlePane" class='greeting greeting-title'></div>

        <div id="endPane" class="greeting greeting-end"></div>

        <div id="statusPane" class="greeting greeting-status" style="display: none"></div>

        <div id="logoutPane" class="greeting greeting-logout"><g:link controller="login" action="logout"></g:link></div>
        
        <div class="notifiers">
            <div class="hint-notifier"><label></label></div>

            <div class="alert"><label></label>

                <div class="alert-window" style="display: none"></div></div>
        </div>

        <div class="greeting greeting-leaderboard leaderboard-pane"></div>

    </div>

    <footer>
        <g:link controller="login" action="logout">Logout</g:link> | <a href="https://www.bloomberg.com/notices/privacy/" target="_blank">Privacy Policy</a> | <g:link controller="login" action="terms" target="_blank">Terms Of Service</g:link>
    </footer>
</div>

<div id="modal" style="visibility: hidden">
    <div id="modal-shade"></div>

    <div id="modal-root"></div>
</div>

</body>

</html>