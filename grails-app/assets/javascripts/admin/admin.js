/* global jQuery */
//= require packaged/jquery/jquery

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

function init() {
    $.get("getData", function (data) {
        console.log(data);

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

        data.players.forEach(function (player) {
            var option = $("<option></option>");
            option.val(player.id);
            option.text(player.description);
            $("#playerSelect").append(option);
        });

        data.activities.forEach(function (activity) {
            var option = $("<option></option>");
            option.val(activity.id);
            option.text(activity.name);
            $("#activitySelect").append(option);
        });
    });
}


$(document).ready(function () {
    init();

    $("#playerSelect").change(function () {
        $("#setPlayerActivityDiv").css("display", "none");
    });

    $("#activitySelect").change(function () {
        $("#setPlayerActivityDiv").css("display", "none");
    });

    $("#findPlayerActivityButton").click(function () {
        $("#setPlayerActivityDiv").css("display", "none");
        $.get("getPlayerActivityPoints", {
            player: parseInt($("#playerSelect").val()),
            activity: parseInt($("#activitySelect").val())
        }, function (data) {
            $("#setPlayerActivityDiv").css("display", "block");
            $("#playerActivityPointsValue").val(data.points);
            console.log(data);
        }).fail(function (err) {
            console.log(err);
            showDialog("No Data Available");
        });
    });
    $("#playerActivityPointsSubmit").click(function () {
        $.post("setPlayerActivityPoints", {
            player: parseInt($("#playerSelect").val()),
            activity: parseInt($("#activitySelect").val()),
            points: $("#playerActivityPointsValue").val()
        }, function (data) {
            console.log("succsess");
            showDialog("Success", refresh);
        }).fail(function (data) {
            console.log(data);
            showDialog("Error :(", refresh);
        });
    });
});