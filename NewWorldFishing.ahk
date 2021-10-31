F5::
	Run,%A_ScriptDir%\NewWorldFishingBot.exe
return

F6::
	run, %comspec% /c wmic path win32_process Where "Caption Like '`%javaw.exe`%' AND CommandLine Like '`%NewWorldFishingBot`%'" Call Terminate
return

F7::
	run, %comspec% /c wmic path win32_process Where "Caption Like '`%javaw.exe`%' AND CommandLine Like '`%NewWorldFishingBot`%'" Call Terminate
	sleep,3000
	Run,%A_ScriptDir%\NewWorldFishingBot.exe
return