# coding=utf-8
from flask import Flask, abort, request
from flaskext.mysql import MySQL
import redis
import requests
import json
# 自作api.pyをロード
import api

app = Flask(__name__)
mysql = MySQL()

app.config['MYSQL_DATABASE_USER'] = 'root'
app.config['MYSQL_DATABASE_PASSWORD'] = 'kdrl'
app.config['MYSQL_DATABASE_DB'] = 'wearthistoday'

mysql.init_app(app)

r = redis.StrictRedis(host='localhost', port=6379, db=0)

# "/"に入ってくる時
@app.route('/')
def basic_response():
    return "Wear This TodayサービスのAPIサーバーです。詳細はhttps://github.com/kdrl/Wear-This-Today より確認ください。\n"

# "/api/echo"にPOSTでrequestが来たら以下が発動
@app.route('/api/test/echo', methods=['POST'])
def testEcho():
    # もしももらったデータがjsonでなかったら、400を返す。(400 Bad Request)
    if not request.json:
        abort(400)
    # もらったJSONをサーバで表示(debug用)
    print(request.json)
    # JSONを返す。
    return json.dumps(request.json)

@app.route('/api/test/helloworld', methods=['POST'])
def testHelloWorld():
    # もしももらったデータがjsonでなかったり、numをキーとする値を持ってなかったら、400を返す。(400 Bad Request)
    if not request.json["num"]:
        abort(400)
    # もらったJSONをサーバで表示(debug用)
    print(request.json)
    # api.helloworldを用いて処理した値を返す
    result = api.helloWorld(request.json["num"])
    return result

@app.route('/api/test/load', methods=['GET'])
def testLoad():
    connection = mysql.connect()
    cursor = connection.cursor()
    cursor.execute('''select * from test''')
    result = cursor.fetchall()
    return json.dumps(result)

@app.route('/api/test/save', methods=['POST'])
def testSave():
    # もしももらったデータがjsonでなかったら、400を返す。(400 Bad Request)
    if not request.json:
        abort(400)
    connection = mysql.connect()
    cursor = connection.cursor()
    query = '''insert into test (content) values (" ''' + str(request.json["content"]) + ''' ")'''
    cursor.execute(query)
    connection.commit()

    return "OK"

@app.route('/api/test/signin', methods=['POST'])
def testSignin():
    r.setex(str(request.json["id"]),10,True)
    return "ok"

@app.route('/api/test/checklogin', methods=['POST'])
def testCheckLogin():
    result = r.get(str(request.json["id"]))
    if not result:
        result = "not authorized"
    else:
        result = "authorized"
    return result

@app.route('/api/test/request', methods=['GET'])
def testRequest():
    return requests.get('http://wearthistoday.monotas.com').content

if __name__ == '__main__':
    # run application with debug mode
    app.run(port=3000, host='0.0.0.0', debug=True)