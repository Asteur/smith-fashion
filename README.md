# 製品概要
自動でどの服を着るかをお勧めしてくれるAIによるファッションコーディネーションサービス

# 背景（製品開発のきっかけ、課題等）
私たちは毎日その日着る服を決めなければいけません。服を選ぶときには自分の好みも大事ですが、その日の天気、予定、気温などいろんなことを考えなくてはなりません。さらに、服のセンスが悪いと周りから変な目線で見られるかもしれません。とても面倒です。そういった時に「誰かが自分の服をコディネーションしてくれたらいいな」と思います。私たちが開発したサービス「Wear This Today」はこれを解決します。

# 製品説明（具体的な製品の説明）
私たちが開発したサービス「Wear This Today」は、今日着ていく服を人工知能が自動で組み合わせて選んでくれるので、服を選ぶ手間を省くことができます。

まず、写真と、フォームによる入力で、「クローゼット」内にアイテムを登録します。これによって、今、自分が持っている服を登録します。このアイテムは、アプリ内で確認可能です。

そして、Recommendボタンを押すと、その日の気温にぴったりの、そしてファッションセンス的にも最適な服のチョイスをあなたに提案します。あなたはもちろん、提案された服を着てもいいですし、それが気にいらないのであれば、何を着るのかを教えることで、この数日間何を着ていたのかを記憶し、明日の服の選択に役立てます。これであなたは、毎日心地よく、そしてイケてるファッションライフを満喫できます。

# 特長
1. 「クローゼット」に登録したアイテムを自動で組み合わせ毎日自分で悩む必要がなく、自分が持っている服の中から、AIが今日のあなたのファッションをコーディネートして提案してくれます。

2. その日の季節・天気や気温に合わせたコーディネートをしてくれます。

3. 「クローゼット」の内容を登録してあるので、自分の持っていないファッションの服が何かわかり購入しやすくなります。

# 解決出来ること
・毎日、着る服を選ぶのがめんどくさい人はすぐに着る服を決めることができ、時間短縮できる。
・ファッションセンスに自信がない人も、この製品を使うことでそれなりの服装で出かけることができる。
・環境（気温など）にあった服を着ることができる。
・クローゼットの中身がわかる

# 今後の展望
* 持っている服からの新商品のレコメンド

あなたの持っている服から、新しい服をご紹介します。これで、あなたのコーディネート力は抜群に!

* 服からヘアースタイルの提案

あなたのファッションに合わせてヘアースタイル、ヘアカラーなどもお勧めします!

*

# 注力したこと（こだわり等）
1. ユーザが手軽に利用出来るサービスを作ること
2. 機械学習を用いたAIのインテリ化
3. 使いやすさを意識したインターフェース
4. FaceBook, Googleなどの外部サービスを利用したセキュアな認証

# 開発技術

## Front-end
Android (Android Studio + Java)

### ライブラリ
Volley<br>Realm(2.2.1)<br>(OpenCV)

### API
Google, FaceBookなどのOAuth API

### 諸技術
LocationManagerによる位置情報

## Back-end

### 言語
Python, SQL

### ライブラリ
Flask<br>flask-mysql<br>Numpy<br>Scipy<br>Pandas

### データベース
MySQL

# 参考論文
1. Fashion Style in 128 Floats
2. Clothes Segmentation

# 期間中に開発した独自機能・技術
1. ファッションのクラスタリング
2. ファッションの評価値と気温と着衣履歴を考慮して服の組み合わせをお勧めする機能
