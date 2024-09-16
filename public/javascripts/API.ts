interface Window { api: API; }
class API{
    public apiUrl = location.protocol+'//'+location.hostname+(location.port ? ':'+location.port : '') + '/api/';
   // public apiUrl = "http://10.0.1.166:9000"+'/api/';
    public session : string = '';

    constructor(){

    }
    private timeout = 20 * 1000;

    sendApi(targetUrl : string, method : string, data : any, onSucc : any, onError : any) {

        this.sendApiWithType("Form",targetUrl,method,data,onSucc,onError);
    }
    sendApiWithType(type: string , targetUrl : string, method : string, data : any, onSucc : any, onError : any) {

        if( !this.apiUrl ){
            console.debug( "API.apiUrl null");
            if(onError) onError();
            return;
        }

        //get s
        if ( Cookies.get("session") != null ) {
            this.session = Cookies.get("session") ;
        }

        var url = this.apiUrl+targetUrl;


        var contenttype = "";
        if (type == "Form") {
            contenttype = "application/x-www-form-urlencoded";
            //data = JSON.stringify(this.getFormData(data));
        }else if("JsonPOST"){
            contenttype = "application/json";
            data = JSON.stringify(data);
        }


        $.ajax({
            url: url,
            method: method,
            contentType: contenttype,
            headers: {
                "session" : this.session,
            },
            dataType : 'json',
            timeout : this.timeout,
            data: data?data:null,
            success: function (data : any) {
                if (onSucc) {

                    onSucc(data);
                }
            },
            error: function (jqXHR : any) {
                console.log(data);
                console.log(contenttype);
                console.log(method);
                //if 401, redirect back to login
                if(jqXHR.status==401 || jqXHR.status==0 || jqXHR.status == 400){
                    // if( window.location.pathname.indexOf("login.html")<0 ){
                    //     window.tools.logout();
                    // }
                }

                if(jqXHR.status>=200 && jqXHR.status<300){
                    if(onSucc) {
                        onSucc(jqXHR);
                    }
                }
                else if( onError ) {

                    onError(jqXHR);
                }
            },
            beforeSend:function(xhr : any){
                //window.tools.showLoading();

            },
            complete:function(data){
                //alert("ccccc");

            }
        });
    }

    getFormData(data) {
        var unindexed_array = data;
        var indexed_array = {};

        $.map(unindexed_array, function(n, i) {

            if ( indexed_array[n['name']] != null ) {

                if ( Array.isArray(indexed_array[n['name']])) {
                    indexed_array[n['name']].push(n['value']);
                }
                else {
                    var list = new Array();
                    list.push(indexed_array[n['name']]) ;
                    list.push(n['value']);
                    indexed_array[n['name']] = list;
                }
            }
            else {
                indexed_array[n['name']] = n['value'];
            }

        });

        return indexed_array;
    }
    logout() {
        Cookies.remove("session");
        swal({
            text:'成功登出!',
            showConfirmButton: false,
            timer: 1500
        });
        setTimeout(()=>{
            document.location.href ="login.html";
        },1500)
    }
}

window.api =new API();

