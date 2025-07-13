# 浏览控制台 console 的 post 请求

```javascript
var url = "https://devopendata.alphalawyer.cn/sop-admin/api/config.limit.update/";
var params = {
    data: "%7B%22id%22%3A10%2C%22routeId%22%3A%22i.penaltyList1.0%22%2C%22appKey%22%3A%2220220330958717615763619840%22%2C%22limitIp%22%3A%22%22%2C%22limitKey%22%3A%22%22%2C%22execCountPerSecond%22%3A10%2C%22durationSeconds%22%3A10%2C%22limitCode%22%3A%22%22%2C%22limitMsg%22%3A%22%22%2C%22tokenBucketCount%22%3A5%2C%22limitStatus%22%3A1%2C%22limitType%22%3A1%2C%22orderIndex%22%3A1%2C%22remark%22%3A%22%22%2C%22typeKey%22%3A%5B1%2C2%5D%2C%22gmtCreate%22%3A%222022-05-10%2019%3A23%3A43%22%2C%22gmtModified%22%3A%222022-05-10%2022%3A24%3A51%22%2C%22serviceId%22%3A%22alpha-penalty%22%7D",
    access_token: "FF59DFD03D5745A28FF9C52E9437D26A"
};
var xhr = new XMLHttpRequest();
xhr.open("POST", url, true);
xhr.setRequestHeader("Content-Type", "application/json");
xhr.onload = function (e) {
    if (xhr.readyState === 4) {
        if (xhr.status === 200) {
            console.log(xhr.responseText);
        } else {
            console.error(xhr.statusText);
        }
    }
};
xhr.onerror = function (e) {
    console.error(xhr.statusText);
};
xhr.send(JSON.stringify(params));
```

# 浏览控制台console的get请求

```javascript
var token = "AD3BA267AD984966B3D2179C18FF44F0";
var xhr = new XMLHttpRequest();
var url =
    xhr.open('GET', 'https://devopendata.alphalawyer.cn/gateway/open-bill/open/getTokenKey?appId=111');
xhr.setRequestHeader("x-access-token", token);
xhr.send(null);
xhr.onload = function (e) {
    var xhr = e.target;
    console.log(xhr.responseText);
}
```

# 控制台调接口

```javascript
function getPromiseRequest(url) {
    return new Promise(function (resolve, reject) {
        var xhr = new XMLHttpRequest();

        xhr.onreadystatechange = function () {
            if (xhr.readyState != 4) return;// 避免执行reject函数
            if (xhr.readyState == 4 && xhr.status == 200) {
                resolve(xhr.responseText);
            } else {
                reject('找不到服务器');
            }
        };
        xhr.open('post', url);
        xhr.send(null);

    })
}

getPromiseRequest('http://localhost:9097/penalty/api/v2/search/findPenaltyById?lid=AC0FD32928DDAE18A58EDF06983D9476&query=%E5%85%A8%E6%96%87_0%3A%E6%B3%95%E5%BE%8B&=')
    .then(function (data) {
        console.log(data);
        return getPromiseRequest('http://localhost:9097/penalty/api/v2/search/findPenaltyById?lid=AC0FD32928DDAE18A58EDF06983D9476&query=%E5%85%A8%E6%96%87_0%3A%E6%B3%95%E5%BE%8B&=');
    }, function (err) {
        console.log(err);
    }).then(function (data) {
    console.log(data);
})
```

```javascript

<script type="text/javascript">
    var btn=document.getElementsByTagName("button")[0];
</script>
```

```javascript
var url = "https://devopendata.alphalawyer.cn/sop-admin/api/config.limit.update/";
var params = {data:"%7B%22id%22%3A10%2C%22routeId%22%3A%22i.penaltyList1.0%22%2C%22appKey%22%3A%2220220330958717615763619840%22%2C%22limitIp%22%3A%22%22%2C%22limitKey%22%3A%22%22%2C%22execCountPerSecond%22%3A10%2C%22durationSeconds%22%3A10%2C%22limitCode%22%3A%22%22%2C%22limitMsg%22%3A%22%22%2C%22tokenBucketCount%22%3A5%2C%22limitStatus%22%3A1%2C%22limitType%22%3A1%2C%22orderIndex%22%3A1%2C%22remark%22%3A%22%22%2C%22typeKey%22%3A%5B1%2C2%5D%2C%22gmtCreate%22%3A%222022-05-10%2019%3A23%3A43%22%2C%22gmtModified%22%3A%222022-05-10%2022%3A24%3A51%22%2C%22serviceId%22%3A%22alpha-penalty%22%7D",access_token:"FF59DFD03D5745A28FF9C52E9437D26A"};
var xhr = new XMLHttpRequest();
xhr.open("POST",url,true);
        xhr.setRequestHeader("Content-Type","application/json");
        xhr.onload=function(e){
        if(xhr.readyState===4){
        if(xhr.status===200){
        console.log(xhr.responseText);
        }else{
            console.error(xhr.statusText);
        }}};
        xhr.onerror=function(e){
        console.error(xhr.statusText);
        };
        xhr.send(JSON.stringify(params));
```