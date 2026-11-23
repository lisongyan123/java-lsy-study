#Requires AutoHotkey v2.0
#SingleInstance Force
Persistent()
helper := A_ScriptDir "\watch-chat-buttons.ps1"
Run('powershell.exe -NoProfile -ExecutionPolicy Bypass -WindowStyle Hidden -File "' helper '" -Owner ' DllCall("GetCurrentProcessId"), , "Hide", &worker)
OnExit((*) => ProcessExist(worker) && ProcessClose(worker))
