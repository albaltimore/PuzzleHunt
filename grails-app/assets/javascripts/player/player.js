/* global jQuery */
//= require jquery-3.2.0



$(document).ready(function () {
    $.get("getPuzzles", function (puzzles) {
        console.log(puzzles);
        var pMap = {};
        puzzles.forEach(function (puzzle) {
            pMap[puzzle.id] = puzzle;
        });
        puzzles.forEach(function (puzzle) {
            pMap[puzzle.id] = puzzle;
            puzzle.requiredPuzzles.filter(function (rp) {
                return pMap[rp];
            }).forEach(function (rp) {
                var a = puzzle, b = pMap[rp];
                var x1 = a.xCor, y1 = a.yCor, x2 = b.xCor, y2 = b.yCor;
                var angle = Math.atan2((y1 - y2), (x1 - x2)) * (180 / Math.PI);
                var length = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
                var thickness = 10;

                var l = $("<div  style='position: absolute; background-color: black'>");

                l.css("transform", "rotate(" + angle + "deg)");
                l.css("-ms-transform", "rotate(" + angle + "deg)");
                l.css("-webkit-transform", "rotate(" + angle + "deg)");

                l.css("height", thickness + "px");
                l.css("width", length + "px");

                l.css("top", ((y1 + y2) / 2) + "px");
                l.css("left", ((x1 + x2) / 2 - length / 2) + "px");

                $("#puzzlePoints").append(l);
            });
        });

        function clearPanes() {
            console.log("clearing", removePanes);
            removePanes.forEach(function (pane) {
                pane.remove();
            });
            removePanes = [];
        }

        var removePanes = [];
        $(document).click(clearPanes);

        puzzles.forEach(function (puzzle) {
            var point = $("<div class='puzzlePoint' />");
            point.css("top", (puzzle.yCor - 15) + "px");
            point.css("left", (puzzle.xCor - 15) + "px");

            var solveable = false;

            if (puzzle.solved) {
                point.css("background-color", "green");
            } else if (!puzzle.requiredPuzzles.length || puzzle.requiredPuzzles.some(function (rp) {
                return pMap[rp] && pMap[rp].solved;
            })) {
                point.css("background-color", "yellow");
                solveable = true;
            }

            $("#puzzlePoints").append(point);

            point.click(function (evt) {
                clearPanes();
                var pane = $("<div style='position: absolute; background-color: black; width: 300px; border: 1px solid white; padding: 5px 5px 5px 5px; z-index: 100' />");
                $("#puzzlePoints").append(pane);

                pane.css(puzzle.yCor < 450 ? "top" : "bottom", (puzzle.yCor < 450 ? puzzle.yCor : 900 - puzzle.yCor) + "px");
                pane.css(puzzle.xCor < 720 ? "left" : "right", (puzzle.xCor < 720 ? puzzle.xCor : 1440 - puzzle.xCor) + "px");
                removePanes.push(pane);
                pane.click(function (evt) {
                    evt.stopPropagation();
                });
                evt.stopPropagation();

                if (puzzle.solved) {
                    var label = $("<label style='position: relative; color:green; font-size: 16px; text-align: center; top: 4px'></label>");
                    label.text(puzzle.name);
                    pane.append(label);

                    if (puzzle.solvedAccessor) {
                        var accessorUrl = "getResource?accessor=" + puzzle.solvedAccessor;
                        var introExtension = puzzle.solvedFilename.substr(puzzle.introFilename.lastIndexOf(".") + 1).toLowerCase();

                        if (introExtension === "pdf") {
                            var body = $("<object data='" + accessorUrl + "' style='width:100%; margin-top: 10px; margin-bottom: 3px; max-height: 200px; overflow-y: auto'/>");
                            var link = $("<div style='margin-bottom: 10px'><a style='color: #59A0E6' target=\"_blank\" href=\"" + accessorUrl + "\">Download</a></div>");
                            pane.append(body);
                            pane.append(link);
                        } else {
                            body = $("<a target=\"_blank\" href=\"" + accessorUrl + "\"><img src=\"" + accessorUrl + "\" style='width:100%; margin-top: 10px; margin-bottom: 10px; max-height: 200px; overflow-y: auto'/></a>");
                            pane.append(body);
                        }
                    } else {
                        var label = $("<label style='width:100%; color:green; font-size: 64px; text-align: center; display: inline-block'>SOLVED</label>");
                        pane.append(label);
                    }

                    link = $("<div style='margin-bottom: 10px'><a style='color: #59A0E6' target=\"_blank\" href=\"" + "getResource?accessor=" + puzzle.introAccessor + "\">The Puzzle</a></div>");
                    pane.append(link);
                } else if (solveable) {
                    var label = $("<label style='position: relative; color:yellow; font-size: 16px; text-align: center; top: 4px'></label>");
                    label.text(puzzle.name);
                    pane.append(label);

                    var accessorUrl = "getResource?accessor=" + puzzle.introAccessor;
                    var introExtension = puzzle.introFilename.substr(puzzle.introFilename.lastIndexOf(".") + 1).toLowerCase();

                    if (introExtension === "pdf") {
                        var body = $("<object data='" + accessorUrl + "' style='width:100%; margin-top: 10px; margin-bottom: 3px; max-height: 200px; overflow-y: auto'/>");
                        var link = $("<div style='margin-bottom: 10px'><a style='color: #59A0E6' target=\"_blank\" href=\"" + accessorUrl + "\">Download</a></div>");
                        pane.append(body);
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
                                    location.reload();
                                } else {
                                    statusLabel.text(data.message);
                                }
                            });
                        }
                    });
                } else {
                    var label = $("<label style='width:100%; color:red; font-size: 64px; text-align: center; display: inline-block'>LOCKED</label>");
                    pane.append(label);
                }
            });
        });
    });
});

