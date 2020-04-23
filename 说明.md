#git生成周报
##必要结构
###运行文件：generateWeekReport.exe
###配置文件：config/config

####config参数说明：

 - `root`为git程序目录的父目录，多个git程序如果在不同目录，使用英文逗号分隔【结尾不用斜杠】
 - `direct`各个项目的根目录，前后无需加斜杠，多个项目用英文逗号分隔
 - `out`周报生成目录
 - `names`各个项目对应的中文名，用英文逗号分隔，与direct长度一致
 - `myname`我的名字
 - `career`职位名称
 - `start` 开始时间（可不写，默认本周一）
 - `end` 结束时间（可不写，默认生成日当天）
 - `from`  自己邮箱地址
 - `pwd` 自己邮箱密码
 - `to` 发送给对方的邮箱地址，前期测试建议加自己地址

格式一定要为a=b，不要有空格

####使用说明：
1. 按照上述说明配置周报内容
2. 运行generateWeekReport.exe
3. 选择需要的项目，可选多个，输入如：ab
4. 回车，直到出现---end---
5. 其他：如果遇到报错，请首先检查配置，如非配置问题，请联系作者


####扩展说明：
1. 注意config文件的位置（生成后相对位置变化，注意在生成的exe文件外部加一个config/config文件）
1. 自己生成请先在eclipse打包jar包，再用exe4j打包成exe文件，注意把所有libs文件夹下内容配置给exe4j
1. exe4j参考下载地址[https://exe4j.apponic.com](https://exe4j.apponic.com "https://exe4j.apponic.com")
1. exe4j首次使用后可保存配置，无需再次配置

### exe4j此项目配置步骤
1.  Choose project type 选jar in exe mode
1.  Application info写的是生成程序名和路径
1.  Executable info 选Console application,后面的勾选fail if,change working
1.  注意下面有个32，64位选择，根据系统位数选
1.  manifest option 两个选项选1和3
1.  java invocation把项目下libs所有jar包以及生成的jar包都打包加进去，有的人用不能弹框，把jar包完整路径放入
1.  JRE 按系统实际情况进行选择
1.  perferred vm用默认
1.  选9生成就可以了
1.  生成后，请拷贝项目下配置好的config目录，到exe文件所在目录

### 这个项目就为了自己用的，没仔细调整结构，此处用作备用
