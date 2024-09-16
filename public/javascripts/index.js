function layoutPage() {
    generatePageHeader();
}
function generatePageHeader() {
    var html = " <nav class=\"navbar navbar-expand-md navbar-dark fixed-top bg-skyBlue\">\n" +
        "        <a class=\"navbar-brand\" href=\"#\">短網址系統</a>\n" +
        "        <button class=\"navbar-toggler\" type=\"button\" data-toggle=\"collapse\" data-target=\"#navbarCollapse\"\n" +
        "                aria-controls=\"navbarCollapse\" aria-expanded=\"false\" aria-label=\"Toggle navigation\">\n" +
        "            <span class=\"navbar-toggler-icon\"></span>\n" +
        "        </button>\n" +
        "        <div class=\"collapse navbar-collapse\" id=\"navbarCollapse\">\n" +
        "            <ul class=\"navbar-nav mr-auto\">\n" +
        "                <li class=\"nav-item\">\n" +
        "                    <a class=\"nav-link\" href=\"index.html\">網址建立 <!--<span class=\"sr-only\">(current)</span>--></a>\n" +
        "                </li>\n" +
        "                <li class=\"nav-item\">\n" +
        "                    <a class=\"nav-link\" href=\"urlList.html\">網址列表</a>\n" +
        "                </li>\n" +
        "                <li class=\"nav-item\">\n" +
        "                    <a class=\"nav-link\" href=\"userList.html\">使用者列表</a>\n" +
        "                </li>\n" +
        "            </ul>\n" +
        /*        "            <form class=\"form-inline mt-2 mt-md-0\">\n" +
                "                <input class=\"form-control mr-sm-2 mx-2 col-7\" type=\"text\" placeholder=\"Search\" aria-label=\"Search\">\n" +
                "                <button class=\"btn btn-outline-light my-2 my-sm-0 col-3\" type=\"submit\">搜尋</button>\n" +
                "            </form>\n" +*/
        "            <a style=\"color: #FFF\" class=\"nav-link\" href='modifyPassword.html'>修改密碼</a>\n" +
        "            <a style=\"color: #FFF\" class=\"nav-link\" href='#' onclick=\"window.api.logout()\">登出</a>\n" +
        "\n" +
        "        </div>\n" +
        "    </nav>";
    document.getElementsByTagName("header")[0].innerHTML = html;
    $("#navbarCollapse a").each(function () {
        // checks if its the same on the address bar
        if (window.location.href == (this.href) || window.location.href == (this.href) + "#") {
            $(this).closest("li").addClass("active");
        }
    });
    //('location.pathname.split(\'/\')[3]');
}
//# sourceMappingURL=index.js.map