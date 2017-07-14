/* global jQuery */
//= require jquery-3.2.0

function reloadStatuses() {
    var tableBody = $("#statusTable");
    tableBody.empty();
    //$(".statusTableRow").detach();


    $.get("getStatus", function (statusData) {

        var cols = {};
        // statusData contains lists of puzzles & players
        // puzzles is a flat list of strings; players each contain a name,
        //  status, solved puzzle names, & accessed puzzle names.
        console.log(statusData);

        // construct the header row for teams up top.
        var headerTr = $("<tr><th><b>STATUS BOARD</b></th></tr>");
        statusData.puzzles.forEach(function (puzzleName) {
            headerTr.append("<td>" + puzzleName + "</td>");
            cols[puzzleName] = [];
        });

        tableBody.append(headerTr);

        statusData.players.forEach(function (player) {
            var rowTr = $("<tr>");
            rowTr.append("<th" + (player.priorityLine ? " class='priority'" : "") + ">" + player.name + "</th></tr>");
            // ensure we go in the same order and cover all players every time.
            statusData.puzzles.forEach(function (puzzle) {
                var style = "";
                if (player.solved.indexOf(puzzle) >= 0) {
                    style = "solved";
                } else if (player.unlocked.indexOf(puzzle) >= 0) {
                    style = "unlocked";
                }
                var td = $("<td><div class='" + style + "' /></td>");
                cols[puzzle].push(td);

                td.mouseenter(function () {
                    cols[puzzle].forEach(function (it) {
                        it.css("background-color", "rgba(255,255,255,.2)");
                    });
                });
                td.mouseleave(function () {
                    cols[puzzle].forEach(function (it) {
                        it.css("background-color", "");
                    });

                });


                rowTr.append(td);
            });

            rowTr.mouseenter(function () {
                rowTr.css("background-color", "rgba(255,255,255,.2)");
            });

            rowTr.mouseleave(function () {
                rowTr.css("background-color", "");
            });

            tableBody.append(rowTr);
        });
    });
}

$(document).ready(function () {
    reloadStatuses();
    var intervalID = window.setInterval(reloadStatuses, 30000);
});

