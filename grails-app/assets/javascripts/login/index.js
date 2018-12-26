function onGoogle(googleUser) {


    console.log('google sign in', googleUser);
    var id_token = googleUser.getAuthResponse().id_token;

    document.getElementById('googleIdToken').value = id_token;
    document.querySelector('.googleAuth').submit();

    // var xhr = new XMLHttpRequest();
    // xhr.open('POST', 'googleAuth');
    // xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    // xhr.onload = function () {
    //     console.log('Signed in as: ' + xhr.responseText);
    // };
    // xhr.send('idtoken=' + id_token);
}

function loadedGoogle() {
    console.log('hi');
    gapi.signin2.render('withGoogle', {
        longtitle: true,
        onsuccess: onGoogle
    });
}