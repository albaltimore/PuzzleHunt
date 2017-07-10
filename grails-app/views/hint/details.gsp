<!--
  To change this license header, choose License Headers in Project Properties.
  To change this template file, choose Tools | Templates
  and open the template in the editor.
-->

<%@ page contentType="text/html;charset=UTF-8" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <asset:stylesheet src="hint/hint.css"/>
        <asset:stylesheet src="login/login.css"/>
        <title class="hint-table">Hint Details</title>
    </head>
    <body bgcolor="#000000">
        <h1 class="bloomberg-headline">Hint Details</h1>
        <g:form useToken="true" controller="hint">
        <input type="submit" value="Back" class="claim-button"/>
        </g:form>
        <label class="status-cell">${notice}</label><br/><br/>
        <label class="bloomberg-cell">Owner:</label>
        <label class="details-cell">${hinterName}</label><br/><br/>
        <label class="bloomberg-cell">Player:</label>
        <label class="details-cell">${playerName}</label><br/><br/>
        <label class="bloomberg-cell">Phone #:</label>
        <label class="details-cell">${phone}</label><br/><br/>
        <label class="bloomberg-cell">Nexi #:</label>
        <label class="details-cell">${nexi}</label><br/><br/>
        <label class="bloomberg-cell">Puzzle:</label>
        <label class="details-cell">${puzzleName}</label><br/><br/>
        <label class="bloomberg-cell">Question:</label>
        <label class="details-cell">${question}</label><br/><br/>
        <label class="bloomberg-cell">Puzzle Link:</label>
        <label class="details-cell">${puzzleLink}</label><br/><br/>
        <label class="bloomberg-cell">Puzzle Solution:</label>
        <label class="details-cell">${solution}</label><br/><br/>
        <label class="bloomberg-cell">Notes:</label><br/>
        <g:form useToken="true">
        <g:hiddenField name="hintid" value="${hintid}" />
        <textarea type="notes" name="entrynotes" class="notes-entry">${notes}</textarea><br/>
        <g:actionSubmit action="claimDetail" value="Claim" width="100" class="update-button"/>
        <g:actionSubmit action="updateNote" value="Update Note" width="100" class="update-button"/>
        <g:actionSubmit action="toggle" value="${action}" width="100" class="update-button"/>
        </g:form>
    </body>
</html>
