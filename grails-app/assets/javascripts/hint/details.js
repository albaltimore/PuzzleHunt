/* global jQuery */
//= require packaged/jquery/jquery


function getParameterByName(name, url) {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

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

function updateNote(show) {
    console.log(show);
    $.get("updateNote", {hintId: hintId, notes: $("#noteEntry").val()}, function () {
        if (show) {
            showDialog("Success");
        } else {
            location.reload();
        }
    }).fail(function () {
        if (show) {
            showDialog("Could not update");
        } else {
            location.reload();
        }
    });
}

var hintId;
$(document).ready(function () {
    hintId = getParameterByName("hintId");

    $("#updateNoteLink").click(function () {
        updateNote(true);
    });

    $.get("getHintDetails", {hintId: hintId}, function (data) {
        console.log(data.hinterName);
        $("#ownerLabel").text(data.hinterName ? data.hinterName : "");
        if (!data.hinterName) {
            $("#ownerBonusLabel").text("(Nobody)");
            $("#ownerBonusLabel").css("color", "red");
        } else if (data.myName === data.hinterName) {
            $("#ownerBonusLabel").text("(That's You!)");
            $("#ownerBonusLabel").css("color", "green");
        }

        $("#requestorLabel").text(data.teamName);
        $("#contactLabel").text(data.contactInfo);

        $("#noteEntry").val(data.notes);

        $("#nameLabel").text(data.puzzleName);
        $("#questionLabel").text(data.question);
        $("#solutionLink").attr("href", "getResource?accessor=" + encodeURIComponent(data.solutionAccessor));
        $("#puzzleLink").attr("href", "getResource?accessor=" + encodeURIComponent(data.puzzleAccessor));

        var actionsDiv = $("#actionsDiv");

        if (data.closed) {
            var reopenBtn = $("<input type='button' class='claim-button' value='ReOpen' />");
            actionsDiv.append(reopenBtn);
            reopenBtn.click(function () {
                $.post("reopenHint", {hintId: hintId}, function () {
                    updateNote(false);
                }).fail(function () {
                    showDialog("Failed", function () {
                        location.reload();
                    });
                });
            });
        } else {
            if (!data.hinterName && !data.myHints && data.myName !== data.hinterName) {
                var claimBtn = $("<input type='button' class='claim-button' value='Claim' />");
                actionsDiv.append(claimBtn);
                claimBtn.click(function () {
                    $.post("claimHint", {hintId: hintId}, function () {
                        updateNote(false);
                    }).fail(function () {
                        showDialog("Failed", function () {
                            location.reload();
                        });
                    });
                });
            } else if (!data.myHints && data.myName !== data.hinterName) {
                var stealBtn = $("<input type='button' class='claim-button' value='Steal' />");
                actionsDiv.append(stealBtn);
                stealBtn.click(function () {
                    $.post("claimHint", {hintId: hintId, steal: true}, function () {
                        updateNote(false);
                    }).fail(function () {
                        showDialog("Failed", function () {
                            location.reload();
                        });
                    });
                });
            }

            if (data.myName === data.hinterName) {
                var unclaimBtn = $("<input type='button' class='claim-button' value='UnClaim' />");
                actionsDiv.append(unclaimBtn);
                unclaimBtn.click(function () {
                    $.post("unclaimHint", {hintId: hintId}, function () {
                        updateNote(false);
                    }).fail(function () {
                        showDialog("Failed", function () {
                            location.reload();
                        });
                    });
                });

                var closeBtn = $("<input type='button' class='claim-button' value='Close' />");
                actionsDiv.append(closeBtn);
                closeBtn.click(function () {
                    $.post("closeHint", {hintId: hintId}, function () {
                        updateNote(false);
                    }).fail(function () {
                        showDialog("Failed", function () {
                            location.reload();
                        });
                    });
                });
            }
        }

        var deleteHintBtn = $("<input type='button' class='claim-button' style='color: red' value='Delete' />");
        actionsDiv.append(deleteHintBtn);
        deleteHintBtn.click(function () {
            if (data.role === "ADMIN") {
                showConfirmDialog("Are you sure you want to delete this hint", "Yes", "Cancel", () => {
                    $.post("deleteHint", {hintId: hintId}, function () {
                        showDialog("Hint Request Deleted", () => {window.location = "/hint/index";});

                    }).fail(function () {
                        showDialog("Failed", function () {
                            location.reload();
                        });
                    });
                });
            } else {
                showDialog("You can't delete hints, but you can ask one of the following admins to do it for you!\n\n" + data.admins.join(", "));
            }

        });


        console.log(data);
    }).fail(function () {
        console.log("hifail");
        showDialog("Could not load Hint Request.", function () {
            console.log("failure");
            window.location = "index";
        });
    });

});
