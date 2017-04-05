

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
        <div style="margin: auto; width: 1080px; height: 1080px">
            <g:img file="map.png" style="width: 1080px; height=1080px; position: absolute; z-index: 1" />

            <div id="puzzlePoints" style="width: 1080px; height: 1080px; position: absolute; z-index: 2">
            </div>
        </div>
    </div>
</body>
</html>
