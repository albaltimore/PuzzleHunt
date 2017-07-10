/* global jQuery */
//= require jquery-3.2.0

function clearHintQueue() {
    console.log('clearing hints');
    $("#ownedTable tr").remove();
    $("#hintTable tr").remove();
    var row= $('<tr border="1"> \
                    <td width="70" class="bloomberg-title-cell">Player</td> \
                    <td width="70" class="bloomberg-title-cell">Puzzle</td> \
                    <td width="200" class="bloomberg-title-cell">Question</td> \
                    <td width="90" class="bloomberg-title-cell">Owner</td> \\n\
                    <td width="90" class="bloomberg-title-cell">Last Owner</td> \
                    <td width="70" class="bloomberg-title-cell">Status</td> \
                    <td width="55" class="bloomberg-title-cell"></td> \
                </tr>');
    var urgentrow= $('<tr border="1"> \
                    <td width="70" class="bloomberg-title-owned">Player</td> \
                    <td width="70" class="bloomberg-title-owned">Puzzle</td> \
                    <td width="200" class="bloomberg-title-owned">Question</td> \
                    <td width="90" class="bloomberg-title-owned">Owner</td> \\n\
                    <td width="90" class="bloomberg-title-owned">Last Owner</td> \
                    <td width="70" class="bloomberg-title-owned">Status</td> \
                    <td width="55" class="bloomberg-title-owned"></td> \
                </tr>');
    $("#hintTable").append(row);
    $("#ownedTable").append(urgentrow);
}

function reloadHintQueue() {
    clearHintQueue();
    console.log('getting hints');
    $.get("getHints", function (hintData) {

        var hintTable = $("#hintTable");
        var ownedTable = $("#ownedTable");
        hintData.hints.forEach(function (hint) {
            var ownerClass = "owner_" + hint.id;

            var hintRow = $('<tr border="1"> \
                <td width="70" class="bloomberg-cell">' + hint.player + '</td> \
                <td width="70" class="bloomberg-cell">' + hint.puzzle + '</td> \
                <td width="200" class="bloomberg-cell">' + hint.question + '</td> \
                <td width="90" class="bloomberg-cell ' + ownerClass + '" id="' + ownerClass + '">' + hint.owner + '</td> \\n\
                <td width="90" class="bloomberg-cell">' + hint.lastOwner + '</td> \
                <td width="70" class="bloomberg-cell">' + hint.action + '</td> \
                <td width="55" class="bloomberg-cell"> \
                    <button id="' + hint.id + '" type="submit" class="claim-button details">Details</button> \
                </td> \
            </tr>');
            
            hintTable.append(hintRow);
        });
        
        hintData.owned.forEach(function (hint) {
            var ownerClass = "owner_" + hint.id;

            var hintRow = $('<tr border="1"> \
                <td width="70" class="bloomberg-cell-owned">' + hint.player + '</td> \
                <td width="70" class="bloomberg-cell-owned">' + hint.puzzle + '</td> \
                <td width="200" class="bloomberg-cell-owned">' + hint.question + '</td> \
                <td width="90" class="bloomberg-cell-owned ' + ownerClass + '" id="' + ownerClass + '">' + hint.owner + '</td> \\n\
                <td width="90" class="bloomberg-cell-owned">' + hint.lastOwner + '</td> \
                <td width="70" class="bloomberg-cell-owned">' + hint.action + '</td> \
                <td width="55" class="bloomberg-cell"> \
                    <button id="' + hint.id + '" type="submit" class="claim-button details">Details</button> \
                </td> \
            </tr>');
            
            ownedTable.append(hintRow);
        });

        hintTable.on("click", ".details", function(){
            console.log("showing hint details");
            window.location.href = "/hint/details?hintid=" + this.id;
        });

        ownedTable.on("click", ".details", function(){
            console.log("showing hint details");
            window.location.href = "/hint/details?hintid=" + this.id;
        });
    });
}

$(document).ready(function () {
    var header = $("<h1 class='bloomberg-headline'>Hint Queue</h1></br>");
    $("#rootPane").append(header);
    var hintTable= $('<table id="hintTable" class="hint-table"> \
                      </table>');
    var ownedTable = $('<table id="ownedTable" class="owned-table"> \
                      </table>');
    var buffer = $('</br></br></br></br>');
    var status = $('<label id="statusLabel" class="error-cell"></label></br></br></br>');
    
    $("#rootPane").append(status);
    $("#rootPane").append(ownedTable);
    $("#rootPane").append(buffer);
    $("#rootPane").append(hintTable);

    reloadHintQueue();
    var intervalID = window.setInterval(reloadHintQueue, 30000);
});