function timeDiffString(nextTime) {
    var lpad = x => (x < 10 ? '0' : '') + parseInt(x);
    return ( nextTime > 1000 * 60 * 60 ? lpad(nextTime / 60 / 60 / 1000) + ":" : "") + lpad((nextTime / 60 / 1000) % 60) + ":" + lpad((nextTime / 1000) % 60);
}

var module = {
    exports: {
        timeDiffString
    }
};