# Back-end Application

## APIサーバーの仕様

<table>
<tr>
<th>url</th>
<th>method</th>
<th>Need token?</th>
<th>required data(送るべきのデータの構造）</th>
<th>response</th>
<th>explanation(説明)</th>
</tr>

<tr>
<td>/</td>
<td>GET</td>
<td>No</td>
<td>None</td>
<td>Wear This TodayサービスのAPIサーバーです。詳細はhttps://github.com/kdrl/Wear-This-Today より確認ください。</td>
<td>一番簡単なことをしていて、GET命令が正しく処理されているかを確認。サーバの起動確認などのためにも活用可能。</td>
</tr>

<tr>
<td>/api/test/echo</td>
<td>POST</td>
<td>No</td>
<td>JSON形式のデータ</td>
<td>JSON形式のデータ(送ったデータが帰ってくる)</td>
<td>POSTでJSONデータを正しく送信しているかの確認。</td>
</tr>

<tr>
<td>/api/test/helloworld</td>
<td>POST</td>
<td>No</td>
<td>
{ "num": 2(定数) }
</td>
<td>
hello world!<br>
hello world!
</td>
<td>
簡単な動作ではあるが、http POSTで何らかの動作をサーバにさせることの確認。
</td>
</tr>

<tr>
<td>/api/test/save</td>
<td>POST</td>
<td>No</td>
<td>
{"content" : 文字列}
</td>
<td>
"OK"
</td>
<td>
MySQLとの連携確認。contentを持ったJSONを送ると、それをDBのtest tableに保存。idは自動で番号が振られてMySQLに保存される。
</td>
</tr>

<tr>
<td>/api/test/load</td>
<td>GET</td>
<td>No</td>
<td>
None
</td>
<td>
wearthistoday DBの test テーブルのすべてのデータがJSONの配列として返ってくる
</td>
<td>
MySQLからデータをJSONの配列で取ってくることの確認。
</td>
</tr>

<tr>
<td>/api/test/registerredis</td>
<td>POST</td>
<td>No</td>
<td>
{ "id": 文字列 }
</td>
<td>
ok
</td>
<td>
Redisとの連携の確認。30秒かん送ってきたidをRedis上に保管する。
</td>
</tr>

<tr>
<td>/api/test/checkregisterredis</td>
<td>POST</td>
<td>No</td>
<td>
{ "id": 文字列 }
</td>
<td>
"authorized" or "not authorized"
</td>
<td>
先ほど/api/test/registerredisで送ったidはRedis上で30秒間保存される。保存されている時にこのcheckregisterredisが動作したらauthorizedが返ってくる。
</td>
</tr>

<tr>
<td>/api/test/request</td>
<td>GET</td>
<td>No</td>
<td>
None
</td>
<td>
Wear This TodayサービスのAPIサーバーです。詳細はhttps://github.com/kdrl/Wear-This-Today より確認ください。
</td>
<td>
ここではサーバの中でGETを発動し、"/"のresponseをもらってきて、それを返している。<br>
つまり、 Client ←→ Server "/api/test/request" ←→ Server "/" の様な形。
</td>
</tr>

<tr>
<td>/api/test/alldata</td>
<td>POST</td>
<td>Yes</td>
<td>
{ "token" : facebook_token }
</td>
<td>
MySQLのuser table上のtokenのユーザに該当するデータ全てがJSONの配列で帰ってくる
</td>
<td>
1. ClientからtokenとともにPOST request。<br>
2-1. ServerでこのtokenのユーザのidをRedisから取ってくる。<br>
2-2. もし、ここで、何らかのエラーが生じると、400が帰ってくる。<br>
3. 正しくRedisからidが取れたら、MySQLのuser tableでidの行の情報を取ってきて、JSONの文字列としてreturnする。<br><br>
tokenが必要となるapiの基本例として作成した。
</td>
</tr>

<tr>
<td>/api/signin</td>
<td>POST</td>
<td>Yes</td>
<td>
{ "token" : facebook_token }
</td>
<td>
サーバでの処理内容が文字列で帰ってくる。(ex. "login success." or "your are a new user.  sign up success. login success.")など
</td>
<td>
1. ClientからtokenとともにPOST request。<br>
2-1. tokenがなかったら400とともにエラーメッセージが帰ってくる。<br>
2-2. tokenをもらったらそれをfacebookを介して確認<br>
2-2-1. tokenが正しくなかったら400とともにエラーメッセージが帰ってくる。<br>
2-2-2. tokenが正しかったらRedis上にtokenとidのペアで情報を保存<br>
3. userのidですでに保存されている情報がMySQLのuserテーブルにあるかどうかの確認。<br>
3-1. なかったのなら、新たに行を追加してidとnameを保存。<br>
3-2. あったのなら、何もせず、4に進む。
4. 正しく、signin処理が終わったので200とともにメッセージを返す。
</td>
</tr>
</table>

## データベース
### RDBMS
MySQL
### Cache DB
Redis
### MySQL Install
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

### MySQL Backup
#### mysqldump
1. $ mysqldump -u[userId] -p[password] --all-databases > dump.sql  
2. $ mysqldump -u[userId] -p[password] --databases [DB] > dump.sql 
3. $ mysqldump -u[userId] -p[password] [DB] [Table]

### MySQL restoration
#### mysqldump
mysql -u [userId] -p [password] [DB] < dump.sql

### Redis Install
<pre>
Wear-This-Today admin$ brew install redis
==> Downloading https://homebrew.bintray.com/bottles/redis-3.2.6.sierra.bottle.t
######################################################################## 100.0%
==> Pouring redis-3.2.6.sierra.bottle.tar.gz
==> Caveats
To have launchd start redis now and restart at login:
  brew services start redis
Or, if you don't want/need a background service you can just run:
  redis-server /usr/local/etc/redis.conf
==> Summary
🍺  /usr/local/Cellar/redis/3.2.6: 11 files, 1.7M
</pre>

### References
#### HomeBrew
http://qiita.com/nori-k/items/f29481b5d65597e89552
#### MySQL
https://github.com/helloheesu/SecretlyGreatly/wiki/%EB%A7%A5%EC%97%90%EC%84%9C-mysql-%EC%84%A4%EC%B9%98-%ED%9B%84-%ED%99%98%EA%B2%BD%EC%84%A4%EC%A0%95%ED%95%98%EA%B8%B0
http://dimdim.tistory.com/entry/MySQL-%EB%B0%B1%EC%97%85-%EB%B0%8F-%EB%B3%B5%EA%B5%AC
