# Wear-This-Today
HACK U 2016

## Android Front-End
Androidでのフロントエンドアプリです。
## 各Classについての説明
### StartActivity.java
![ログイン画面](/image/image1.png)
ログイン画面のClass

レイアウトはactivity_startと紐づけられている。

#### onCreate()
これが初期起動時に読み込まれる。もし、tokenがあるなら、Intetnの遷移によりMainActivity.javaに移る。

もし、tokenがないなら、そのまま、いつものようにonCreateが終了する。

#### public void login(View v)
ログインボタンが押された際に呼び出される。

![FaceBookのログイン画面](/image/image2.png)
ここでは、リスナーでFaceBookログインを紐づけており、成功した場合はtokenを保存し、tokenを送信する。そして、Intentの遷移により、MainActivityへ移る。ここではリスナーの登録をしているだけであって、実際にログインが完了した際にこれらのリスナーが、onActivityResultで呼び出される。
### MainActivity
![メイン画面](/image/image3.png)
ログインがかんりょした際にこの画面に映る。レイアウトはactivity_mainに紐づけられている。

#### onCreate()
基本的にはここはあまり重要ではない。通信部分が残っているが、ここは消します。

#### register()
ここで服の登録を行う。これを実行する(登録ボタンを押す)と、カメラが起動し、そこから登録フォームが起動。
終了すると、このActivitiyに戻ってくる。

#### closet()
このボタンを押すと、今まで登録してきた服の一覧を見ることができる。

#### logOut()
ログアウト用ボタン、メソッド。これが押されると、tokenがnullになる。そして、ログアウト用のコールバックが実行され、ログアウト。元のStartActivityに戻る。

#### initial_register()
ユーザー情報登録用フォーム起動ボタン、メソッド。ここを押すと、ユーザー情報を登録するフォームに飛ぶ。終わればこのActivitiyに戻る。

#### onActivityResult()
ここはあまりいじらないでください。カメラ画面が終了すると呼び出されます。

### FormActivity
服登録Activity

![服情報登録画面](/image/image4.png)
#### onCreate()
ここでAcitivityのコンテンツとの紐づけを行なっている。また、カメラからの画像の読み込みを行なっており、realmを使用するための初期化も行なっている。

#### register()
ここで、フォームの情報をRealmのデータベースに登録し、サーバーにも送信します。
終了次第、MainActivityへ戻ります。

### ListActivity

![服一覧画面](/image/image5.png)
服一覧を確認できます。
ここで服の削除も可能です。

内部でAdapterなどのクラスが実装されていますが、ここら辺はあまり触らないでください。

#### onCreate()

ここでListViewの設定とかしてます。あまり弄らないでください...
```
mListView.setOnItemLongClickListener
```
というところで、長押ししたら削除するよう設定してます。また、ここでも、何を削除したのかを送信するようにしてます。
### RegisterActivity
元は前野くんが作ったものMainActivityへの復帰はIntentではなくてfinish()に変えました。

![ユーザー情報登録画面](/image/image6.png)
#### onCreate()
ここら辺でコンポーネントの紐づけと、とかをやってる。

#### Register()
ここで、フォームの情報を記憶させて、終了している。

### RecommendActivity
ここは今作業中。

## 今実装されているActivity
### MainActivity
これが最初に起動されるようになっています。
#### public void register(View v)
ここからカメラ画面を起動して、カメラを撮影します。

終了した際、またMainActivityに一旦戻り、FormActivityへと明示的Intentによって移動します。

##### Extra (暫定)
```
Bitmap caputerdImage
```
ここに撮った画像が収納されています。KeyはImageです。

### FormActivity
ここで服を登録します。
登録するパラメータなんかはあとでなんとでもなるので、とりあえず、

* 色
* 服の種類

だけでも作りましょう。(前野担当分)

spinnerに値をsetするにはres/values内にxmlを作ってそこに書いてやるといいはず。

#### その他のデータ
* 緯度経度 : 小島がやります。(Permission関連はやっときました。)
* 画像 : とりあえずPNGに圧縮してbyte配列にはしました。

#### 通信用ライブラリ
Volleyを使っています。

ここではPOSTを使ってますが、同じ要領でGETもできます。

## 今実装されているクラス(Not Activity)

### RequestSingleton
参考書をみて書きました。そこまで深く考えなくてもいいです。

## 今後実装すべきActivity

### ClothesListActivity

服一覧がわかるアクティビティ(画面)

ここは、通信でデータを取るか、RealmとかActiveAndroidで中にデータを持っておくかは未定

#### 表示方法
ListView

##### ListViewのカスタム

* 服の写真
* 服の種類
etc...(その他全て文字データ(TextView))

### Recommend Activity
ここで、服をリコメンドします。

#### 送る情報

##### リコメンド前

* 位置データ
* (予定?)

##### リコメンド後

* 実際に着たかどうか
* リコメンドに対する評価

## Jsonによる送受信

### 登録時 (POST)

#### 送り値

| Key        | Value       | Type         |
|:-----------|:------------|:-------------|
| Username   |  ユーザー名   |     String   |
| gender     |      性別    |    Int(0 : 女性 1 : 男性)   |
| Color    |        色      |    String     |
| type         |      服の種類 |     String     |
| Image      |    服の写真   |    byte[]    |

#### 帰り値

jsonで成功した!みたいなデータ出してくれるといいです。

### 画像の圧縮方法について

PNGで圧縮してます!! 多分ここミスるとやばいことになります...

### リコメンド (GET)

ここで位置情報とユーザー名を送ります。

#### 送りの際の値

| Key        | Value       | Type         |
|:-----------|:------------|:-------------|
| Username   |  ユーザー名   |     String   |
| gender     |      性別    |    Int(0 : 女性 1 : 男性)   |
| latitude     |      緯度    |    double  |
|   longitude  |      経度    |    double |
#### 帰り値(これが欲しい...)

| Key        | Value       | Type         |
|:-----------|:------------|:-------------|
| Image      |    服の写真   |    byte[]    |
| Color      |  色         |     String   |
| type       |     服の種類 |     String   |

こちらで検索かけます...

#### 気に入った場合(POST)

| Key        | Value       | Type         |
|:-----------|:------------|:-------------|
| Username   |  ユーザー名   |     String   |
| gender     |      性別    |    Int(0 : 女性 1 : 男性)   |
| Image      |    服の写真   |    byte[]    |
| Color      |  色         |     String   |
| type       |     服の種類 |     String   |
#### 気に入らなかった場合(POST)

| Key        | Value       | Type         |
|:-----------|:------------|:-------------|
| Username   |  ユーザー名   |     String   |
| gender     |      性別    |    Int(0 : 女性 1 : 男性)   |
| Image      |    服の写真   |    byte[]    |
| Color      |  色         |     String   |
| type       |     服の種類 |     String   |
