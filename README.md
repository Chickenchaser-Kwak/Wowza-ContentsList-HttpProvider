# WowzaContentsListHttpProvider
HttpProvider for WowzaEngine to list all mp4 files in contents folder

= How To Use =
1. Open project using Eclipse with Wowza IDE.
2. Configure build.xml file if you install WowzaEngine at a different path.
3. Compile and build. It will automatically generate a jar file(ContentsListProvider.jar) under {WowzaInstallDir}/lib folder.

4. Add below lines to {WowzaInstallDir}/conf/VHost.xml.

```xml
<HTTPProvider>
  <BaseClass>com.chasersgroup.chickenchaser.wowza.ContentsListHTTPProvider</BaseClass>
  <RequestFilters>contentslist*</RequestFilters>
  <AuthenticationMethod>none</AuthenticationMethod>
</HTTPProvider>
```
You probably insert the code under HostPort 8086 section.


4. Restart Wowza Server.

5. test by sending http request to http://{Your Wowza Server IP}:8086/contentslist.
Then you can get a http response whici is listing all .mp4 files in {WowzaInstallDir}/contents like below : 

```json
[{"path":"testStream","lastModified":1508144685235}, 
{"path":"testStream","lastModified":1508142934912}, 
{"path":"testStream","lastModified":1508142976607}]
```

= Reference =

https://www.wowza.com/docs/how-to-create-an-http-provider
