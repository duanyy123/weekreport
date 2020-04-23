D:
cd D:\program\backcontrol\VanwardService
for /F %%i in ('git config --get user.name') do ( set name=%%i)
git log --author=%name% --since ==2020-01-13 --until=2020-01-17 > D:\program\eclipse\work\DEST\config\VanwardService.txt
D:
cd D:\program\backcontrol\o2oGitProject
for /F %%i in ('git config --get user.name') do ( set name=%%i)
git log --author=%name% --since ==2020-01-13 --until=2020-01-17 > D:\program\eclipse\work\DEST\config\o2oGitProject.txt
D:
cd D:\program\backcontrol\yunding
for /F %%i in ('git config --get user.name') do ( set name=%%i)
git log --author=%name% --since ==2020-01-13 --until=2020-01-17 > D:\program\eclipse\work\DEST\config\yunding.txt
D:
cd D:\program\android\as\android_engineer
for /F %%i in ('git config --get user.name') do ( set name=%%i)
git log --author=%name% --since ==2020-01-13 --until=2020-01-17 > D:\program\eclipse\work\DEST\config\android_engineer.txt
D:
cd D:\program\backcontrol\vwzt-o2o
for /F %%i in ('git config --get user.name') do ( set name=%%i)
git log --author=%name% --since ==2020-01-13 --until=2020-01-17 > D:\program\eclipse\work\DEST\config\vwzt-o2o.txt
