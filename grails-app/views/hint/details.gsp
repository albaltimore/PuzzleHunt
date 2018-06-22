<!--
  To change this license header, choose License Headers in Project Properties.
  To change this template file, choose Tools | Templates
  and open the template in the editor.
-->
<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>

<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <asset:stylesheet src="hint/hint.css"/>
    <asset:javascript src="hint/details.js"/>
    <title class="hint-table">Hint Details</title>
</head>

<body style="background-color: black; font-family: sans-serif; font-size: 17px">

<div style="height:100%">
    <a href="index">
        <label style="color: #59A0E6; cursor: pointer; font-size: 32px; float: left; height: 100%">Go Back</label>
    </a>
</div>

<div style="margin: auto; width: 850px">
    <label style="color: white; font-size: 36px">Hint Details</label>

    <div style="height: 20px"></div>

    <div>
        <label class="details-name">Owner</label>
        <label id="ownerLabel" class="details-cell"></label>
        <label id="ownerBonusLabel"></label>
    </div>

    <div>
        <label class="details-name">Requesting Team</label>
        <label id="requestorLabel" class="details-cell"></label>
    </div>

    <div>
        <label class="details-name">Contact</label>
        <label id="contactLabel" class="details-cell"></label>
    </div>

    <div style="height: 20px"></div>

    <div>
        <label class="details-name">Puzzle Name</label>
        <label id="nameLabel" class="details-cell"></label>
    </div>

    <div>
        <a id="puzzleLink" target="_blank" style="text-decoration: none">
            <label style="color: #59A0E6; cursor: pointer">The Puzzle</label>
        </a>
        <label style="color: white">|</label>
        <a id="solutionLink" target="_blank" style="text-decoration: none">
            <label style="color: #59A0E6; cursor: pointer">Solution Instructions</label>
        </a>
    </div>


    <div style="height: 20px"></div>

    <div>
        <label class="details-name">Request Data</label>
        <label id="questionLabel"
               style="padding: 3px; display: block; white-space: pre-line; background-color: lightgray; color: black; height: 120px; overflow-y: auto; box-sizing: border-box"></label>
    </div>


    <div style="height: 20px"></div>

    <div>
        <label class="details-name" style="display: block">Notes</label>
        <textarea id="noteEntry" type="notes" class="notes-entry"></textarea>
        <label id="updateNoteLink"
               style="color: #59A0E6; cursor: pointer; margin-top: 2px; float: right">Update Note</label>
    </div>

    <div id="actionsDiv"></div>

</div>

<div id="modal"
     style="visibility: hidden; position: absolute; left: 0px; right: 0px; top: 0px; bottom: 0px; z-index: 100">
    <div id="modal-shade"></div>

    <div id="modal-root"></div>
</div>
</body>

</html>