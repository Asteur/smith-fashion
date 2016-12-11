# coding=utf-8
from flask import Flask
'''
    db setting
    以下のようにDBを読み込むことが可能
'''
from db.extension import mysql
'''
    register api
    変数の名前が重なることで正しく読み込まれない問題があった
    as を用いて回避する
'''
from api.test import test as testApi

app = Flask(__name__)
'''
    MySQL configurations
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

'''
    "/"に入ってくる時
'''
@app.route('/')
def basic_response():
    return "Wear This TodayサービスのAPIサーバーです。詳細はhttps://github.com/kdrl/Wear-This-Today より確認ください。\n"

if __name__ == '__main__':
    # run application with debug mode
    app.run(port=3000, host='0.0.0.0', debug=True)