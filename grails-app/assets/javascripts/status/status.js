/* global jQuery */
//= require jquery/jquery

const REFRESH_INTERVAL = 30000;
const BLANK_VALUE = "_";

function paintStatusForTeam(teamName)
{
  console.log("paintStatusForTeam('"+team+"')");
  $.get("getStatus", function(statusData) {

    $("#statusTable tbody").remove(); // Remove current non-header content

    var teams = statusData.teams.filter(function (team) { return team.name === teamName; });



    var tbody = $( "<tbody></tbody>" );


    if (teams.length === 1) {
      var team = teams[0];
      statusData.puzzles.forEach(function(puzzleName) {
        var style;
        if (team.unlocked.indexOf(puzzleName) >= 0) {
          style = "unlocked";
        }
        if (team.solved.indexOf(puzzleName) >= 0) {
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
     statusData.teams.forEach(function (team) {
       teamComboBox += "<option value='" + team.name + "'>" 
                  + team.name + "</option>";
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

  // Clear the grid and tell users it's loading.
  $("#statusTable tbody").remove();
  var loading_content = $( "<tbody><tr><td>Loading...</td><td></td></tr></tbody>" )
  $("#statusTable").append(loading_content);

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

        // construct the header row for team up top.
        var headerTr = $("<tr><th><b>STATUS BOARD</b></th></tr>");
        statusData.puzzles.forEach(function (puzzleName) {
            var td = $("<td>" + puzzleName + "</td>");
            headerTr.append(td);
            cols[puzzleName] = [td];
        });

        tableBody.append(headerTr);

        statusData.teams.forEach(function (team) {
            var rowTr = $("<tr>");
            rowTr.append("<th" + (team.priorityLine ? " class='priority'" : "") + ">" + team.name + "</th></tr>");
            // ensure we go in the same order and cover all players every time.
            statusData.puzzles.forEach(function (puzzle) {
                var style = "";
                if (team.solved.indexOf(puzzle) >= 0) {
                    style = "solved";
                } else if (team.unlocked.indexOf(puzzle) >= 0) {
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

