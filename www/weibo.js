var exec = require('cordova/exec');

module.exports = {
	login: function(onSuccess, onFail) {
		exec(onSuccess, onFail, "Weibo", "login", []);
	},

    isInstalled: function(onSuccess, onFail) {
        exec(onSuccess, onFail, "Weibo", "isInstalled", []);
    }
}
