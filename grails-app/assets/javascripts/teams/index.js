//= require jquery/jquery
//= require shared/modal

var timeDiffString = require("/shared/countdown.js").timeDiffString;

var hasStarted = false;
var startTime = 0;

function onFail() {
    showDialog("An error has occurred", location.reload);
}

function reload() {
    showLoading();
    $.get('getState', data => {
        closeLoading();
        console.log(data);

        $(".hunt-container").text(`Welcome to ${data.hunt}`);
        $(".team-members").empty();
        $('.player-name').text(data.name);

        if (data.team) {
            if (hasStarted && data.team.isFinalized) {
                location.reload();
                return;
            }

            $(".team-status-label").text(`You are on team`);
            $(".team-status-team").text(data.team.name);
            $(".team-create").css('display', 'none');
            $(".team-leave").css('display', data.team.isFinalized ? 'none' : 'inline-block');
            $(".team-members-container").css('display', 'block');
            $(".team-finalize-container").css('display', 'block');
            $(".team-invite-container").css('display', 'block');
            $(".team-request-container").css('display', 'block');
            $(".team-join").css('display', 'none');
            $(".team-invite-key").text(data.team.key);


            $(".team-status-public").css('display', 'block').empty().append(`<label>Your team is currently</label>
                <label class="emp">${(data.team.isPublic ? '' : 'Not ') + "Visible"}</label>
                <label>to others</label>\n`).append($(`
                <label class="puzzle-button">Make ${data.team.isPublic ? 'Hidden' : 'Visible'}</label>`).click(evt => {
                showLoading();
                $.post('publicize', {isPublic: !data.team.isPublic}, function () {
                    closeLoading();
                    reload();
                }).fail(onFail);
            }));

            var memberNames = {};

            data.team.players.forEach(p => {
                memberNames[p.name] = true;
                var label = $(`<label class="team-member"/>`);
                label.text(p.name);
                $(".team-members").append(label).append(' ');
            });

            if (data.team.isFinalized) {
                if (data.team.hasStarted) {
                    $('.team-finalize').css('display', 'none');
                } else {
                    $('.team-finalize').text('UnFinalize').click(evt => {
                        showLoading();
                        $.post('makeFinal', {isFinalized: false}, data => {
                            closeLoading();
                            reload();
                        }).fail(onFail);
                    });
                }

                $('.team-finalize-label').text('Your team is finalized. The hunt will start soon.');
            } else {
                $('.team-finalize').text('Finalize').click(evt => {
                    $.get('getState', stateData => {
                        if (!stateData.team) onFail();
                        showConfirmDialog(`Warning!\n
                            Are you sure you want to finalize your team?
                            You will not be able to accept new team members. Double check you have accepted any new member requests.
                            You will not be able to undo this once the hunt starts.
                            
                            Current members: ${stateData.team.players.map(p => p.name).join(', ')}
                            ${stateData.team.invites.length ? "There are" + stateData.team.invites.length + "  requests to join your team." : ""} 
                            `, "Yes", "No", () => {
                            showLoading();
                            $.post('makeFinal', {isFinalized: true}, data => {
                                closeLoading();
                                location.reload();
                            }).fail(onFail);
                        });
                    }).fail(onFail);
                });
                $('.team-finalize-label').text('Finalize your team when you are ready to start.');
            }


            var invitesFiltered = data.team.invites.filter(invite => !memberNames[invite.player]);
            if (invitesFiltered.length) {
                invitesFiltered.forEach((invite, idx) => {
                    $(".team-request-list")
                        .append(`<label class="team-other-index">${idx + 1}</label>`)
                        .append($("<label/>").text(invite.player))
                        .append(' ').append($("<label class='link'>Accept</label>").click(evt => {
                        if (data.team.maxTeamSize <= data.team.players.length) {
                            showDialog("Your team has already reached them maximum of " + data.team.maxTeamSize + " members.")
                        } else {
                            showLoading();
                            $.post('teamAccept', {invite: invite.id}, data => {
                                closeLoading();
                                location.reload();
                            }).fail(onFail);
                        }
                    }))
                        .append(' ').append($("<label class='link'>Decline</label>").click(evt => {
                        $.post('teamDecline', {invite: invite.id}, data => {
                            closeLoading();
                            location.reload();
                        }).fail(onFail);
                    }));
                });
            } else {
                $(".team-request-container").css('display', 'none');
            }
        } else {
            $(".team-status-label").text('You are not on a team');
            $(".team-status-team").text('');
            $(".team-create").css('display', 'inline-block');
            $(".team-leave").css('display', 'none');
            $(".team-members-container").css('display', 'none');
            $(".team-status-public").css('display', 'none');
            $(".team-finalize-container").css('display', 'none');
            $(".team-invite-container").css('display', 'none');
            $(".team-request-container").css('display', 'none');
            $(".team-join").css('display', 'block');
        }

        $(".team-list").empty();

        var teamsFiltered = data.teams.filter(team => !data.team || team.name !== data.team.name);

        if (teamsFiltered.length) {
            $(".team-list-container").css('display', 'block');
            teamsFiltered.forEach((team, teamIdx) => {
                var div = $(`<div class="team-other-container"><label class="team-other-index">${teamIdx + 1}</label></div>`);
                $(".team-list").append(div);

                var teamName = $("<label class='team-other-name'></label>");
                teamName.text(team.name);
                div.append(teamName);
                div.append(' ');

                var players = $("<label class='team-other-members'></label>");
                players.text(`${team.players} Member${team.players === 1 ? '' : 's'}`);
                div.append(players);
                div.append(' ');

                if (!data.team && !team.hasInvite) {
                    var join = $("<label class='link'>Join Team</label>");
                    div.append(join);
                    join.click(evt => {
                        showConfirmDialog(`Are you sure you want to join team ${team.name}?`, 'Yes', 'No', () => {
                            $.post('playerInvite', {key: team.key}, data => {
                                reload();
                            }).fail(onFail);
                        });
                    });
                }

                if (team.hasInvite) {
                    div.append('<label>Invite Sent</label>')
                }
            });
        } else {
            $(".team-list-container").css('display', 'none');
        }
    }).fail(onFail);
}


$(document).ready(function () {

    $(".team-create").click(evt => {
        showLoading();
        $.post("createTeam", data => {
            closeLoading();
            reload();
        }).fail(err => {
            showDialog("An error has occurred", location.reload);
        });
    });

    $(".team-leave").click(evt => {

        showConfirmDialog("Are you sure you want to leave your team?", "Yes", "No", () => {
            showLoading();
            $.post("leaveTeam", data => {
                closeLoading();
                reload();
            }).fail(onFail);
        });
    });

    $(".team-join-submit").click(evt => {
        var text = $(".team-join-key").val();
        console.log(text);
        if (!text) {
            showDialog("Please enter the key of the team to join");
            return;
        }
        showLoading();
        $.post('playerInvite', {key: text}, data => {
            $(".team-join-key").val('');
            showDialog("Invite sent!");
        }).fail(err => {
            showDialog("Cannot request to join this team");
        });
    });

    var reloadTimer = () => {
        $.get('start', data => {
            console.log(data);
            if (data.hasStarted) {
                location.reload();
                return;
            }

            function startedLabel() {
                $('.puzzle-timer').empty().append(`<label class="puzzle-timer-heading">The hunt has started!</label>
                <label>Finalize your team and join the hunt.</label>`);
            }

            if (data.startsIn < 0) {
                hasStarted = true;
                startedLabel();
            } else {
                startTime = data.startsIn + Date.now();
                var start;
                $('.puzzle-timer').empty().append(start = $(`<label class="puzzle-timer-heading puzzle-timer-heading-time"></label>`));

                var intervalID = setInterval(() => {
                    var left = startTime - Date.now();
                    if (left < 0) {
                        clearInterval(intervalID);
                        startedLabel();
                        setTimeout(location.reload, 2000);
                        return;
                    }
                    start.text(timeDiffString(left));
                }, 25);
            }

            reload();
        }).fail(onFail);
    };
    reloadTimer();
});
