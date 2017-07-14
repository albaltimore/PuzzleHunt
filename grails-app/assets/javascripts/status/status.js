/* global jQuery */
//= require jquery-3.2.0

function reloadStatuses() {
    var tableBody = $("#statusTable");
    tableBody.empty();
    //$(".statusTableRow").detach();

    $.get("getStatus", function (statusData) {
        // statusData contains a list of puzzles and a list of players.
        // each puzzle will have a list of players who have solved it, and players who have access to it.
        console.log(statusData);

        // construct the header row for teams up top.
        var header_tr = $("<tr><th>Status</th>");
        statusData.puzzles.forEach(function (puzzleName) {
          header_tr.append("<td>"+puzzleName+"</td>");
        });
        header_tr.append("</tr>");

        tableBody.append(header_tr);

        statusData.players.forEach(function (player) {
          var row_tr = $("<tr>");
          row_tr.append("<th>"+player.name+"</th>")
          // ensure we go in the same order and cover all players every time.
          statusData.puzzles.forEach(function(puzzle) {
            var style = "";
            if (player.solved.indexOf(puzzle) >= 0) {
              style = "solved";
            } else if (player.unlocked.indexOf(puzzle) >= 0) {
              style = "unlocked";
            }

            row_tr.append("<td><div id='"+style+"' /></td>");
          });
          row_tr.append("</tr>");
          tableBody.append(row_tr);
        });
    });
}

$(document).ready(function () {
    reloadStatuses();
    var intervalID = window.setInterval(reloadStatuses, 30000);
});

