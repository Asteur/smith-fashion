#!/usr/bin/env python
# -*- coding: utf-8 -*-

'''
    api/test.py
    ここにテスト用のapiを作成してテストを行う
'''
from flask import Blueprint, request, abort, Response
import json
import requests
from db.extension import mysql, redis
'''
    画像処理
'''
import re
import os
import base64
from io import BytesIO
from PIL import Image
import sys
import numpy as np
import subprocess
'''
    testというBlueprintを生成
    これをmain.pyで読み込んでapiとして登録
'''
test = Blueprint('test', __name__)
'''
    以下でTEST用のapiが続く
'''
@test.route('/echo', methods=['POST'])
def this_echo():
    # もしももらったデータがjsonでなかったら、400を返す。(400 Bad Request)
    if not request.json:
        abort(400)
    # もらったJSONをサーバで表示(debug用)
    print(request.json)
    # JSONを返す。
    return json.dumps(request.json)

@test.route('/helloworld', methods=['POST'])
def this_helloworld():
    if not request.json["num"]:
        abort(400)
    print(request.json)
    result = ""
    for i in range(request.json["num"]):
        result += "helloworld\n"
    return result

@test.route('/load', methods=['GET'])
def this_load():
    # MySQLに連結
    connection = mysql.connect()
    # カーサーを持ってくる(このカーサを用いてQueryを動作して結果を持ってきたりする)
    cursor = connection.cursor()
    # 以下の命令がQueryを動作させること
    cursor.execute('''select * from test''')
    # cursor.descriptionには持ってきたデータのColumnの情報がTuple形式で入っている
    columns = cursor.description
    result = []
    # cursor.fetchall()の返り値には各行のデータがTuple形式で入っている
    # それを以下の処理でJSON化できる
    for value in cursor.fetchall():
        tmp = {}
        for (index, column) in enumerate(value):
            tmp[columns[index][0]] = column
        result.append(tmp)
    # resultはJSONである。それを最後に返す時は文字列として返す
    return str(result)

@test.route('/save', methods=['POST'])
def this_save():
    if not request.json:
        abort(400)
    # MySQLにつなく
    connection = mysql.connect()
    cursor = connection.cursor()
    # 以下ではデータのinsertをしている。
    query = '''insert into test (content) values (" ''' + str(request.json["content"]) + ''' ")'''
    cursor.execute(query)
    # insertなど、新たに何かを入れるとか、updateするときには以下のcommit()を実行する必要があるらしい
    connection.commit()

    return "OK"

@test.route('/registerredis', methods=['POST'])
def this_registerredis():
    # Redisに30秒間あるデータを保存させる命令
    # RedisはKeyとValueの関係を保存するcache DB
    redis.setex(str(request.json["token"]),30,True)
    return "ok"

@test.route('/checkregisterredis', methods=['POST'])
def this_checkregisterredis():
    # もらった "token"をキーとするデータがあればresultにはその値が入って、そうでなけれは空になる
    result = redis.get(str(request.json["token"]))
    if not result:
        result = "not authorized"
    else:
        result = "authorized"
    return result

@test.route('/request', methods=['GET'])
def this_request():
    # requestsはGET命令とかPOSTとかを出すことができる
    # googleで試してみました
    return requests.get('http://www.google.com').text

'''
    以下ではClientでログインができており、
    POSTで必ず正しいtokenを送るときにのみ、
    正しい返り値を持つapiが続く
'''
# ユーザのDB上のすべての情報をJSONで返す
@test.route('/alldata', methods=['POST'])
def this_alldata():
    # まずはtokenをもらう
    token = str(request.json["token"])
    # tokenがそもそもなかったら間違っているので400を返す
    if not token:
        abort(400)
    # redisにこのtokenをキーとして持つデータがあるかどうかを確認する
    user_id = redis.get(token)
    # もしもらったtokenをKetとして持つデータがなかったら400を返す
    if not user_id:
        abort(400)
    # さて、あったのなら、それをutf-8でエンコードすることで正しい文字列に戻す
    user_id = user_id.decode("utf-8")
    # さて、user/idが求まったので、これを用いて、MySQLで情報を取ってくる
    connection = mysql.connect()
    cursor = connection.cursor()
    query = 'select * from user where id=' + user_id
    cursor.execute(query)
    columns = cursor.description
    result = []
    for value in cursor.fetchall():
        tmp = {}
        for (index, column) in enumerate(value):
            tmp[columns[index][0]] = column
        result.append(tmp)
    # この時点でresultには送られてきたtokenに対応するユーザのMySQL上のすべてのデータをJSONで持つ
    # 最後に文字列として返す
    return str(result)

# 送られてきたlat,lonの気温を返す
@test.route('/weather', methods=['POST'])
def this_weather():
    # まずはtoken,lat,lngをもらう
    token = str(request.json["token"])
    lat = str(request.json["lat"])
    lon = str(request.json["lon"])
    # データがそもそもなかったら間違っているので400を返す
    if not (token or lat or lon):
        abort(400)
    # redisにこのtokenをキーとして持つデータがあるかどうかを確認する
    user_id = redis.get(token)
    # もしもらったtokenをKetとして持つデータがなかったら400を返す
    if not user_id:
        abort(400)
    # パラメータを作成
    params = {
        "cnt": 1,
        "lat": lat,
        "lon": lon,
        "APPID": "c29457b7d3cadfecf035898bad66d918"
    }
    url = 'http://api.openweathermap.org/data/2.5/find'
    # 返ってくるJSONを処理
    response_weather = requests.get(url, params=params)
    response_weather = json.loads(response_weather.text)
    # 以下のresponse_weather_tmpは気温だけを取り出した変数
    # response_weather_tmp = response_weather["list"][0]["main"]["temp"]

    return str(response_weather)

# 送られてきた画像(base64)をjpgに変換し、main.luaでstylenetを介して特徴ベクトルに変換してサーバ側でconsole出力
@test.route('/image', methods=['POST'])
def this_image():
    data = str(request.json["image"])
    data = re.sub('^data:image/.+;base64,', '', data)
    image_name_path = "./tmp/test.jpg"
    im = Image.open(BytesIO(base64.b64decode(data)))
    im.save(image_name_path, "JPEG")

    # extract fashion features and format these features
    result = subprocess.check_output(["th" ,"./api/main.lua", image_name_path]).decode("utf-8")
    print (result)
    result = result.split('\n')
    print (result)
    fashion_feature = subprocess.check_output(["th" ,"./api/main.lua", image_name_path]).split('\n')[:-3]
    escape = re.compile(r'\x1b\[\?\d+h')
    fashion_feature = list([escape.sub('', x) for x in fashion_feature])
    fashion_feature = np.array(fashion_feature).astype(np.float32).reshape(1, 128)

    return str(fashion_feature)

@test.route('/recommand', methods=['POST'])
def this_recommand():
    res = {
        "result" : [
            {
                "index" : 0,
                "kakkoii" : 1,
                "kawaii" : 2,
                "formal" : 3,
                "casual" : 4
            },{
                "index" : 1,
                "kakkoii" : 1,
                "kawaii" : 2,
                "formal" : 3,
                "casual" : 4
            },{
                "index" : 2,
                "kakkoii" : 1,
                "kawaii" : 2,
                "formal" : 3,
                "casual" : 4
            }
        ]
    }
    return Response(json.dumps(res), status=200, mimetype='application/json')


