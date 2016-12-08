# Back-end Application

## APIサーバーの仕様
<table>
<tr>
<th>url</th>
<th>method</th>
<th>data形式</th>
<th>response</th>
</tr>
<tr>
<td>/</td>
<td>GET</td>
<td>None</td>
<td>Wear This TodayサービスのAPIサーバーです。詳細はhttps://github.com/kdrl/Wear-This-Today より確認ください。</td>
</tr>
<tr>
<td>/api/echo</td>
<td>POST</td>
<td>JSON形式のデータ</td>
<td>JSON形式のデータ(送ったデータが帰ってくる)</td>
</tr>
<tr>
<td>/api/helloworld</td>
<td>POST</td>
<td>
{ "num": 2(定数) }
</td>
<td>
hello world!<br>
hello world!
</td>
</tr>
<tr>
<td>/api/savetest</td>
<td>POST</td>
<td>
any JSON data
</td>
<td>
"saved" or "error"
</td>
</tr>
<tr>
<td>/api/loadtest</td>
<td>GET</td>
<td>
None
</td>
<td>
savetestで保存されたデータの中で一番最新のものが返ってくる
</td>
</tr>
</table>

# データベース
mysql
## Install
<pre>
Wear-This-Today admin$ brew install mysql
Warning: You are using a pre-release version of Xcode.
You may encounter build failures or other breakages.
Please create pull-requests instead of filing issues.
==> Downloading https://homebrew.bintray.com/bottles/mysql-5.7.15.el_capitan.bot
######################################################################## 100.0%
==> Pouring mysql-5.7.15.el_capitan.bottle.tar.gz
==> Using the sandbox
==> /usr/local/Cellar/mysql/5.7.15/bin/mysqld --initialize-insecure --user=admin
==> Caveats
We've installed your MySQL database without a root password. To secure it run:
    mysql_secure_installation

To connect run:
    mysql -uroot

To have launchd start mysql now and restart at login:
  brew services start mysql
Or, if you don't want/need a background service you can just run:
  mysql.server start
==> Summary
🍺  /usr/local/Cellar/mysql/5.7.15: 13,510 files, 445.9M
</pre>
