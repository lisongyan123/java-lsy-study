1. OAuth 2.0 的使用从来都不应该脱离 HTTPS, 因为访问令牌、应用密钥敏感信息要在网络上传输, 都离不开 HTTPS 的保护, HTTPS 也只是保证了访问令牌等重要信息在网络传输上的安全
2. OAuth 协议被发明的目的, 就是用令牌代替用户名和密码 
3. OAuth 2.0 不能被直接用来“从事”身份认证协议的“工作”, 虽然 OAuth2.0 的使用要求是在 HTTPS 的环境下, 但这并不能解决 JWT 令牌对第三方软件“不透明”的问题, 还需要进行加密

# refresh token

1. Access Token具有较长的有效期, 一旦被盗用, 攻击者就可以拿Access Token使用很长时间, 聪明的你可能会想到, 攻击者可以同时盗取Refresh Token 
2. 授权服务必须维护 Refresh
3. Token与客户端的绑定关系, 也就是说只有合法用户的客户端(可通过IP,UA等资料判断)来请求是可以通过的。退一步讲, 如果攻击者模拟了客户端可以执行刷新请求, 那么就要看谁先刷
5. 由于授权服务可以设置 Refresh Token 一次有效, 因此不管哪个先刷新, 另一个人刷新就会报错。如果用户先刷新, 攻击者以 Access Token 和 Refresh Token 的双重失效结束游戏。如果攻击者先刷新了, 合法用户就会收到报错信息, 授权服务会引导用户 重新开始认证, 从而把有效的 Refresh Token 拿回到合法用户这里

# OAuth2 获取 AccessToken 认证步骤

1. 授权码模式
   ```json
    {
    "code": 200,    // 200表示请求成功，非200标识请求失败, 以下不再赘述 
    "msg": "ok",
    "data": {
        "access_token": "7Ngo1Igg6rieWwAmWMe4cxT7j8o46mjyuabuwLETuAoN6JpPzPO2i3PVpEVJ",     // Access-Token值
        "refresh_token": "ZMG7QbuCVtCIn1FAJuDbgEjsoXt5Kqzii9zsPeyahAmoir893ARA4rbmeR66",    // Refresh-Token值
        "expires_in": 7199,                 // Access-Token剩余有效期，单位秒  
        "refresh_expires_in": 2591999,      // Refresh-Token剩余有效期，单位秒  
        "client_id": "1001",                // 应用id
        "scope": "userinfo",                // 此令牌包含的权限
        "openid": "gr_SwoIN0MC1ewxHX_vfCW3BothWDZMMtx__"     // openid 
    }
    }
   ```
2. 密码模式
    ```json
    http://sa-oauth-server.com:8001/oauth2/token
    ?grant_type=password
    &client_id={value}
    &client_secret={value}
    &username={value}
    &password={value}
   
   // 返回参数如下
   {
    "code": 200,    // 200表示请求成功，非200标识请求失败, 以下不再赘述 
    "msg": "ok",
    "data": {
        "access_token": "7Ngo1Igg6rieWwAmWMe4cxT7j8o46mjyuabuwLETuAoN6JpPzPO2i3PVpEVJ",     // Access-Token值
        "refresh_token": "ZMG7QbuCVtCIn1FAJuDbgEjsoXt5Kqzii9zsPeyahAmoir893ARA4rbmeR66",    // Refresh-Token值
        "expires_in": 7199,                 // Access-Token剩余有效期，单位秒  
        "refresh_expires_in": 2591999,      // Refresh-Token剩余有效期，单位秒  
        "client_id": "1001",                // 应用id
        "scope": "",                        // 此令牌包含的权限
        "openid": "gr_SwoIN0MC1ewxHX_vfCW3BothWDZMMtx__"     // openid 
    }
    }


   ```
3. 隐藏式

```json
// 通过 url 来引导用乎访问, 直接返回Access-Token到前端页面
    http://sa-oauth-server.com:8001/oauth2/authorize
    ?response_type=token
    &client_id={value}
    &redirect_uri={value}
    &scope={value}
    $state={value}

```

4. 凭证式
```json
http://sa-oauth-server.com:8001/oauth2/client_token?grant_type=client_credentials&client_id={value}&client_secret={value}
// 返回参数
{
"code": 200,
"msg": "ok",
"data": {
"client_token": "HmzPtaNuIqGrOdudWLzKJRSfPadN497qEJtanYwE7ZvHQWDy0jeoZJuDIiqO", // Client-Token 值
"expires_in": 7199, // Token剩余有效时间，单位秒 
"client_id": "1001", // 应用id
"scope": null           // 包含权限 
}
}

```
