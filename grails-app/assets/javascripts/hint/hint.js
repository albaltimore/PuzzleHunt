/* global jQuery */
//= require jquery-3.2.0


function reloadHintQueue() {
    var tableBody = $("#hintsTable");
    $(".hintTableRow").detach();

    $.get("getHints", function (hintData) {
        console.log(hintData);

        hintData.hints.sort(function (a, b) {
            if (a.open !== b.open) {
                return b.open - a.open;
            }

            if ((a.owner === hintData.myName) !== (b.owner === hintData.myName)) {
                return (b.owner === hintData.myName) - (a.owner === hintData.myName);
            }
            if (!Boolean(a.owner) !== !Boolean(b.owner)) {
                return !Boolean(b.owner) - !Boolean(a.owner);
            }
            return b.createTime - a.createTime;
        }).forEach(function (hint) {

            var createdAgo = (new Date().getTime() - hint.createTime) / (1000.0 * 60);

            var lastHints = hintData.hints.filter(function (otherHint) {
                return otherHint.puzzle === hint.puzzle && otherHint.player === hint.player && otherHint.owner;
            });
            var lastOwner = lastHints.length ? lastHints[0].owner : "";


            var tr = $("<tr class='bloomberg-row' />");
            tableBody.append(tr);

            var myColor = function () {
                if (!hint.open) return "darkgray";
                if (hintData.myName === hint.owner) return  "lightgreen";
                if (!hint.owner && createdAgo < 10) return "lightred";
                if (!hint.owner) return "red";
                return "white";
            };

            tr.css("color", myColor());

            tr.mouseover(function () {
                tr.css("color", "black");
                tr.css("background-color", "white");
            });
            tr.mouseout(function () {
                tr.css("color", myColor());
                tr.css("background-color", "inherit");
            });

            tr.click(function () {
                window.location = "details?hintId=" + hint.id;
            });


            [
                hint.player,
                hint.puzzle,
                hint.question,
                hint.owner ? hint.owner : "(Nobody)",
                createdAgo < 1 ? "< 1min ago" : Math.ceil(createdAgo) + "min ago",
                lastOwner
            ].forEach(function (it) {
                var td = $("<td class='hintTableRow bloomberg-cell' />");
                tr.append(td);

                var label = $("<label style='cursor: pointer' />");
                td.append(it ? it.substr(0, 50) : "--");
                label.text(it);
            });

        });

    });
}

$(document).ready(function () {
    reloadHintQueue();
    var intervalID = window.setInterval(reloadHintQueue, 30000);
});