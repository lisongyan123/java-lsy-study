#Requires AutoHotkey v2.0

; 创建主窗口
MyGui := Gui(, "我的 AHK v2 GUI 示例")

; 添加文本控件
MyGui.Add("Text",, "这是一个 AutoHotkey v2 GUI 示例")

; 添加编辑框
edtName := MyGui.Add("Edit", "w200", "在这里输入文本")

; 添加复选框
chkOption := MyGui.Add("CheckBox",, "启用选项")

; 添加单选按钮
opt1 := MyGui.Add("Radio", "Group", "选项 1")
opt2 := MyGui.Add("Radio",, "选项 2")
opt3 := MyGui.Add("Radio",, "选项 3")

; 添加下拉列表
ddlChoice := MyGui.Add("DropDownList", "w100", ["红色", "绿色", "蓝色"])

; 添加按钮
btnSubmit := MyGui.Add("Button", "Default w80", "确定")
btnSubmit.OnEvent("Click", SubmitForm)

btnCancel := MyGui.Add("Button", "x+10 w80", "取消")
btnCancel.OnEvent("Click", (*) => MyGui.Destroy())

; 添加状态栏
sb := MyGui.Add("StatusBar",, "就绪")

; 显示 GUI
MyGui.Show("w300 h250")

; 提交按钮的事件处理函数
SubmitForm(*)
{
    ; 获取输入值
    name := edtName.Value
    optionChecked := chkOption.Value
    selectedOption := opt1.Value ? 1 : opt2.Value ? 2 : 3
    color := ddlChoice.Text
    
    ; 显示消息框
    MsgBox(
        "您输入的信息:`n"
        "文本: " name "`n"
        "复选框: " (optionChecked ? "已选中" : "未选中") "`n"
        "单选按钮: 选项 " selectedOption "`n"
        "颜色: " color,
        "提交的信息"
    )
    
    ; 更新状态栏
    sb.Text := "最后提交于: " A_Now
}

; 当窗口关闭时退出脚本
MyGui.OnEvent("Close", (*) => ExitApp())