# -*- coding: utf-8 -*-
'''
    main.py
    Back-end applicationの中心
    python main.py より起動
    前提条件
    1. MySQLが起動できていること
        1.1 dump.sqlでDBの骨子を保存しといた
        1.2 dump.sqlを適用する方法はREADME.mdを参照
    2. Redisが起動できていること
'''
from flask import Flask
'''
    db setting
    以下のようにDBを読み込むことが可能
    Redisもモナ軸読み込むことが可能であるが、このmain.pyではまだ、必要なかったので読んでない
'''
from db.extension import mysql
'''
    APIを読み込む
    変数の名前が重なることで正しく読み込まれない問題があったので as を用いて別名をつけることで解決
'''
from api.test import test as testApi
from api.signin import signin as signinApi
from api.recommand import recommand as recommandApi
'''
    Flaskアプリケーション
'''
app = Flask(__name__)
'''
    MySQL configurations
    DB接続設定
'''
app.config['MYSQL_DATABASE_USER'] = 'root'
app.config['MYSQL_DATABASE_PASSWORD'] = 'kdrl'
app.config['MYSQL_DATABASE_DB'] = 'wearthistoday'
mysql.init_app(app)
'''
    register blueprint
    APIを登録する
'''
app.register_blueprint(testApi, url_prefix='/api/test')
app.register_blueprint(signinApi, url_prefix='/api')
app.register_blueprint(recommandApi, url_prefix='/api')
'''
    "/"に入ってくる時
'''
@app.route('/')
def basic_response():
    return "Wear This TodayサービスのAPIサーバーです。詳細はhttps://github.com/kdrl/Wear-This-Today より確認ください。\n"

'''
    サーバー起動
'''
if __name__ == '__main__':

    # run application with debug mode
    app.run(port=3000, host='0.0.0.0', debug=True)