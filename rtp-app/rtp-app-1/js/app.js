
var $$ = Dom7;
var app;
var paymentApp;
var theme = 'auto';
if (document.location.search.indexOf('theme=') >= 0) {
    theme = document.location.search.split('theme=')[1].split('&')[0];
}


$(document).ready(function(e) {
    // Framewrork7 init
    id: 'com.payment.mobile',
    app = new Framework7({
        root: '#app',
        theme: theme,
        routes: routes
    });
    
    paymentApp = new Payment(appConfig.API);

    $$(document).off('page:afterin').on('page:afterin', function (e, page1) {
        var page = app.views.main.router.currentRoute.name;
        console.log(page);
        if(page == "transaction") {
            showTransaction();
        } else if (page == "account") {
            showAccount();
        }
    });
});

function showTransaction() {
    app.preloader.show();
     var params = {
        "accountNumber": paymentApp.getAccountId()
     }
    paymentApp.queryTransaction(params).then(function(res) {
        app.preloader.hide();
        var transactionList = res.transactions;
        var html = "";
        for(var i=0; i<transactionList.length; i++) {
            html += getTransationRow(transactionList[i]);
        }
        $(".page-current .transhistory ul").html(html);
    }).catch((err) => {
        $(".page-current .transhistory ul").html("<li>Oops! There was some error on server.</li>");
    });
}

function getTransationRow(item) {
    var icon = 'fa-check';
    
    var firstName ='aa';
    var lastName ='bb';
    
    if(item.status == "PENDING") {
        icon = 'fa-question';
    } else if(item.status == "REJECTED") {
        icon = 'fa-times';
    }
    if(item.creditDebitCode == "CREDIT") {
        firstName = item.senderFirstName;
        lastName  = item.senderLastName ;
    } else  {
        firstName = item.receiverFirstName;
        lastName  = item.receiverLastName ;
    }
    return '<li class="tran-' + item.status + '">\
        <div class="item-content"> \
            <div class="item-before stat-status"><div>' + '<i class="fas ' + icon + '"></i>' + '</div></div>\
            <div class="item-inner">\
                <div class="item-title-row toggle-item">\
                    <div class="item-title stat-title">' + firstName + ' ' + lastName + '</div>\
                </div>\
                <div class="item-subtitle stat-transaction"> Reference no.:' + item.transId + '</div>\
                <div class="orderdetail">Status: ' + item.status + '</div>\
            </div>\
            <div class="item-after stat-amount">' + item.amount + '</div>\
        </div>\
    </li>';
}

function sendPayment() {
     
    var data = {
        "senderAccountNumber": paymentApp.getAccountId(),
        "amount": $(".page-current .s-amount").val(),
        "receiverFirstName": $(".page-current .s-first-name").val(),
        "receiverLastName": $(".page-current .s-last-name").val(),
        "receiverEmail":  $(".page-current .s-phoneemail").val(),
        "receiverCellPhone":"null"
        
    }
    var emailPhone = $(".page-current .s-phoneemail").val();

    for(var key in data) {
        if(data[key] == "") {
            showToast("Please fill all fields.");
            return false;
        }
    }

    if(emailPhone == "") {
        showToast("Please fill all fields.");
        return false;
    }

    if(validateEmail(emailPhone)) {
        //data.receiverEmail = emailPhone;
    } else if (!isNaN(emailPhone)) {
        //data.receiverCellPhone = emailPhone;
    } else {
        showToast("Please enter either valid email address or 10 digit mobile number.");
        return false;
    }

    app.preloader.show();
    var params = {
        "payments": [data]
    }
    paymentApp.sendPayment(params).then((res) => {
        app.preloader.hide();
        // showToast("Thanks! Your payment has been completed.");
        $("form[name=from-send-payment]")[0].reset();
        
        $(".ack-account").html(res.payments[0].debtorAccountNumber);
        $(".ack-amount").html("$" + res.payments[0].amount.toFixed(2));
        $(".ack-name").html(res.payments[0].receiverFirstName + " " + res.payments[0].receiverLastName);
        var todayDate = getTodayDate();
        $(".ack-date").html(todayDate);
        app.popup.open(".popup-acknowledge");
    }).catch((err) => {
        app.preloader.hide();
        showToast("Oops there was some error on server. Please try again.");
    })
}

function getTodayDate() {
    var today = new Date();
    var dd = today.getDate();
    var mm = today.getMonth() + 1; //January is 0!
    
    var yyyy = today.getFullYear();
    if (dd < 10) {
      dd = '0' + dd;
    } 
    if (mm < 10) {
      mm = '0' + mm;
    } 
    var today = dd + '/' + mm + '/' + yyyy;
    return today;
}

function showToast (message) {
    var toastMsg = app.toast.create({
        text: message,
        closeTimeout: 3000,
        closeButton: false
    });
    toastMsg.open();
}

function validateEmail (elementValue) {
    var emailRegEx = /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}$/i;
    var status = (elementValue.search(emailRegEx) == -1) ? false : true;
    return status;
}

function login() {
    var data = {
        accountId: $(".page-current .login-accountId").val(),
        password: $(".page-current .login-password").val()
    }
    for(var key in data) {
        if(data[key] == "") {
            showToast("Please fill all fields.");
            return false;
        }
    }

    paymentApp.setAccountId(data.accountId);
    app.router.navigate("/send/");
}

function logout() {
    app.router.navigate("/");
}

function showAccount() {
    var profile = paymentApp.getProfile();
    $(".page-current .account-id").html(profile.id);
    $(".page-current .account-email").html(profile.email);
    $(".page-current .account-name").html(profile.firstName + ' ' + profile.lastName);
    $(".page-current .account-hero-info").html('$' + profile.balance);
}
