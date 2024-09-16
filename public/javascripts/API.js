var API = /** @class */ (function () {
    function API() {
        this.apiUrl = location.protocol + '//' + location.hostname + (location.port ? ':' + location.port : '') + '/api/';
        // public apiUrl = "http://10.0.1.166:9000"+'/api/';
        this.session = '';
        this.timeout = 20 * 1000;
    }
    API.prototype.sendApi = function (targetUrl, method, data, onSucc, onError) {
        this.sendApiWithType("Form", targetUrl, method, data, onSucc, onError);
    };
    API.prototype.sendApiWithType = function (type, targetUrl, method, data, onSucc, onError) {
        if (!this.apiUrl) {
            console.debug("API.apiUrl null");
            if (onError)
                onError();
            return;
        }
        //get s
        if (Cookies.get("session") != null) {
            this.session = Cookies.get("session");
        }
        var url = this.apiUrl + targetUrl;
        var contenttype = "";
        if (type == "Form") {
            contenttype = "application/x-www-form-urlencoded";
            //data = JSON.stringify(this.getFormData(data));
        }
        else if ("JsonPOST") {
            contenttype = "application/json";
            data = JSON.stringify(data);
        }
        $.ajax({
            url: url,
            method: method,
            contentType: contenttype,
            headers: {
                "session": this.session,
            },
            dataType: 'json',
            timeout: this.timeout,
            data: data ? data : null,
            success: function (data) {
                if (onSucc) {
                    onSucc(data);
                }
            },
            error: function (jqXHR) {
                console.log(data);
                console.log(contenttype);
                console.log(method);
                //if 401, redirect back to login
                if (jqXHR.status == 401 || jqXHR.status == 0 || jqXHR.status == 400) {
                    // if( window.location.pathname.indexOf("login.html")<0 ){
                    //     window.tools.logout();
                    // }
                }
                if (jqXHR.status >= 200 && jqXHR.status < 300) {
                    if (onSucc) {
                        onSucc(jqXHR);
                    }
                }
                else if (onError) {
                    onError(jqXHR);
                }
            },
            beforeSend: function (xhr) {
                //window.tools.showLoading();
            },
            complete: function (data) {
                //alert("ccccc");
            }
        });
    };
    API.prototype.getFormData = function (data) {
        var unindexed_array = data;
        var indexed_array = {};
        $.map(unindexed_array, function (n, i) {
            if (indexed_array[n['name']] != null) {
                if (Array.isArray(indexed_array[n['name']])) {
                    indexed_array[n['name']].push(n['value']);
                }
                else {
                    var list = new Array();
                    list.push(indexed_array[n['name']]);
                    list.push(n['value']);
                    indexed_array[n['name']] = list;
                }
            }
            else {
                indexed_array[n['name']] = n['value'];
            }
        });
        return indexed_array;
    };
    API.prototype.logout = function () {
        Cookies.remove("session");
        swal({
            text: '成功登出!',
            showConfirmButton: false,
            timer: 1500
        });
        setTimeout(function () {
            document.location.href = "login.html";
        }, 1500);
    };
    return API;
}());
window.api = new API();
//# sourceMappingURL=API.js.map