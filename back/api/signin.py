# -*- coding: utf-8 -*-
'''
    api/signin.py
    ここにログイン・会員登録を行う処理を書く
'''
from flask import Blueprint, Response, request, abort
import json
import requests
from db.extension import mysql, redis
'''
    signinというBlueprintを生成
    これをmain.pyで読み込んでapiとして登録
'''
signin = Blueprint('signin', __name__)
# /api/signin にPOSTがきたら以下が動作する
@signin.route('/signin', methods=['POST'])
def this_signin():
    res = {
        "message" : ""
    }
    # まず、tokenをとる
    token = str(request.json["token"])
    if not token:
        # tokenがないっということはClient側で正しくtokenを渡していないこと
        res["message"] = "you need token"
        return Response(json.dumps(res), status=400, mimetype='application/json')
    # もらったtokenが正しいtokenかをfacebookを介して確認する
    params = {
        "access_token": token
    }
    url = 'https://graph.facebook.com/me'
    # facebookから返ってくるjson形式の回答をresponse_facebookに保存
    response_facebook = requests.get(url, params=params)
    response_facebook = json.loads(response_facebook.text)
    # 正しかったらJSONは{"id" : user_id, "name" : user_name}のような形で返ってくる
    user_id = response_facebook["id"]
    user_name = response_facebook["name"]
    if not user_id:
        # ここで正しくuser_idが取れてないのはClientで送ってくれたtokenがおかしかったということ
        res["message"] = "your token is wrong"
        return Response(json.dumps(res), status=400, mimetype='application/json')
    # tokenが正しかったのでこれをredisに上書きする
    redis.set(token, user_id)
    # 以下では取ってきたuser_idを用いてデータが以前までになかったら新たにidとnameをMySQLのwearthistodayいうDBのuserテーブルに登録する
    connection = mysql.connect()
    cursor = connection.cursor()
    query = 'select id from user where id='+ user_id
    cursor.execute(query)
    result = cursor.fetchall()
    if not result:
        # ユーザ新規登録
        query = 'insert into user (id, name) values ("' + user_id + '", "' + user_name + '")'
        cursor.execute(query)
        connection.commit()
        res["message"] += "your are a new user.  sign up success. "
    # 回答するJSONをまとめて
    res["message"] += "login success."
    # 以下のようにResponseを用いて返す
    return Response(json.dumps(res), status=200, mimetype='application/json')