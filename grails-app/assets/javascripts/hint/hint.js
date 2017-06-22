/* global jQuery */
//= require jquery-3.2.0

function clearHintQueue() {
    console.log('clearing hints');
    $("#hintTable tr").remove();
    var row= $('<tr border="1"> \
                    <td width="70" class="bloomberg-title-cell">Player</td> \
                    <td width="70" class="bloomberg-title-cell">Puzzle</td> \
                    <td width="200" class="bloomberg-title-cell">Question</td> \
                    <td width="90" class="bloomberg-title-cell">Owner</td> \
                    <td width="70" class="bloomberg-title-cell">Status</td> \
                    <td width="55" class="bloomberg-title-cell"></td> \
                    <td width="55" class="bloomberg-title-cell"></td> \
                </tr>');
    $("#hintTable").append(row);
}

function reloadHintQueue() {
    clearHintQueue();
    console.log('getting hints');
    $.get("getHints", function (hintData) {

        var rootPane = $("#rootPane");
        var hintTable = $("#hintTable");
        hintData.hints.forEach(function (hint) {
            var ownerClass = "owner_" + hint.id;

            var hintRow = $('<tr border="1"> \
                <td width="70" class="bloomberg-cell">' + hint.player + '</td> \
                <td width="70" class="bloomberg-cell">' + hint.puzzle + '</td> \
                <td width="200" class="bloomberg-cell">' + hint.question + '</td> \
                <td width="90" class="bloomberg-cell ' + ownerClass + '" id="' + ownerClass + '">' + hint.owner + '</td> \
                <td width="70" class="bloomberg-cell">' + hint.action + '</td> \
                <td width="55" class="bloomberg-cell"> \
                    <input id="' + hint.id + '" "type="submit" value="' + hint.status + '" class="claim-button claim"/> \
                </td> \
                <td width="55" class="bloomberg-cell"> \
                    <input id="' + hint.id + '" type="submit" value="Details" class="claim-button details"/> \
                </td> \
            </tr>');
            
            hintTable.append(hintRow);
        });

        hintTable.on("click", ".claim", function(){
            $.post("claim", {hintid: this.id}, function (response) {
                console.log("requested hint");
                this.value = response.action;
                var ownerClass = "owner_" + this.id;
                $("#" + ownerClass + "").text(response.owner);
                
            }.bind(this));
        });

        hintTable.on("click", ".toggle", function(){
            $.post("toggle", {hintid: this.id}, function (response) {
                console.log("toggled hint");
                this.value = this.value === "done" ? "re-open" : "done";
            }.bind(this));
        });

        hintTable.on("click", ".details", function(){
            console.log("showing hint details");
            window.location.href = "/hint/details?hintid=" + this.id;
        });
    });
}

$(document).ready(function () {
    var header = $("<h1 class='bloomberg-headline'>Hint Queue</h1></br>");
    $("#rootPane").append(header);
    var hintTable= $('<table id="hintTable" class="hint-table"> \
                        <tr border="1"> \
                            <td width="70" class="bloomberg-title-cell">Player</td> \
                            <td width="70" class="bloomberg-title-cell">Puzzle</td> \
                            <td width="200" class="bloomberg-title-cell">Question</td> \
                            <td width="90" class="bloomberg-title-cell">Owner</td> \
                            <td width="70" class="bloomberg-title-cell">Status</td> \
                            <td width="55" class="bloomberg-title-cell"></td> \
                            <td width="55" class="bloomberg-title-cell"></td> \
                        </tr> \
                      </table>');
    $("#rootPane").append(hintTable);

    reloadHintQueue();
    var intervalID = window.setInterval(reloadHintQueue, 30000);
});