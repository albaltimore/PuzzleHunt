/* global jQuery */
//= require packaged/jquery/jquery


function reloadHintQueue() {
    var tableBody = $("#hintsTable");
    $(".hintTableRow").detach();

    $.get("getHints", function (hintData) {
        console.log(hintData);



        hintData.hints.map(function (hint) {
            var lastHints = hintData.hints.filter(function (otherHint) {
                return otherHint.puzzle === hint.puzzle && otherHint.player === hint.player && otherHint.owner;
            });
            hint.lastOwner = lastHints.length ? lastHints[0].owner : "";

            return hint;
        }).sort(function (a, b) {
            if (a.open !== b.open) {
                return b.open - a.open;
            }
            if ((a.owner === hintData.myName) !== (b.owner === hintData.myName)) {
                return (b.owner === hintData.myName) - (a.owner === hintData.myName);
            }
            if ((a.lastOwner === hintData.myName) !== (b.lastOwner === hintData.myName)) {
                return (b.lastOwner === hintData.myName) - (a.lastOwner === hintData.myName);
            }
            if (!Boolean(a.owner) !== !Boolean(b.owner)) {
                return !Boolean(b.owner) - !Boolean(a.owner);
            }
            return a.createTime - b.createTime;
        }).forEach(function (hint) {
            var createdAgo = (new Date().getTime() - hint.createTime) / (1000.0 * 60);

            var tr = $("<tr class='bloomberg-row' />");
            tableBody.append(tr);

            var myColor = function () {
                if (!hint.open) return "darkgray";
                if (hintData.myName === hint.owner) return  "lightgreen";
                if (!hint.owner && createdAgo < 10) return "pink";
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
                hint.lastOwner
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