CocosStudio新建项目的目录结构
--Classes
--cocosstudio
--proj.android
--pro.ios_mac
--proj.win32
--Resource
--.cocos-project.json
--config.json
--xxxx.ccs
--xxxx.cfg
--xxxx.udf
=========================================================

参考该目录结构，完成以下逻辑：
1.把Resource目录下的资源拷贝到cocosstudio目录下
2.在cocosstudio下有一csb目录，里面存放着所有的csb文件。子目录按模块划分。从svn上更新cocosstudio。【只有csb目录会被提交到svn上，其余不会。】
3.generateCCS.bat会运行最新的ccs文件。

