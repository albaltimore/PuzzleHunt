/* global jQuery */

//= require jquery/jquery

function showDialog(text, cb) {
    var modal = $("#modal");
    var modalRoot = $("#modal-root");
    modal.css("visibility", "visible");
    modalRoot.empty();

    var pane = $("<div style='position: relative; max-width: 700px; padding: 20px' />");
    var label = $("<label class='modal-label' />");
    pane.append(label);
    var close = $("<input type='button' value='close' class='modal-button-close'  style='left: 50%; transform: translate(-50%); position: relative; display: block' />");
    close.click(function () {
        pane.remove();
        modal.css("visibility", "hidden");
        if (cb) cb();
    });
    pane.append(close);

    label.text(text);
    modalRoot.append(pane);
}

function showConfirmDialog(text, yText, nText, cb) {
    var modal = $("#modal");
    var modalRoot = $("#modal-root");
    modal.css("visibility", "visible");
    modalRoot.empty();

    var pane = $("<div style='padding: 20px; 20px; 20px; 20px; max-width: 740px' />");
    var label = $("<label class='modal-label' />");
    pane.append(label);
    pane.append($("<br/>"));


    var closeDiv = $("<div style='position: relative; left: 50%; transform: translate(-50%); display: inline-block' />");
    pane.append(closeDiv);

    var close = $("<input type='button' class='modal-button-close' />");
    close.val(yText);
    close.click(function () {
        pane.remove();
        modal.css("visibility", "hidden");
        if (cb) cb();
    });
    closeDiv.append(close);

    var closeNo = $("<input type='button' class='modal-button-close' />");
    closeNo.val(nText);
    closeNo.click(function () {
        pane.remove();
        modal.css("visibility", "hidden");
    });
    closeDiv.append(closeNo);


    label.text(text);
    modalRoot.append(pane);
}

function refresh() {
    location.reload();
}


function dateString(d) {
    var td = x => x < 10 ? '0' + x : x;
    return td(d.getFullYear()) + '-' + td(d.getMonth() + 1) + '-' + td(d.getDate()) + 'T' + td(d.getHours()) + ":" + td(d.getMinutes()) + ":" + td(d.getSeconds())
}

function init() {
    $.get("getData", function (data) {
        console.log(data);
        var hunts = {};

        var huntsDiv = $("#huntsDiv");
        $("#huntSelect").append($("<option/>").val("%NEW%").text("NEW"));
        data.hunts.forEach(hunt => {
            console.log(hunt);
            $("#huntSelect").append($("<option/>").val(hunt.id).text(hunt.description));
            hunts[hunt.id] = hunt;
        });

        $("#huntSelect").change(evt => {
            var hunt = hunts[$("#huntSelect").val()];
            $("#huntKey").text(hunt ? window.location.origin + "/login/register/" + hunt.linkKey : "");
            $("#huntDescription").val(hunt ? hunt.description : "");
            $("#huntWinningText").val(hunt && hunt.winningText ? hunt.winningText : "");
            $("#huntGameoverText").val(hunt && hunt.gameoverText ? hunt.gameoverText : "");
            $("#huntStart").val(hunt && hunt.startTime ? dateString(new Date(hunt.startTime)) : "");
            $("#huntEnd").val(hunt && hunt.endTime ? dateString(new Date(hunt.endTime)) : "");
            $("#huntMaxTeamSize").val(hunt && hunt.maxTeamSize ? hunt.maxTeamSize : "");

        });

        $("#huntSave").click(evt => {

            var req = {
                id: $("#huntSelect").val() === "%NEW%" ? null : $("#huntSelect").val(),
                description: $("#huntDescription").val() || null,
                startTime: new Date($("#huntStart").val()).getTime() || null,
                endTime: new Date($("#huntEnd").val()).getTime() || null,
                maxTeamSize: parseInt($("#huntMaxTeamSize").val()) || null,
                winningText: $("#huntWinningText").val() || null,
                gameoverText: $("#huntGameoverText").val() || null
            };
            console.log(req);
            $.post("saveHunt", req, evt => {
                console.log("succsess");
                showDialog("Success", refresh);
            }).fail(function (data) {
                console.log(data);
                showDialog("Error :(", refresh);
            });

        });


        var roundsDiv = $("#unlockDiv");
        data.rounds.forEach(function (round) {
            var roundDiv = $("<div />");
            roundsDiv.append(roundDiv);
            roundDiv.append($("<label style='margin: 0 2px;'>" + round.name + "</label>"));
            roundDiv.append($("<label style='margin: 0 2px;'>" + "Status:" + "</label>"));
            roundDiv.append($("<label style='margin: 0 2px;'>" + (round.unlocked ? "Unlocked" : "Locked") + "</label>"));
            var link = $("<label style='cursor: pointer; color: #59A0E6'>" + (round.unlocked ? "Lock" : "Unlock") + "</label>");
            roundDiv.append(link);
            link.click(function () {
                showConfirmDialog("Are you sure you want to " + (round.unlocked ? "Lock" : "Unlock") + " round " + round.name, "Yes", "Cancel", function () {
                    $.post("setRoundUnlocked", {round: round.id, unlocked: !round.unlocked}, function () {
                        console.log("succsess");

                        showDialog("Success", refresh);
                    }).fail(function (data) {
                        console.log(data);
                        showDialog("Error :(", refresh);
                    });
                });
            });
        });

        var allPlayerOption = $("<option/>");
        allPlayerOption.val("ALL");
        allPlayerOption.text("All Teams");
        $("#alertPlayerSelect").append(allPlayerOption);

        data.players.forEach(function (player) {
            function addOption(widget) {
                var option = $("<option></option>");
                option.val(player.id);
                option.text(player.description);
                $(widget).append(option);
            }

            addOption("#alertPlayerSelect")
        });

        data.teams.forEach(function (team) {
            function addOption(widget) {
                var option = $("<option></option>");
                option.val(team.id);
                option.text(team.name);
                $(widget).append(option);
            }

            addOption("teamSelect");
        });

        data.activities.forEach(function (activity) {
            var option = $("<option></option>");
            option.val(activity.id);
            option.text(activity.name);
            $("#activitySelect").append(option);
        });

        var alertsDiv = $("#alertsDiv");
        Object.keys(data.alerts).forEach(k => {
            var alert = data.alerts[k];
            var alertsPane = $("<div></div>");
            alertsDiv.append(alertsPane);
            alertsPane.append($("<label>" + alert.title + "</label>"));
            alertsPane.append($("<label style='color: gray'>|</label>"));
            alertsPane.append($("<label>" + new Date(alert.targetTime).toLocaleString() + "</label>"));

            var deleteButton = $("<label style='cursor: pointer; color: #59A0E6'>delete</label>")
            alertsPane.append(deleteButton);
            deleteButton.click(function () {
                $.post("deleteAlertsByBatchId", {batchId: k}, function () {
                    console.log("succsess");
                    showDialog("Success", refresh);
                }).fail(function (data) {
                    console.log(data);
                    showDialog("Error :(", refresh);
                });
            });

        });

        if (data.start) {
            $("#startEntry").val(dateString(new Date(parseInt(data.start))));
        }
    });
}


$(document).ready(function () {
    init();

    $("#teamSelect").change(function () {
        $("#setTeamActivityDiv").css("display", "none");
    });

    $("#activitySelect").change(function () {
        $("#setTeamActivityDiv").css("display", "none");
    });

    $("#findTeamActivityButton").click(function () {
        $("#setTeamActivityDiv").css("display", "none");
        $.get("getTeamActivityPoints", {
            team: parseInt($("#teamSelect").val()),
            activity: parseInt($("#activitySelect").val())
        }, function (data) {
            $("#setTeamActivityDiv").css("display", "block");
            $("#teamActivityPointsValue").val(data.points);
            console.log(data);
        }).fail(function (err) {
            console.log(err);
            showDialog("No Data Available");
        });
    });
    $("#teamActivityPointsSubmit").click(function () {
        $.post("setTeamActivityPoints", {
            player: parseInt($("#team").val()),
            activity: parseInt($("#activitySelect").val()),
            points: $("#teamActivityPointsValue").val()
        }, function (data) {
            console.log("succsess");
            showDialog("Success", refresh);
        }).fail(function (data) {
            console.log(data);
            showDialog("Error :(", refresh);
        });
    });

    $("#startSubmit").click(function () {
        $.post("setProperty", {name: "START", value: new Date($("#startEntry").val()).getTime()}, function () {
            console.log("succsess");
            showDialog("Success", refresh);
        }).fail(function () {
            console.log(data);
            showDialog("Error :(", refresh);
        });
    });

    $("#alertSubmit").click(function () {
        if (!$("#alertTitle").val() || !$("#alertTargetTime").val() || !$("#alertPlayerSelect").val()) showDialog("You have to fill in all the fields.");
        var player = $("#alertPlayerSelect").val();

        $.post('createAlert', {
            player: player === 'ALL' ? 'ALL' : parseInt(player),
            title: $("#alertTitle").val(),
            message: $("#alertMessage").val(),
            targetTime: new Date($("#alertTargetTime").val()).getTime(),
            leadTime: parseInt($("#alertLeadTime").val())
        }, function () {

            console.log("succsess");
            showDialog("Success", refresh);
        }).fail(function () {
            console.log(data);
            showDialog("Error :(", refresh);
        });
    });
});
