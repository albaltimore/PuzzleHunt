/* global jQuery */
//= require jquery-3.2.0

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

function showLoading(text) {
    var modal = $("#modal");
    var modalRoot = $("#modal-root");
    modal.css("visibility", "visible");
    modalRoot.empty();

    var pane = $("<div style='position:absolute; width:100%; height 100%' />");
    var label = $("<label class='modal-label' style='top: 20px; left:70px; color:orange' />");
    pane.append(label);

    label.text(text ? text : "Loading...");
    modalRoot.append(pane);
}

function closeLoading() {
    var modal = $("#modal");
    var modalRoot = $("#modal-root");
    modalRoot.empty();
    modal.css("visibility", "hidden");
}

function showHintDialog(puzzleId, puzzleName, hintText) {
    $.get("nextHintTime", function (nextHintData) {
        var modal = $("#modal");
        var modalRoot = $("#modal-root");
        modal.css("visibility", "visible");
        //modalRoot.css("height", "500px");
        //modalRoot.css("width", "500px");
        modalRoot.empty();

        var pane = $("<div style='padding: 20px; 20px; 20px; 20px; max-width: 600px; position: relative' />");

        modalRoot.append(pane);

        pane.append($("<label style='color: white; display: block; font-size: 24px;'>To request your next hint, please enter as much detailed information as possible about what you have tried so far.</label>"));

        pane.append($("<label style='color: white; display: inline-block; font-size: 22px; margin-top: 20px;'>Puzzle:&nbsp;</label><label style='color: lightgreen; display: inline-block; font-size: 22px' >" + puzzleName + "<label/>"));

        var availableLabel = $("<label style='color: white; display: block; font-size: 24px; margin-top: 20px;'></label>");
        pane.append(availableLabel);

        var hintEntry = $("<textarea maxlength='1150' style='resize: none; font-size: 16px; width: 100%; margin-top: 20px; height: 150px; box-sizing: border-box' />");
        pane.append(hintEntry);
        hintEntry.text(hintText ? hintText : "");


        pane.append($("<label style='color: white; display: block; font-size: 24px; margin-top: 35px'>Tell us how to reach you! Either provide a phone number, or your room number so we can call you via Nexi.</label>"));
        var contactEntry = $("<textarea maxlength='250' style='resize: none; font-size: 16px; width: 100%; margin-top: 20px; height: 40px; box-sizing: border-box' />");
        pane.append(contactEntry);
        if (contactInfo) {
            contactEntry.val(contactInfo);
        }

        var timer = setInterval(function () {

            var left = Math.max(0, nextHintData.time - new Date().getTime()) / 1000;
            if (nextHintData.left) {
                if (nextHintData.max <= 1) {
                    availableLabel.text("A hint is available now...");
                } else {
                    availableLabel.text(nextHintData.left + " out of " + nextHintData.max + " hints are available.");
                }
            } else {
                var pad = function (t) {
                    return t < 10 ? '0' + parseInt(t) : parseInt(t);
                };
                availableLabel.text("No hints left. Next hint in: " + pad((left / 3600) % 60) + ":" + pad((left / 60) % 60) + ":" + pad(left % 60));
            }

        }, 25);

        var closeDiv = $("<div style='position: relative; left: 50%; transform: translate(-50%); display: inline-block' />");
        pane.append(closeDiv);

        var close = $("<input type='button' value='Request Hint' class='modal-button-close' />");
        close.css("left", "35px");
        close.css("top", "430px");
        close.click(function () {
            var hintRequest = hintEntry.val();
            if (contactEntry.val()) {
                contactInfo = contactEntry.val();
            }
            if (!hintRequest || !contactInfo) return;

            $.post("requestHint", {id: puzzleId, question: hintRequest, contactInfo: contactInfo}, function (data) {
                clearInterval(timer);
                pane.remove();
                modal.css("visibility", "hidden");

                showDialog(data.success ? "Submitted. Someone will help shortly!" : data.error, data.success ? null : function () {
                    showHintDialog(puzzleId, puzzleName, hintRequest);
                });

            }).fail(function () {
                showDialog("Something went wrong! :(");
            });
        });
        closeDiv.append(close);

        var closeNo = $("<input type='button' value='Cancel' class='modal-button-close' />");
        closeNo.css("left", "285px");
        closeNo.css("top", "430px");
        closeNo.click(function () {
            clearInterval(timer);
            pane.remove();
            modal.css("visibility", "hidden");
        });
        closeDiv.append(closeNo);
    });
}


var removePanes = [];
var removeTimers = [];
function clearPanes() {
    console.log("clearing", removePanes);
    removePanes.forEach(function (pane) {
        pane.remove();
    });
    removePanes = [];

    removeTimers.forEach(function (timer) {
        clearInterval(timer);
    });
    removeTimers = [];
}


var puzzlePoints = [];
function clearPoints() {
    clearPanes();
    puzzlePoints.forEach(function (puzzlePoint) {
        if (puzzlePoint) {
            puzzlePoint.remove();
        }
    });
    puzzlePoints = [];
}

var rounds = {};
var selectedRound;
var playerStatus;
var contactInfo;

function reloadMap(openPuzzleId) {
    clearPanes();
    $.get("getPuzzles", function (playerData) {
        console.log(playerData);
        var rootPane = $("#rootPane");
        var pMap = {};


        function selectRound(roundId) {
            selectedRound = roundId;
            Object.keys(rounds).forEach(function (key) {
                rounds[key].pane.css("display", key === selectedRound ? "block" : "none");
            });
        }

        var titleDiv = $("#titlePane");
        titleDiv.css("display", "inline-block");
        titleDiv.empty();

        contactInfo = playerData.contactInfo;

        playerStatus = playerData.status;
        var statusPane = $("#statusPane");
        statusPane.empty();
        if (!playerData.status) {
            statusPane.css("display", "none");
        } else {
            statusPane.css("display", "inline-block");
            statusPane.append($("<img src='getResource?accessor=" + playerStatus.resource + "' style='height: 70px; display: block; margin: auto'/>"));
            statusPane.append($("<label style='font-size: 22px; font-family: sans-serif; display: block; text-align: center; cursor: pointer'>" + playerStatus.name + "</label>"));
        }

        playerData.rounds.forEach(function (round) {
            if (!selectedRound) {
                selectedRound = round.id;
            }
            if (rounds[round.id]) {
                return;
            }
            rounds[round.id] = round;
            var paneDiv = $("<div style='margin: auto; width: " + round.width + "px ;height: " + round.height + "px; border: 10px ridge gold'>");
            rootPane.append(paneDiv);
            var img = $("<img src=getResource?accessor=" + round.background + " style='position: absolute; z-index: 1'>");
            paneDiv.append(img);
            rounds[round.id].pane = paneDiv;

            var puzzlePoints = $("<div style='position: absolute; z-index: 2; width: " + round.width + "px; height: " + round.height + "px'>");
            paneDiv.append(puzzlePoints);
            rounds[round.id].pointsDiv = puzzlePoints;

            if (round.id !== selectedRound) {
                paneDiv.css("display", "none");
            }
        });

        if (playerData.rounds.length > 1) {
            var desLabel = $("<label>Welcome. Please choose a floor: </label>");
            titleDiv.append(desLabel);
            var links = [];
            Object.keys(rounds).sort(function (a, b) {
                return rounds[a].floorId - rounds[b].floorId;
            }).forEach(function (key) {
                var link = $("<label style='cursor: pointer; color: #59A0E6'></label>");
                link.text("Floor " + rounds[key].floorId);
                links.push(link);
                links.push($("<div style='height: 1em; width: 3px; background-color: gold; display: inline-block; margin: 0 10px'>|</div>"));
                link.click(function () {
                    selectRound(key);
                });
            });
            links.pop();
            var linkDiv = $("<div style='margin: auto; display: table'></div>");
            titleDiv.append(linkDiv);
            links.forEach(function (link) {
                linkDiv.append(link);
            });
        } else {
            var desLabel = $("<label>Welcome to The Lexington Hotel</label>");
            titleDiv.append(desLabel);
        }

        playerData.puzzles.forEach(function (puzzle) {
            pMap[puzzle.id] = puzzle;
        });

        console.log(rounds);
        playerData.puzzles.forEach(function (puzzle) {
            pMap[puzzle.id] = puzzle;
            puzzle.requiredPuzzles.filter(function (rp) {
                return pMap[rp.id];
            }).forEach(function (rp) {
                if (puzzle.roundId !== pMap[rp.id].roundId) {
                    console.log("nolinks", puzzle, rp);
                    return;
                }

                var points = [puzzle].concat(rp.points).concat([pMap[rp.id]]);

                var thickness = 10;
                var color = rp.color ? rp.color : "black";
                for (var i = 0; i < points.length - 1; i++) {
                    var a = points[i], b = points[i + 1];
                    var x1 = a.xCor, y1 = a.yCor, x2 = b.xCor, y2 = b.yCor;
                    var angle = Math.atan2((y1 - y2), (x1 - x2)) * (180 / Math.PI);
                    var length = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));

                    var l = $("<div  style='position: absolute; background-color: " + color + "'>");
                    l.css("transform", "rotate(" + angle + "deg)");
                    l.css("-ms-transform", "rotate(" + angle + "deg)");
                    l.css("-webkit-transform", "rotate(" + angle + "deg)");
                    l.css("height", thickness + "px");
                    l.css("width", length + "px");
                    l.css("top", ((y1 + y2) / 2 - thickness / 2) + "px");
                    l.css("left", ((x1 + x2) / 2 - length / 2) + "px");
                    rounds[puzzle.roundId].pointsDiv.append(l);
                    if (i + 2 < points.length) {
                        var c = $("<div style='position: absolute; width: " + (thickness) + "px; height: " + (thickness) + "px; background-color: " + color + "; border-radius: 50%' />");
                        c.css("top", (b.yCor - thickness / 2) + "px");
                        c.css("left", (b.xCor - thickness / 2) + "px");
                        rounds[puzzle.roundId].pointsDiv.append(c);
                    }
                }
            });
        });

        playerData.puzzles.forEach(function (puzzle) {
            var point = $("<div class='puzzlePoint' />");
            puzzlePoints.push(point);
            point.css("top", (puzzle.yCor - 15) + "px");
            point.css("left", (puzzle.xCor - 15) + "px");

            var solveable = false;

            if (puzzle.solved) {
                point.css("background-color", "green");
            } else if (!puzzle.requiredPuzzles.length || puzzle.requiredPuzzles.some(function (rp) {
                return pMap[rp.id] && pMap[rp.id].solved;
            })) {
                point.css("background-color", "yellow");
                solveable = true;
            }

            rounds[puzzle.roundId].pointsDiv.append(point);

            point.click(function (evt) {
                clearPanes();
                var pane = $("<div style='position: absolute; background-color: black; width: 300px; border: 1px solid white; padding: 5px 5px 5px 5px; z-index: 100' />");
                rounds[puzzle.roundId].pointsDiv.append(pane);

                var rw = rounds[puzzle.roundId].width;
                var rh = rounds[puzzle.roundId].height;
                pane.css(puzzle.yCor < (rh / 2) ? "top" : "bottom", (puzzle.yCor < (rh / 2) ? puzzle.yCor : rh - puzzle.yCor) + "px");
                pane.css(puzzle.xCor < (rw / 2) ? "left" : "right", (puzzle.xCor < (rw / 2) ? puzzle.xCor : rw - puzzle.xCor) + "px");
                removePanes.push(pane);
                pane.click(function (evt) {
                    evt.stopPropagation();
                });
                evt.stopPropagation();

                var timers = [];
                removeTimers.push(setInterval(function () {
                    timers.forEach(function (timer) {
                        var left = Math.max(0, timer.end - new Date().getTime()) / 1000;
                        if (left <= 0) {
                            timer.widget.text("Time Expired");
                            timer.widget.css("color", "red");
                        } else {
                            var pad = function (t) {
                                return t < 10 ? '0' + parseInt(t) : parseInt(t);
                            };

                            timer.widget.text("Time left:  " + pad((left / 3600) % 60) + ":" + pad((left / 60) % 60) + ":" + pad(left % 60));
                        }
                    });
                }, 25));

                console.log("puzzle", puzzle.solved);
                if (puzzle.solved) {
                    console.log("solved", puzzle);
                    var label = $("<label style='position: relative; color:green; font-size: 20px; text-align: center; top: 4px'></label>");
                    label.text(puzzle.name);
                    pane.append(label);

                    if (puzzle.solvedAccessor) {
                        var accessorUrl = "getResource?accessor=" + puzzle.solvedAccessor;
                        var introExtension = puzzle.solvedFilename ? puzzle.solvedFilename.substr(puzzle.introFilename.lastIndexOf(".") + 1).toLowerCase() : "link";

                        if (introExtension === "pdf") {
                            var body = $("<object data='" + accessorUrl + "' style='width:100%; margin-top: 10px; margin-bottom: 3px; max-height: 200px; overflow-y: auto'/>");
                            var link = $("<div style='margin-bottom: 10px'><a style='color: #59A0E6' target=\"_blank\" href=\"" + accessorUrl + "\">Download</a></div>");
                            pane.append(body);
                            pane.append(link);
                        } else if (introExtension === "mp4") {
                            var body = $("<video style='width:100%; margin-top: 10px; margin-bottom: 3px; max-height: 200px; overflow-y: auto' controls><source src='" + accessorUrl + "' type='video/mp4' /></video>");
                            var link = $("<div style='margin-bottom: 10px'><a style='color: #59A0E6' target=\"_blank\" href=\"" + accessorUrl + "\">Download</a></div>");
                            pane.append(body);
                            pane.append(link);
                        } else if (introExtension === "link") {
                            var link = $("<div style='padding: 30px; margin: auto; display: table'><a style='color: #59A0E6; font-size: 36px' target=\"_blank\" href=\"" + accessorUrl + "\"><img src='" + asset_url('youtube.png') + "' width='120' height='120' /></a></div>");
                            pane.append(link);
                        } else {
                            body = $("<a target=\"_blank\" href=\"" + accessorUrl + "\"><img src=\"" + accessorUrl + "\" style='width:100%; margin-top: 10px; margin-bottom: 10px; max-height: 200px; overflow-y: auto'/></a>");
                            pane.append(body);
                        }
                    } else {
                        var label = $("<label style='width:100%; color:green; font-size: 64px; text-align: center; display: inline-block'>SOLVED</label>");
                        pane.append(label);
                    }

                    link = $("<div style='margin-bottom: 10px'><a style='color: #59A0E6' target=\"_blank\" href=\"" + "getResource?accessor=" + puzzle.introAccessor + "\">Click here for Puzzle</a></div>");
                    pane.append(link);
                } else if (solveable) {
                    var label = $("<label style='position: relative; color:yellow; font-size: 20px; text-align: center; top: 4px'></label>");
                    label.text(puzzle.name);
                    pane.append(label);

                    if (puzzle.started) {
                        if (puzzle.timeLimit) {
                            var endTime = puzzle.timeLimit * 1000 + puzzle.startTime;
                            console.log(new Date().getTime(), endTime);

                            var ends = $("<div style='margin-top: 5px'></div>");
                            var endLab = $("<label style='color: white; font-size: 18px; margin: auto; width: 100%'>Time Limit Expired!</label>");
                            ends.append(endLab);
                            timers.push({widget: endLab, end: endTime});

                            pane.append(ends);
                        }

                        var accessorUrl = "getResource?accessor=" + puzzle.introAccessor;
                        var introExtension = puzzle.introFilename ? puzzle.introFilename.substr(puzzle.introFilename.lastIndexOf(".") + 1).toLowerCase() : "link";

                        if (introExtension === "pdf") {
                            var body = $("<object data='" + accessorUrl + "' style='width:100%; margin-top: 10px; margin-bottom: 3px; max-height: 200px; overflow-y: auto'/>");
                            var link = $("<div style='margin-bottom: 10px'><a style='color: #59A0E6' target=\"_blank\" href=\"" + accessorUrl + "\">Download</a></div>");
                            pane.append(body);
                            pane.append(link);
                        } else if (introExtension === "mp4") {
                            var body = $("<video style='width:100%; margin-top: 10px; margin-bottom: 3px; max-height: 200px; overflow-y: auto' controls><source src='" + accessorUrl + "' type='video/mp4' /></video>");
                            var link = $("<div style='margin-bottom: 10px'><a style='color: #59A0E6' target=\"_blank\" href=\"" + accessorUrl + "\">Download</a></div>");
                            pane.append(body);
                            pane.append(link);
                        } else if (introExtension === "link") {
                            var link = $("<div style='padding: 30px; margin: auto; display: table'><a style='color: #59A0E6; font-size: 36px' target=\"_blank\" href=\"" + accessorUrl + "\"><img src='" + asset_url('youtube.png') + "' width='120' height='120' /></a></div>");
                            pane.append(link);
                        } else {
                            body = $("<a target=\"_blank\" href=\"" + accessorUrl + "\"><img src=\"" + accessorUrl + "\" style='width:100%; margin-top: 10px; margin-bottom: 10px; max-height: 200px; overflow-y: auto'/></a>");
                            pane.append(body);
                        }

                        pane.append($("<span style='margin-right: 15px'><label style='color: white; font-size: 14px'>Solve</label></span>"));
                        var solveEntry = $("<input type='text' style='width: 245px' placeholder='Type the solution then press <Enter>' />");
                        pane.append(solveEntry);

                        var statusDiv = $("<div style='position: relative; height: 20px; margin-top: 10px; margin-bottom: 5px; overflow-y: auto; background-color: #444444'/>");
                        pane.append(statusDiv);

                        var statusLabel = $("<label style='color: white;  display: inline-block'>Awaiting input...</label>");
                        statusDiv.append(statusLabel);

                        solveEntry.keydown(function (evt) {
                            if (evt.key === "Enter") {
                                if (!solveEntry.val().length) {
                                    statusLabel.text("Awaiting input...");
                                    return;
                                }

                                statusLabel.text("Checking..");
                                $.post("checkPuzzle", {id: puzzle.id, solution: solveEntry.val()}, function (data) {
                                    console.log(data);
                                    if (data.solved) {
                                        reloadMap(puzzle.id);
                                    } else {
                                        statusLabel.text(data.message);
                                    }
                                }).fail(function (err) {
                                    console.log(err);
                                    statusLabel.text("Probably Incorrect");
                                });
                            }
                        });
                        if (!puzzle.hintDisabled) {
                            var hintDiv = $("<div style='position: relative;  margin-top: 10px; margin-bottom: 5px; margin-right: 8px'>");
                            pane.append(hintDiv);
                            var hintLink = $("<label style='color: #59A0E6; text-decoration: underline; cursor: pointer; text-align: end; display: block'>Need a hint?</label>");
                            hintDiv.append(hintLink);
                            hintLink.click(function () {
                                showHintDialog(puzzle.id, puzzle.name);
                            });
                        }
                    } else {
                        var label = $("<label style='position: relative; color:white; font-size: 16px; text-align: center'></label>");
                        label.text("This is a timed puzzle. You have " + (puzzle.timeLimit / 60) + " minutes to solve it. You should gather your whole team!");
                        var labDiv = $("<div style='position: relative; margin-top: 10px'>");
                        labDiv.append(label);
                        pane.append(labDiv);

                        var startBtn = $("<label style='cursor: pointer; color: white; margin-top: 10px; color: lightgreen'>Ok, start!</label>");
                        pane.append(startBtn);
                        startBtn.click(function () {
                            showConfirmDialog("Are you sure? You'll have " + (puzzle.timeLimit / 60) + " minutes to solve it. Gather your whole team!", "Start!", "Cancel", function () {
                                $.post("startTimedPuzzle", {id: puzzle.id}, function (data) {
                                    reloadMap(puzzle.id);
                                });
                            });
                        });
                    }

                } else {
                    var label = $("<label style='width:100%; color:red; font-size: 64px; text-align: center; display: inline-block'>LOCKED</label>");
                    pane.append(label);
                }
            });
            if (openPuzzleId === puzzle.id) {
                console.log('hi');
                point.click();
            }
        });
    });
}



$(document).ready(function () {
    $(document).click(clearPanes);
    $("#modal").click(function (event) {
        event.stopPropagation();
    });
    reloadMap();

    $("#statusPane").click(function () {
        if (playerStatus) {
            showDialog("You have achieved " + playerStatus.name + " hotel status!\n\n" +
                (playerStatus.priorityLine ? "You may now use the priority line.\n" : "") +
                (playerStatus.hintTime ? "Your hint timer is decreased by " + playerStatus.hintTime + " seconds.\n" : "") +
                (playerStatus.hintCount ? "You may now store up to " + (playerStatus.hintCount + 1) + " hints.\n" : "") +
                (playerStatus.puzzleTime ? "Your speed puzzle timer is increased by " + playerStatus.puzzleTime + " seconds.\n" : ""));
        }
    });

    setInterval(function () {
        if (!removePanes.length) {
            reloadMap();
        }
    }, 1000 * 60 * 5);
});

