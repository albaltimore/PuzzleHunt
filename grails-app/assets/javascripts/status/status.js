/* global jQuery */
//= require jquery-3.2.0

function reloadStatuses() {
    var tableBody = $("#statusTable");
    tableBody.empty();
    //$(".statusTableRow").detach();

    $.get("getStatus", function (statusData) {
        // statusData contains lists of puzzles & players
        // puzzles is a flat list of strings; players each contain a name,
        //  status, solved puzzle names, & accessed puzzle names.
        console.log(statusData);

        // construct the header row for teams up top.
        var header_tr = $("<tr><th><b>STATUS BOARD</b></th>");
        statusData.puzzles.forEach(function (puzzleName) {
          header_tr.append("<td>"+puzzleName+"</td>");
        });
        header_tr.append("</tr>");

        tableBody.append(header_tr);

        statusData.players.forEach(function (player) {
          var row_tr = $("<tr>");
          row_tr.append("<th"+ (player.priorityLine ? " class='priority'" : "") +">"+player.name+"</th>")
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

