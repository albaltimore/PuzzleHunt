function displayMessage(msg) {
    var elem = document.getElementById('otherMessage');
    elem.textContent = msg;
    elem.style.display = null;
}

function loadedGoogle() {
    console.log('hi');
    gapi.signin2.render('withGoogle', {
        longtitle: true,
        width: '500',
    });

    console.log(gapi);
    gapi.load('auth2', () => {
        gapi.auth2.getAuthInstance().attachClickHandler('withGoogle', {}, function (googleUser) {
            console.log('google sign in', googleUser);
            document.getElementById('googleIdToken').value = googleUser.getAuthResponse().id_token;
            document.querySelector('.googleAuth').submit();
        }, () => displayMessage('Failed to login with Google'));
    });
}