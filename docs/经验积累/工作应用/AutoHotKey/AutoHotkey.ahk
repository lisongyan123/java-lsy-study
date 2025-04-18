;Notes: #==win !==Alt ^==Ctrl  +==shift ::分隔 run AHK命令  没有 return 会走下一个脚本

;================================================================================================================
;================================================================================================================
+^!p::
openfile=C:\Users\84216\Desktop\AutoHotkey.ahk
; opensoft=xxx.exe RUN %opensoft% "%openfile%"
Run "%openfile%"
return
;================================================================================================================
#F::
Run D:\飞书.lnk
;================================================================================================================
^+m:: ;将鼠标所在文件重命名为剪切板内容，ctrl+shift搭配子集
clipboard = %clipboard%   ; 把任何复制的文件, HTML 或其他格式的文本转换为纯文本
send,{F8}
sleep,100
mouseclick, right
send,m
send,^v
send, {enter}
tooltip,已为你将鼠标所在文件重命名为剪切板内容
sleep, 1500
tooltip,
return
;================================================================================================================
^+]:: ;复制当前鼠标所在文件文件名并替换为ffmpeg到剪切板 ，ctrl+shift子集
;请将你的鼠标放在目标文件位置，即将为你复制其文件名
mouseclick, right
send,m
sleep,100
send,^c
sleep,50
send, {enter}
Loop
{
StringReplace, clipboard, clipboard, %A_SPACE% , , UseErrorLevel ;清除空格
StringReplace, clipboard, clipboard, `, , , UseErrorLevel ;清除英文逗号
StringReplace, clipboard, clipboard, ， , , UseErrorLevel ;清除中文逗号
StringReplace, clipboard, clipboard, `; , , UseErrorLevel ;清除英文分号
StringReplace, clipboard, clipboard, ； , , UseErrorLevel ;清除英文分号
StringReplace, clipboard, clipboard, . , , UseErrorLevel ;清除英文句号
StringReplace, clipboard, clipboard, 。 , , UseErrorLevel ;清除中文句号
StringReplace, clipboard, clipboard, : , , UseErrorLevel ;清除英文冒号
StringReplace, clipboard, clipboard, ： , , UseErrorLevel ;清除中文冒号
StringReplace, clipboard, clipboard, ‘’ , , UseErrorLevel ;清除中文双引号1
StringReplace, clipboard, clipboard, “ , , UseErrorLevel ;清除中文双引号2
StringReplace, clipboard, clipboard, ” , , UseErrorLevel ;清除中文双引号3
StringReplace, clipboard, clipboard, ' , , UseErrorLevel ;清除英文双引号
StringReplace, clipboard, clipboard, 、 , , UseErrorLevel ;清除中文顿号
StringReplace, clipboard, clipboard, / , , UseErrorLevel ;清除右斜杠
StringReplace, clipboard, clipboard, \ , , UseErrorLevel ;清除左斜杠
StringReplace, clipboard, clipboard, - , , UseErrorLevel ;清除短横线
StringReplace, clipboard, clipboard, —— , , UseErrorLevel ;清除长横线
StringReplace, clipboard, clipboard, = , , UseErrorLevel ;清除等号
StringReplace, clipboard, clipboard, + , , UseErrorLevel ;清除加号
StringReplace, clipboard, clipboard, （ , , UseErrorLevel ;清除中文左括号
StringReplace, clipboard, clipboard, ） , , UseErrorLevel ;清除中文右括号
StringReplace, clipboard, clipboard, ( , , UseErrorLevel ;清除英文左括号
StringReplace, clipboard, clipboard, ) , , UseErrorLevel ;清除英文右括号
StringReplace, clipboard, clipboard, 【 , , UseErrorLevel ;清除中文【
StringReplace, clipboard, clipboard, 】 , , UseErrorLevel ;清除中文】
StringReplace, clipboard, clipboard, [ , , UseErrorLevel ;清除[
StringReplace, clipboard, clipboard, ] , , UseErrorLevel ;清除]
StringReplace, clipboard, clipboard, ! , , UseErrorLevel ;清除英文感叹号
StringReplace, clipboard, clipboard, ！ , , UseErrorLevel ;清除中文感叹号
StringReplace, clipboard, clipboard, ？ , , UseErrorLevel ;清除中文问号
StringReplace, clipboard, clipboard, ? , , UseErrorLevel ;清除英文问号
StringReplace, clipboard, clipboard, < , , UseErrorLevel ;清除英文<
StringReplace, clipboard, clipboard, > , , UseErrorLevel ;清除英文>
StringReplace, clipboard, clipboard, 《 , , UseErrorLevel ;清除中文《
StringReplace, clipboard, clipboard, 》 , , UseErrorLevel ;清除英文》
StringReplace, clipboard, clipboard, @ , , UseErrorLevel ;清除@
StringReplace, clipboard, clipboard, # , , UseErrorLevel ;清除#
StringReplace, clipboard, clipboard, $ , , UseErrorLevel ;清除$
StringReplace, clipboard, clipboard, `% , , UseErrorLevel ;清除%
StringReplace, clipboard, clipboard, ^ , , UseErrorLevel ;清除^
StringReplace, clipboard, clipboard, & , , UseErrorLevel ;清除&
StringReplace, clipboard, clipboard, * , , UseErrorLevel ;清除*
StringReplace, clipboard, clipboard, `` , , UseErrorLevel ;清除`
StringReplace, clipboard, clipboard, `:: , , UseErrorLevel ;清除::
StringReplace, clipboard, clipboard, " , , UseErrorLevel ;清除"
StringReplace, clipboard, clipboard, { , , UseErrorLevel ;清除{
StringReplace, clipboard, clipboard, }, , UseErrorLevel ;清除}
StringReplace, clipboard, clipboard, |, , UseErrorLevel ;清除|
StringReplace, clipboard, clipboard, ·, , UseErrorLevel ;清除中文···
StringReplace, clipboard, clipboard, ~, , UseErrorLevel ;清除~
StringReplace, clipboard, clipboard, _ , , UseErrorLevel ;清除_
    if ErrorLevel = 0  ; 不需要再进行替换.
        break
}
sleep,50
mouseclick, right
sleep,100
send,m
sleep,100
send,^v
sleep,50
send, {enter}
;clipboard = ffmpeg -i %clipboard%.mp4 -c:v libx264 -crf 24 -preset slower %clipboard%output.mp4 ;mp4版压制
clipboard = ffmpeg -i %clipboard%.mkv %clipboard%output.mp4 ;mkv转mp4
return
;================================================================================================================
#g::
run https://github.com/search?q=%clipboard% ,,max
tooltip,星星之火，可以燎原！！！
sleep, 1500
tooltip,
return

#y::
run https://www.youtube.com
tooltip,1个亿算什么，我们的目标是星辰大海！！！
sleep, 1500
tooltip,
return

#b::
run https://search.bilibili.com/all?keyword=%clipboard%
run https://search.bilibili.com/all?keyword=%clipboard%&order=stow
run https://www.baidu.com/s?wd=%clipboard%
tooltip,与其悲叹自己的命运，不如相信自己的力量！！！
sleep, 1500
tooltip,
return

#t:: ;淘宝搜索
run https://s.taobao.com/search?q=%clipboard%
return

#j:: ;京东搜索
run https://search.jd.com/Search?keyword=%clipboard%
return

#z::
run https://www.zhihu.com/search?type=content&q=%clipboard%
tooltip,希望我的文章对你我都能有所助益，有所启迪，感谢你的关注与支持。
sleep, 1500
tooltip,
return

#x::
run https://www.xinpianchang.com/index.php?app=search&kw=%clipboard%
run https://www.xinpianchang.com/user/stars/sort-recommend?from=tabUser
run https://www.xinpianchang.com/channel/index/sort-like
return

;同时按下Shift+Alt+d键，自动删除temp tencent wechat cloudmusic等缓存文件================================================================================================================
+!d::
send,#e
sleep,500
WinMaximize,A
sleep,500
FileRemoveDir, C:\Users\hocto\AppData\Local\Temp , 1
FileRemoveDir, C:\Users\hocto\AppData\Local\Netease\CloudMusic\Cache , 1
FileRemoveDir, C:\Users\hocto\AppData\Roaming\Tencent , 1
tooltip,接下来，小海将为你删除temp tencent wechat cloudmusic等缓存文件，请注意查看容量变化
sleep, 1000
tooltip,
return

;无损删除当前向下5行================================================================================================================

:*:555::
send,{end}{shiftdown}{home}{shiftup}{delete}{delete}
send,{end}{shiftdown}{home}{shiftup}{delete}{delete}
send,{end}{shiftdown}{home}{shiftup}{delete}{delete}
send,{end}{shiftdown}{home}{shiftup}{delete}{delete}
send,{end}{shiftdown}{home}{shiftup}{delete}{delete}
return

;可任意更改剪切板内容================================================================================================================
:*:gfff::
;clipboard = 工作总结：
clipboard = 小海实战项目预览版
send,^v
return
;================================================================================================================
:*:888:: ;复制当前行到剪切板
send,{home}{shiftdown}{end}{shiftup}
;send,^c
send,^x{delete}
clipboard = %clipboard%   ; 把任何复制的文件, HTML 或其他格式的文本转换为纯文本
send, {end}
return
;自动输入AutoHotkey================================================================================================================
:*:autoh::
clipboard = AutoHotkey
send,^v
return
; 此热字串通过后面命令将热字串替换成当前日期和时间 ===============================================================================================================
:*:iid::
FormatTime, CurrentDateTime,, yyyyMMdd ; 形式：成都_20211020_小海调色Version
clipboard = _%CurrentDateTime%_Version
send,^v
SendInput {left}{left}{left}{left}{left}{left}{left}
return
; 此热字串通过后面命令将热字串替换成当前日期和时间================================================================================================================
:*:idate::
FormatTime, CurrentDateTime,, yyyyMMdd-HHmm ; 形式：成都_20211020-1127_小海调色Version
clipboard = _%CurrentDateTime%_Version
send,^v
SendInput {left}{left}{left}{left}{left}{left}{left}
return
;左键拖选文字复制================================================================================================================
~LButton::
cos_mousedrag_treshold := 20 ; pixels
MouseGetPos, cos_mousedrag_x, cos_mousedrag_y
win1 := WinActive("A")
KeyWait LButton
MouseGetPos, cos_mousedrag_x2, cos_mousedrag_y2
win2 := WinActive("A")
WinGetClass cos_class, A
if(((abs(cos_mousedrag_x2 - cos_mousedrag_x) > cos_mousedrag_treshold
  or abs(cos_mousedrag_y2 - cos_mousedrag_y) > cos_mousedrag_treshold)) and win1 = win2
  and cos_class != "ConsoleWindowClass")
{
	SendInput ^c
}
return
;用快捷键得到当前选中文件的路径================================================================================================================
#+z::
send ^c
sleep,200
clipboard=%clipboard% ;windows 复制的时候，剪贴板保存的是“路径”。只是路径不是字符串，只要转换成字符串就可以粘贴出来了
tooltip,%clipboard% ;提示文本
sleep,500
tooltip,
return
；================================================================================================================
;cosea原创代码之隐藏windows系统的桌面图标 ================================================================================================================
#h::
send,{AppsKey}
send,v
send,d
return
;================================================================================================================
+space::
run http://fanyi.youdao.com/
tooltip,世界是你们的，也是我们的，但归根结底是你们的！！！
sleep, 3000
tooltip,
return
;================================================================================================================
!space:: ;cosea按快捷键同时搜索多个页面
run https://www.google.com/search?q=%clipboard% ;用google搜索剪切板的内容
clipboard1=%clipboard%&tbs=qdr:1,sbd:1
run https://www.google.com/search?q=%clipboard1% ;按时间排序
run https://www.google.com/search?q=%clipboard%&tbs=qdr:m ;只显示最近一个月信息
run https://www.google.com/search?q=%clipboard%&tbs=qdr:y ;只显示最近一年信息
run https://www.google.com/search?q=%clipboard%&as_filetype=pdf ;指定PDF文件
run https://www.google.com/search?q=%clipboard%&tbs=li:1 ;精确匹配
run https://www.google.com/search?&as_epq=%clipboard% ;完全匹配
run https://www.google.com/search?q=%clipboard% inurl:gov ;url包括gov的网站信息
run https://www.google.com/search?q=intitle:%clipboard% ;文章标题中包含关键词的结果
run https://www.google.com/search?q=%clipboard%&source=lnt&lr=lang_zh-CN|lang_zh-TW ;&source=lnt&lr=lang_zh-CN|lang_zh-TW，指定中文网页
run https://www.google.com/search?q=%clipboard%&tbm=isch&tbs=imgo:1 ;&tbm=isch指定搜索谷歌图片
run https://www.google.com/search?q=%clipboard%&tbm=isch&tbs=isz:l ;将URL更改为大尺寸图片&tbs=isz:l
run https://image.baidu.com/search/index?z=3&tn=baiduimage&word=%clipboard% ;z=3是大尺寸，z=9是特大尺寸
run https://www.behance.net/search?content=projects&sort=appreciations&time=week&featured_on_behance=true&search=%clipboard%
run https://www.zcool.com.cn/search/content?&word=%clipboard%
clipboard2=%clipboard%&tbm=isch&tbs=qdr:m,isz:l,imgo:1
run https://www.google.com/search?q=%clipboard2% ;为URL添加&tbs=qdr:m，只显示新近一个月内的图片，&tbs=imgo:1，显示图片大小
tooltip, 那晚，风也美，人也美。。。
sleep 1500
tooltip,
return
;================================================================================================================
#space::
run https://www.baidu.com/
tooltip, 以前向往更加辽阔的天空，走了一小圈，发现`n`n无论是平平淡淡，还是轰轰烈烈，都隐藏不住自己内心的那份虚无缥缈！！！
sleep 2000
tooltip,
return

;================================================================================================================
; 按caps键盘相当于按enter键了
; $CapsLock::Enter
LAlt & Capslock::SetCapsLockState, % GetKeyState("CapsLock", "T") ? "Off" : "On"
;================================================================================================================
;最爱代码之窗口置顶
;!enter::
;    WinGet ow, id, A
;    WinTopToggle(ow)
;    return
;WinTopToggle(w) {
;
;    WinGetTitle, oTitle, ahk_id %w%
;    Winset, AlwaysOnTop, Toggle, ahk_id %w%
;    WinGet, ExStyle, ExStyle, ahk_id %w%
;    if (ExStyle & 0x8)  ; 0x8 为 WS_EX_TOPMOST.在WinGet的帮助中
;        oTop = 置顶
;    else
;        oTop = 取消置顶
;    tooltip %oTitle% %oTop%
;    SetTimer, RemoveToolTip, 5000
;    return
;
;    RemoveToolTip:
;    SetTimer, RemoveToolTip, Off
;    ToolTip
;    return
;}

;================================================================================================================

^Esc::Pause ; 按一次 ^+Esc 会暂停脚本. 再按一次则取消暂停

::china::https://www.baidu.com/

::/hh::哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈

::/main::    public static void main(String[] args) {{}}

::sys::   System.out.println();

::center::  <center><h1> </h1></center>

::==::;================================================================================================================

;notepad, 没有notepad就弹出来一个, 有就直接弹出来 ================================================================================================================

!n::Run notepad
#n:: ;cosea强行修改版，最常用功能之打开记事本
IfWinNotExist ahk_class Notepad
{
    run notepad,,max
    WinActivate
}
Else IfWinNotActive ahk_class Notepad
{
    WinActivate
    sleep,500
    WinMaximize,A
}
Else
{
    WinMinimize
}
Return

#=:: ;窗口透明化增加或者减弱
    WinGet, ow, id, A
    WinTransplus(ow)
    return

#-:: ;窗口透明化增加或者减弱
    WinGet, ow, id, A
    WinTransMinus(ow)
    return
WinTransplus(w){

    WinGet, transparent, Transparent, ahk_id %w%
    if transparent < 255
        transparent := transparent+10
    else
        transparent =
    if transparent
        WinSet, Transparent, %transparent%, ahk_id %w%
    else
        WinSet, Transparent, off, ahk_id %w%
    return
}
WinTransMinus(w){

    WinGet, transparent, Transparent, ahk_id %w%
    if transparent
        transparent := transparent-10
    else
        transparent := 240
    WinSet, Transparent, %transparent%, ahk_id %w%
    return
}
