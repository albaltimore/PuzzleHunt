<!--
  To change this license header, choose License Headers in Project Properties.
  To change this template file, choose Tools | Templates
  and open the template in the editor.
-->

<%@ page contentType="text/html;charset=UTF-8" %>
<!doctype html>
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="shortcut icon" type="image/png" href="${assetPath(src: 'favicon.png')}"/>
    <asset:javascript src="admin/admin.js"/>
    <asset:stylesheet src="hint/hint.css"/>
</head>

<body>
<h3>Rounds</h3>

<div id='unlockDiv'></div>

<h3>Player Status Bonus</h3>

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

<h3>Start Time</h3>

<div id="startTimeDiv">
    <label>Start Time:</label>
    <input type="datetime-local" id="startEntry"/>
    <input id='startSubmit' type="button" value='Update'/>
</div>

<h3>Create Alert</h3>

<div>
    <label>Target Time</label><input type="datetime-local" id="alertTargetTime"/>
    <label>Lead Time (Seconds)</label><input type="number" min="0" step="60" id="alertLeadTime" value="300"  />
    <label>Player</label><select style="width: 150px" id='alertPlayerSelect'><option/></select>
    <br/>
    <input type="text" id="alertTitle" style="width: 200px" placeholder="Alert title here..."/>
    <br/>
    <textarea id="alertMessage" style="width: 200px" placeholder="Alert body here..."></textarea>
    <br/>
    <input id='alertSubmit' type="button" value='Create'/>
</div>

<div id="modal" style="visibility: hidden; position: absolute; left: 0; right: 0; top: 0; bottom: 0; z-index: 100">
    <div id="modal-shade"></div>

    <div id="modal-root"></div>
</div>

</body>

</html>