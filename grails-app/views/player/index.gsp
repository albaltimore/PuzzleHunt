

<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

    <asset:stylesheet src="player/player.css"/>
    <asset:javascript src="player/player.js"/>
    <title>Puzzle Hunt</title>
</head>
<body style="background-color: black">
    <div style="position: fixed; width: 100%; height: 100%; overflow: auto">
        <div style="margin: auto; width: 1440px; height: 900px">
            <g:img file="r2-bg.png" style="width: 1440px; height=900px; position: absolute; z-index: 1" />

            <div id="puzzlePoints" style="width: 1440px; height: 900px; position: absolute; z-index: 2">
            </div>
        </div>
    </div>
</body>
</html>
