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
        <title class="hint-table">Hint Queue</title>
    </head>
    <body bgcolor="#000000">
        <h1 class="bloomberg-headline">Hint Queue</h1>
        <g:form controller="hint" action="refreshlist">
            <input type="submit" value="REFRESH" class="submit-button"/>
        </g:form>
        </br>
        <table class="hint-table">
            <tr border="1">
                <td width="70" class="bloomberg-title-cell">Player</td>
                <td width="70" class="bloomberg-title-cell">Puzzle</td>
                <td width="200" class="bloomberg-title-cell">Question</td>
                <td width="70" class="bloomberg-title-cell">Owner</td>
                <td width="55" class="bloomberg-title-cell"></td>
                <td width="55" class="bloomberg-title-cell"></td>
                <td width="55" class="bloomberg-title-cell"></td>
            </tr>
            <g:each in="${list}" var="hint">
            <tr border="1">
                <td width="70" class="bloomberg-cell">${hint.player}</td>
                <td width="70" class="bloomberg-cell">${hint.puzzle}</td>
                <td width="200" class="bloomberg-cell">${hint.question}</td>
                <td width="70" class="bloomberg-cell">${hint.owner}</td>
                <td width="55" class="bloomberg-cell">
                    <g:form useToken="true" controller="hint" action="claim">
                    <g:hiddenField name="hintid" value="${hint.id}" />
                    <input type="submit" value="${hint.status}" class="claim-button"/>
                    </g:form>
                </td>
                <td width="55" class="bloomberg-cell">
                    <g:form useToken="true" controller="hint" action="toggle">
                    <g:hiddenField name="hintid" value="${hint.id}" />
                    <input type="submit" value="${hint.action}" class="claim-button"/>
                    </g:form>
                </td>
                <td width="55" class="bloomberg-cell">
                    <g:form useToken="true" controller="hint" action="details">
                    <g:hiddenField name="hintid" value="${hint.id}" />
                    <input type="submit" value="View Details" class="claim-button"/>
                    </g:form>
                </td>
            </tr>
            </g:each>
        </table>
    </body>
</html>
