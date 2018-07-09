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

    var pane = $("<div style='padding: 20px; max-width: 740px' />");
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
        hintNext = nextHintData.time;

        var modal = $("#modal");
        var modalRoot = $("#modal-root");
        modal.css("visibility", "visible");
        //modalRoot.css("height", "500px");
        //modalRoot.css("width", "500px");
        modalRoot.empty();

        var pane = $("<div style='padding: 20px; max-width: 600px; position: relative' />");

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

        var hasRunOnce = false;
        var close = $("<input type='button' value='Request Hint' class='modal-button-close' />");
        close.css("left", "35px");
        close.css("top", "430px");
        close.click(function () {
            var hintRequest = hintEntry.val();
            if (contactEntry.val()) {
                contactInfo = contactEntry.val();
            }
            if (!hintRequest || !contactInfo) return;

            if (hasRunOnce) {
                return;
            }
            hasRunOnce = true;

            $.post("requestHint", {id: puzzleId, question: hintRequest, contactInfo: contactInfo}, function (data) {
                clearInterval(timer);
                pane.remove();
                modal.css("visibility", "hidden");

                showDialog(data.success ? "Submitted. Someone will help shortly!" : data.error, data.success ? null : function () {
                    showHintDialog(puzzleId, puzzleName, hintRequest);
                });

            }).fail(function () {
                showDialog("Something went wrong! :(");
            }).always(function () {
                $.get("nextHintTime", function (nextHintData) {
                    hintNext = nextHintData.time;
                });
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


var alertsMap = {};
var alertRefreshInterval;
var alertNext;

function updateAlerts() {
    console.log("update alerts");
    alertNext = null;
    var alertLabel$ = $(".alert");
    var alertWindow$ = $(".alert-window");

    alertLabel$.removeClass("alert-pending alert-available");

    $.get('getAlerts', function (alerts) {
        console.log(alerts);
        if (alerts && alerts.length) {
            alerts.forEach(alert => {
                if (alertsMap[alert.id]) {
                    alertsMap[alert.id].isAcknowledged = alert.isAcknowledged;
                } else {
                    alertsMap[alert.id] = alert;
                }
            });
        }
    }).fail(function (e) {
        console.log(e);
    }).always(function () {
        if (!alertNext) alertNext = setTimeout(updateAlerts, 66000);
    });

    if (!alertRefreshInterval) {
        $(document).click(function () {
            alertWindow$.css('display', 'none');
        });


        $(".alert > label").click(function (evt) {
            evt.stopPropagation();
            alertWindow$.css('display', alertWindow$.css('display') === 'block' ? 'none' : 'block');
        });

        alertWindow$.click(evt => evt.stopPropagation());

        alertRefreshInterval = setInterval(() => {
            var now = Date.now();
            alertWindow$.empty();
            var alertPending = false;
            var alertAvailable = false;
            var createdAny = false;
            alertLabel$.removeClass("alert-pending alert-available");
            Object.keys(alertsMap).map(k => alertsMap[k]).sort((a, b) => {
                if (a.isAcknowledged !== b.isAcknowledged) return Number(a.isAcknowledged) - Number(b.isAcknowledged);
                if (a.targetTime !== b.targetTime) {
                    if (a.isAcknowledged) return b.targetTime - a.targetTime;
                    return a.targetTime - b.targetTime;
                }
                return b.id - a.id;
            }).forEach(function (alert) {
                var alertDiv = $("<div class='alert-item' />");
                alertWindow$.append(alertDiv);

                var alertTitle = $("<label class='alert-title' />");
                alertTitle.text(alert.title);
                alertDiv.append(alertTitle);

                var alertTime = $("<label class='alert-timestamp' />");
                alertTime.text(new Date(alert.targetTime).toLocaleString());
                alertDiv.append(alertTime);

                var alertMessage = $("<label class='alert-message' />");
                alertMessage.text(alert.message);
                alertDiv.append(alertMessage);

                if (!alert.isAcknowledged) {
                    alertDiv.addClass("alert-item-available");
                    var alertAck = $("<label class='alert-acknowledge' />");
                    alertDiv.append(alertAck);
                    alertAvailable = true;
                    alertAck.click(() => {
                        $.post("acknowledgeAlert", {id: alert.id}, function (alertData) {
                            console.log(alertData);
                            alertAck.remove();

                        }).fail(console.log).always(updateAlerts)
                    });
                    alertAvailable = true;
                } else if (alert.targetTime > now) {
                    alertDiv.addClass('alert-item-pending');
                    alertPending = true;
                } else {
                    alertDiv.addClass('alert-item-past');
                }
                createdAny = true;
            });
            if (alertAvailable) {
                alertLabel$.addClass("alert-available");
            } else if (alertPending) {
                alertLabel$.addClass("alert-pending")
            }
        }, 2500);
    }
}


var removePanes = [];
var removeTimers = [];
var paneMaximized = false;

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
    console.log("clearing points", puzzlePoints);
    clearPanes();
    puzzlePoints.forEach(function (puzzlePoint) {
        console.log('remove point', puzzlePoint);
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
    clearPoints();
    $.get("getPuzzles", function (playerData) {
        console.log('player data', playerData);
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
            statusPane.append($("<img src='getResource?accessor=" + playerStatus.resource + "'/>"));
            statusPane.append($("<label >" + playerStatus.name + "</label>"));
        }

        playerData.rounds.forEach(function (round) {
            console.log(round.id, parseInt(window.location.hash.substr(1)));
            if (!selectedRound && round.id === parseInt(window.location.hash.substr(1))) {
                selectedRound = round.id;
            }
            if (rounds[round.id]) {
                return;
            }
            rounds[round.id] = round;
            var paneDiv = $("<div class='map-root' style='margin: auto; width: " + round.width + "px ;height: " + round.height + "px'>");
            paneDiv.addClass("map-root-round-" + round.floorId);
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


        if (!selectedRound) {
            selectRound("" + playerData.rounds[0].id);
        }

        if (playerData.rounds.length > 1) {
            var desLabel = $("<label class='greeting-title-multimap' />");
            titleDiv.append(desLabel);
            var links = [];
            Object.keys(rounds).sort(function (a, b) {
                return rounds[a].floorId - rounds[b].floorId;
            }).forEach(function (key) {
                var link = $("<a class='greeting-title-multimap-map' href='#" + rounds[key].floorId + "' ></a>");
                link.text(rounds[key].name);
                links.push(link);
                link.click(function () {
                    selectRound(key);
                });
            });
            var linkDiv = $("<div style='margin: auto; display: table'></div>");
            titleDiv.append(linkDiv);
            links.forEach(function (link) {
                linkDiv.append(link);
            });
        } else {
            desLabel = $("<label class='greeting-title-onemap' />");
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

                if (rp.pathResource) {
                    rp.pathResource.forEach(function (pr) {
                        var img = $("<img src='" + "getResource?accessor=" + pr.resource + "' />");
                        img.css({top: pr.yCor + "px", left: pr.xCor + "px", position: 'absolute'});
                        rounds[puzzle.roundId].pointsDiv.append(img);
                    });
                } else {
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
                }
            });
        });

        playerData.puzzles.forEach(function (puzzle) {
            if (puzzle.iconAccessor) {
                var pointDiv = false;
                var point = $("<img class='puzzle-point puzzle-point-image' src='" + "getResource?accessor=" + puzzle.iconAccessor + "' style='position: absolute; cursor: pointer; transform: translate(-50%, -50%)' />");
            } else {
                pointDiv = true;
                point = $("<div class='puzzle-point puzzle-point-div' />");
            }
            point.addClass("round-" + puzzle.roundId);
            puzzlePoints.push(point);
            point.css("top", puzzle.yCor + "px");
            point.css("left", puzzle.xCor + "px");

            var solveable = false;

            if (puzzle.solved) {
                point.addClass('puzzle-point-solved');
                //if (pointDiv) point.css("background-color", "green");
            } else if (!puzzle.requiredPuzzles.length || puzzle.requiredPuzzles.some(function (rp) {
                return pMap[rp.id] && pMap[rp.id].solved;
            })) {
                point.addClass('puzzle-point-available');
                solveable = true;
            }

            rounds[puzzle.roundId].pointsDiv.append(point);

            point.click(function (evt) {
                clearPanes();
                var pane = $("<div class='puzzle-pane'/>");
                rounds[puzzle.roundId].pointsDiv.append(pane);

                var rw = rounds[puzzle.roundId].width;
                var rh = rounds[puzzle.roundId].height;

                var setCoords = doClear => {
                    pane.css(puzzle.yCor < (rh / 2) ? "top" : "bottom", doClear ? "" : ((puzzle.yCor < (rh / 2) ? puzzle.yCor : rh - puzzle.yCor) + "px"));
                    pane.css(puzzle.xCor < (rw / 2) ? "left" : "right", doClear ? "" : ((puzzle.xCor < (rw / 2) ? puzzle.xCor : rw - puzzle.xCor) + "px"));
                };

                setCoords();
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

                var maximizeButton = $("<div class='puzzle-pane-maximize'/>");
                pane.append(maximizeButton);
                maximizeButton.click(() => {
                    pane.toggleClass('puzzle-pane-maximized');
                    paneMaximized = pane.hasClass('puzzle-pane-maximized');
                    setCoords(paneMaximized);
                });


                console.log('pane maximized', paneMaximized);
                if (paneMaximized) {
                    maximizeButton.click();
                }

                console.log("puzzle", puzzle.solved);
                if (puzzle.solved) {
                    console.log("solved", puzzle);
                    var label = $("<label class='puzzle-pane-title' style='color: green' ></label>");
                    label.text(puzzle.name);
                    pane.append(label);

                    if (puzzle.solvedAccessor) {
                        var accessorUrl = "getResource?accessor=" + puzzle.solvedAccessor;
                        var introExtension = puzzle.solvedFilename ? puzzle.solvedFilename.substr(puzzle.solvedFilename.lastIndexOf(".") + 1).toLowerCase() : "link";

                        if (introExtension === "pdf") {
                            var body = $("<object data='" + accessorUrl + "#view=FitH' class='puzzle-pane-content'/>");
                            var link = $("<div style='margin-bottom: 10px'><a style='color: #59A0E6' target=\"_blank\" href=\"" + accessorUrl + "\">Download</a></div>");
                            pane.append(body);
                            pane.append(link);
                        } else if (introExtension === "mp4") {
                            var body = $("<video class='puzzle-pane-content' controls><source src='" + accessorUrl + "' type='video/mp4' /></video>");
                            var link = $("<div style='margin-bottom: 10px'><a style='color: #59A0E6' target=\"_blank\" href=\"" + accessorUrl + "\">Download</a></div>");
                            pane.append(body);
                            pane.append(link);
                        } else if (introExtension === "link") {
                            var link = $("<iframe class='puzzle-pane-content' src='" + accessorUrl + "' frameborder='0' allow='autoplay; encrypted-media' allowfullscreen />");
                            pane.append(link);
                        } else {
                            body = $("<a target=\"_blank\" href=\"" + accessorUrl + "\"  class='puzzle-pane-content-img' ><img src=\"" + accessorUrl + "\" class='puzzle-pane-content'/></a>");
                            pane.append(body);
                            body = $("<a target=\"_blank\" href=\"" + accessorUrl + "\"  class='puzzle-pane-content-background puzzle-pane-content'  style='background-image: url(\"" + accessorUrl + "\") ' /></a>");
                            pane.append(body);
                        }
                    } else {
                        var label = $("<label style='width:100%; color:green; font-size: 64px; text-align: center; display: inline-block'>SOLVED</label>");
                        pane.append(label);
                    }

                    link = $("<div style='margin-bottom: 10px'><a style='color: #59A0E6' target=\"_blank\" href=\"" + "getResource?accessor=" + puzzle.introAccessor + "\">The Puzzle</a></div>");
                    pane.append(link);
                } else if (solveable) {
                    var label = $("<label class='puzzle-pane-title' style='color:yellow;'></label>");
                    label.text(puzzle.name);
                    pane.append(label);

                    if (puzzle.started) {
                        if (puzzle.timeLimit) {
                            var endTime = puzzle.timeLimit * 1000 + puzzle.startTime;
                            console.log(new Date().getTime(), endTime);

                            var ends = $("<div style='margin-top: 5px'></div>");
                            var endLab = $("<label style='color: white; margin: auto; width: 100%'>Time Limit Expired!</label>");
                            ends.append(endLab);
                            timers.push({widget: endLab, end: endTime});

                            pane.append(ends);
                        }

                        var accessorUrl = "getResource?accessor=" + puzzle.introAccessor;
                        var introExtension = puzzle.introFilename ? puzzle.introFilename.substr(puzzle.introFilename.lastIndexOf(".") + 1).toLowerCase() : "link";

                        if (introExtension === "pdf") {
                            var body = $("<object data='" + accessorUrl + "#view=FitH' class='puzzle-pane-content'/>");
                            var link = $("<div style='margin-bottom: 10px'><a style='color: #59A0E6' target=\"_blank\" href=\"" + accessorUrl + "\">Download</a></div>");
                            pane.append(body);
                            pane.append(link);
                        } else if (introExtension === "mp4") {
                            var body = $("<video class='puzzle-pane-content' controls><source src='" + accessorUrl + "' type='video/mp4' /></video>");
                            var link = $("<div style='margin-bottom: 10px'><a style='color: #59A0E6' target=\"_blank\" href=\"" + accessorUrl + "\">Download</a></div>");
                            pane.append(body);
                            pane.append(link);
                        } else if (introExtension === "link") {
                            var link = $("<iframe class='puzzle-pane-content' src='" + accessorUrl + "' frameborder='0' allow='autoplay; encrypted-media' allowfullscreen />");
                            pane.append(link);
                        } else {
                            body = $("<a target=\"_blank\" href=\"" + accessorUrl + "\" class='puzzle-pane-content-img'><img src=\"" + accessorUrl + "\" class='puzzle-pane-content'/></a>");
                            pane.append(body);
                            body = $("<a target=\"_blank\" href=\"" + accessorUrl + "\" class='puzzle-pane-content puzzle-pane-content-background'  style='background-image: url(\"" + accessorUrl + "\") ' /></a>");
                            pane.append(body);
                        }


                        var solveDiv = $("<div style='flex: 0 0 auto; display: flex; margin: 2px 0'  />")
                        pane.append(solveDiv);

                        solveDiv.append($("<span style='margin-right: 15px; flex 0 0 auto'><label style='color: white'>Solve</label></span>"));
                        var solveEntry = $("<input class='puzzle-pane-solve' type='text' placeholder='Type the solution then press <Enter>' />");
                        solveDiv.append(solveEntry);

                        var statusDiv = $("<div style='flex: 0 0 auto; margin-top: 10px; margin-bottom: 5px; overflow-y: auto; background-color: #444444; padding: 2px 4px'/>");
                        pane.append(statusDiv);

                        var statusLabel = $("<label style='color: white; line-height: 20px; display: inline-block'>Awaiting input...</label>");
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
                            var hintDiv = $("<div style='position: relative;  margin-top: 5px; margin-bottom: 5px; margin-right: 8px'>");
                            pane.append(hintDiv);
                            var hintLink = $("<label style='color: #59A0E6; text-decoration: underline; cursor: pointer; text-align: end; display: block; flex 0 0 auto'>Need a hint?</label>");
                            hintDiv.append(hintLink);
                            hintLink.click(function () {
                                showHintDialog(puzzle.id, puzzle.name);
                            });
                        }
                    } else {
                        var label = $("<label style='position: relative; color:white; text-align: center'></label>");
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


var hintTimer;
var hintNext;
var hintLastAvailable;

$(document).ready(function () {
    $(document).click(() => {
        paneMaximized = false;
        clearPanes();
    });
    $("#modal").click(function (event) {
        event.stopPropagation();
    });
    reloadMap();
    updateAlerts();

    var hintWidget = $(".hint-notifier");
    var hintLabel = hintWidget.find("label");

    $.get("nextHintTime", function (nextHintData) {
        hintNext = nextHintData.time;

        hintTimer = setInterval(function () {
            var nowTime = Date.now();
            if (hintNext < nowTime) {
                if (!hintLastAvailable) {
                    hintLastAvailable = true;
                    hintWidget.addClass('hint-notifier-available');
                    hintLabel.text("");
                }
            } else {
                var lpad = x => (x < 10 ? '0' : '') + parseInt(x);
                var nextTime = hintNext - nowTime;
                hintLabel.text(lpad(nextTime / 60 / 1000) + ":" + lpad((nextTime / 1000) % 60));
                if (hintLastAvailable) {
                    hintWidget.removeClass('hint-notifier-available');
                    hintLastAvailable = false;
                }
            }

        }, 25);
    });

    $.get('getInstructions', function (instructions) {
        if (!instructions) return;
        var pane = $(".greeting-links");
        console.log(instructions);
        instructions.forEach(instruction => {
            var link = $("<a class='greeting-links-item' href='getResource?accessor=" + instruction.resource + "' target='_blank' />");
            link.text(instruction.name);
            pane.append(link);
        });
    });

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

