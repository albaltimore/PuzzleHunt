/* global jQuery */
//= require packaged/jquery/jquery

const REFRESH_INTERVAL = 30000;
const BLANK_VALUE = "_";

function paintStatusForTeam(team)
{
  console.log("paintStatusForTeam('"+team+"')");
  $.get("getStatus", function(statusData) {

    $("#statusTable tbody").remove(); // Remove all non-header rows.

    var players = statusData.players.filter(function (player) { return player.login === team; });

    var tbody = $( "<tbody></tbody>" );


    if (players.length === 1) {
      var player = players[0];
      statusData.puzzles.forEach(function(puzzleName) {
        var style;
        if (player.unlocked.indexOf(puzzleName) >= 0) {
          style = "unlocked";
        }
        if (player.solved.indexOf(puzzleName) >= 0) {
          style = "solved";
        }
        if (!style) { return; }
        var row = $( "<tr><td><div class='" + style + "' /></td><td>" + puzzleName + "</td></tr>");
        console.log("append row for "+puzzleName + " with style=" + style );
        tbody.append(row);
      });
    }

    $("#statusTable").append(tbody);
  });
}

function initTeamComboBox()
{
  if (initTeamComboBox.initialized) { return; }

  initTeamComboBox.initialized = true;
  $.get("getStatus", function(statusData) {
     var id = "\"teamComboBox\"";
     teamComboBox = "<select id=" + id + " onchange='onTeamChange("+id+")'>";
     teamComboBox += "<option value='"+BLANK_VALUE+"'> -- Select a Team -- </option>";
     statusData.players.forEach(function (player) {
       teamComboBox += "<option value='" + player.login + "'>" 
                  + player.name + "</option>";
     });
     teamComboBox += "</select>";
     var headerTr = $("<thead><th>"+teamComboBox+"</th><th>Team &rarr; Puzzle Status</th></thead>");
     $("#statusTable").append(headerTr);
     $("#statusTable").addClass("oneTeamView");
  });
}

function onTeamChange(element_id)
{
  console.log("onTeamChange("+element_id+")");
  if (onTeamChange.lastTimeout) {
    window.clearTimeout(onTeamChange.lastTimeout);
  }
  var selected_val = document.getElementById(element_id).value;
  reloadStatuses(selected_val);
  if (selected_val !== BLANK_VALUE)  {
    onTeamChange.lastTimeout = window.setTimeout(
      function() { 
        $("#"+element_id).val(BLANK_VALUE); 
        onTeamChange(element_id); 
      }, REFRESH_INTERVAL*4);
  }
}

function reloadStatuses(team) {
    // If a particular team has been provided, or we have a past team
    //  we've loaded for, carry on doing that.
    if (team) { reloadStatuses.team = team; }
    if (reloadStatuses.team) {
      paintStatusForTeam(reloadStatuses.team);
      return;
    }

    $.get("getStatus", function (statusData) {

        // If we're not supposed to show all puzzles, instead initialize a
        //  Team Selection Combo Box, and bail. initTeamComboBox is idempotent.
        if (!statusData.showAll) { return initTeamComboBox(); }

        var tableBody = $("#statusTable");
        tableBody.empty();

        var cols = {};
        // statusData contains lists of puzzles & players
        // puzzles is a flat list of strings; players each contain a name,
        //  status, solved puzzle names, & accessed puzzle names.
        console.log(statusData);

        // construct the header row for teams up top.
        var headerTr = $("<tr><th><b>STATUS BOARD</b></th></tr>");
        statusData.puzzles.forEach(function (puzzleName) {
            var td = $("<td>" + puzzleName + "</td>");
            headerTr.append(td);
            cols[puzzleName] = [td];
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
    window.setInterval(reloadStatuses, REFRESH_INTERVAL);
});

