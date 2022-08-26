var xmlHttp = false;
var default_head_path = "/hs-tmp/pic/uid_2-time_688-image.jpg";
// var default_head_path = "uid_2-time_688-image.jpg";
// axios 默认值
axios.defaults.baseURL = 'http://localhost:10001';
// AJAX 初始化
function initAJAX() {
    if (window.XMLHttpRequest) {
        xmlHttp = new XMLHttpRequest();
    }
    else if (window.ActiveXObject) {
        try {
            xmlHttp = new ActiveXObject("Msxml2.XMLHTTP");
        } catch (e) {
            try {
                xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
            } catch (e) {
                window.alert("浏览器不支持ajax")
            }
        }
    }
}
// Cookie 操作
// 创建
function setCookie(cookieKey, cookieValue, exhours) {
    var d = new Date();
    d.setTime(d.getTime() + (exhours * 60 * 60 * 1000));
    var expires = "expires=" + d.toGMTString();
    document.cookie = cookieKey.trim() + "=" + cookieValue + "; " + expires + ";path=/";
}
// 获取
function getCookie(cookieKey) {
    var name = cookieKey + "=";
    var ca = document.cookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i].trim();
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length).trim();
        }
    }
    return "";
}
// 删除
function deleteCookie(cookieKey) {
    setCookie(cookieKey, "", -1);
    // document.cookie = cookieKey.trim() + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT";
}
// 检测 token
function checkToken() {
    const token = getCookie("token");
    const uid = getCookie('uid');
    // console.log(token);
    if (token == "") {
        alert("未登录, 请先登录/注册再查看");
        top.location = "login.html";
    } else {
        axios({
            method: 'get',
            headers: {
                'Content-type': 'application/json',
                'token': token
            },
            url: '/entry/search/user/get',
            params: {
                'uid': uid
            }
        }).then(function (response) {
            var d = response.data;
            if (d['code'] != 200) {
                // console(d);
                alert("登录信息已过期/被篡改, 请重新登录\n");
                deleteCookie('token');
                deleteCookie('uid');
                deleteCookie('role');
                top.location = "login.html";
            } else {
                setCookie('token', token, 5);
                setCookie('uid', uid, 5);
                setCookie('role', getCookie('role'), 5);
            }
        }).catch(function (error) {
            alert("登录异常:" + error);
            deleteCookie('token');
            deleteCookie('uid');
            top.location = "login.html";
        })
    }
}
// 头像处理
function getHeadImage(path) {
    if (path == 'default') {
        return default_head_path;
    } else {
        return path;
    }
}
// Cookie Role Number
function getCookieRole(student, teacher, admin) {
    var res = 0;
    if (student) {
        res += 1;
    }
    if (teacher) {
        res += 2;
    }
    if (admin) {
        res += 4;
    }
    return res;
}
function isStudent(number) {
    return number == 1 || number == 3 || number == 5 || number == 7;
}
function isTeacher(number) {
    return number == 2 || number == 6 || number == 7;
}
function isAdmin(number) {
    return number == 4 || number == 5 || number == 6 || number == 7;
}
// 时间
function getTime(element) {
    document.getElementById(element).innerText = new Date().toLocaleTimeString();
    setTimeout("getTime('" + element + "')", 1000);
}
// 判断是否为图片
function checkImg(file) {
    if (!/\.(jpg|jpeg|png|GIF|JPG|PNG|gif)$/.test(file)) {
        return false;
    } else {
        return true;
    }
}
function regImg(filename) {
    return /\.(jpg|jpeg|png|GIF|JPG|PNG|gif)$/.test(filename);
}
// 获取问题类型
async function getTypes(token) {
    var types = [];
    await axios({
        'method': 'get',
        'headers': {
            'Content-Type': 'application/json',
            'token': token
        },
        'url': '/entry/search/type/all'
    }).then(res => {
        const data = res.data;
        console.log(data['message']);
        if (data['code'] == 200) {
            types = data['objects'];
        } else {
            types.push('问题类型获取异常');
        }
    }).catch(err => {
        types.push('问题类型获取错误');
    })
    return types;
}
// axios 请求设定
function axiosRequest(files, uid, token) {
    let formDatas = new FormData();
    formDatas.append('uid', uid);
    var url = "";
    if (files.length == 1) {
        if (checkImg(files[0])) {
            formDatas.append('image', files[0]);
            url = '/store/upload/image';
        } else {
            formDatas.append('file', files[0]);
            url = '/store/upload/file';
        }
    } else {
        if (checkImg(files[0])) {
            Array.from(files).forEach(im => {
                formDatas.append('images', im);
            })
            url = '/store/upload/images';
        } else {
            Array.from(files).forEach(fi => {
                formDatas.append('files', fi);
            })
            url = '/store/upload/files';
        }
    }
    var tmp = {
        'method': 'post',
        'headers': {
            'token': token
        },
        'url': url,
        'data': formDatas
    }
    return tmp;
}