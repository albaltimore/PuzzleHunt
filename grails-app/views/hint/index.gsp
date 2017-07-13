

<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

    <asset:stylesheet src="hint/hint.css"/>
    <asset:javascript src="hint/hint.js"/>
    <title class="hint-table">Hint Queue</title>
</head>
<body style="background-color: black">
    <div style="position: fixed; width: 100%; height: 100%; overflow: auto">
        <div id="rootPane" style="margin: auto; width: 100%;">

            <div style="display: table; position: relative; margin: auto">


                <table id="hintsTable" class="owned-table">
                    <tr>
                        <th class="bloomberg-title-cell" ><label>Player</label></th>
                        <th class="bloomberg-title-cell"><label>Puzzle</label></th>
                        <th class="bloomberg-title-cell"><label>Question</label></th>
                        <th class="bloomberg-title-cell"><label>Owner</label></th>

                        <th class="bloomberg-title-cell"><label>Created</label></th>
                        <th class="bloomberg-title-cell"><label>Last Owner</label></th>
                    </tr>

                </table>

            </div>
        </div>
    </div>
    <div id="modal" style="visibility: hidden; position: absolute; left: 0px; right: 0px; top: 0px; bottom: 0px; z-index: 100">
        <div id="modal-shade"></div>
        <div id="modal-root"></div>
    </div>
</body>
</html>