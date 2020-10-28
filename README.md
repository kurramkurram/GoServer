# GoServer
Loacl WebServer and Android App
Loacl WebServer is accessed by Android App.
Android app upload text file to local webserver and request text file from local webserver.

# Getting Started
1. clone repository
```
$https://github.com/kurramkurram/GoServer.git
```
2. start local webserver 
```
$go run main.go
```
3. Execute the following command to get the ip address of loacl webserver 
```
$ipconfig
```
4. Android Studio File -> Open
5. Run

## upload file
1. Set ip address on android app
2. Set file name on android app
3. Set file contents on android app
4. To create select [create] button on android app
5. To upload file, select [connect] buton on android app
6. If you get toast that is [Upload Success], uploading file is success

## download file
1. Create a text file in the same directory as main.go on local webserver.
2. Set file name for download on android app
3. To download file from local webserver, select [download] button
4. If you get toast that is [Download Success], download is success
5. You can access the file by [android studio](https://developer.android.com/studio/debug/device-file-explorer).
