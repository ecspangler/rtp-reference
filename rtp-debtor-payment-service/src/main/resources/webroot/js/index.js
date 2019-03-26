
var cordovaApp = {
    initialize: function() {
        document.addEventListener('deviceready', this.onDeviceReady.bind(this), false);
    },
    onDeviceReady: function() {
        console.log('device is ready');
    },
};

cordovaApp.initialize();