# Wear-This-Today
HACK U 2016

## Android Front-End
Androidでのフロントエンドアプリです。

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
