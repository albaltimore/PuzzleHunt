<!--
  To change this license header, choose License Headers in Project Properties.
  To change this template file, choose Tools | Templates
  and open the template in the editor.
-->

<%@ page contentType="text/html;charset=UTF-8" %>

<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <asset:javascript src="admin/admin.js"/>
    <asset:stylesheet src="hint/hint.css"/>
</head>

<body>
<label style="font-size: 24px">Rounds</label>

<div id='unlockDiv'></div>

<label style="font-size: 24px; margin-top: 40px">Player Status Bonus</label>

<div>
    <div>
        <label>Player</label>
        <select style="width: 150px" id='playerSelect'>
            <option/>
        </select>
        <label>Activity</label>
        <select style="width: 150px" id='activitySelect'>
            <option/>
        </select>
        <input id="findPlayerActivityButton" type='button' value="Find"/>
    </div>

    <div id='setPlayerActivityDiv' style='display: none'>
        <label>Points</label>
        <input id="playerActivityPointsValue" type="number" min="0" step='1'/>
        <input id='playerActivityPointsSubmit' type="button" value='Update'/>
    </div>
</div>


<div id="modal"
     style="visibility: hidden; position: absolute; left: 0px; right: 0px; top: 0px; bottom: 0px; z-index: 100">
    <div id="modal-shade"></div>

    <div id="modal-root"></div>
</div>

</body>

</html>