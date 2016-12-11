from flask import Blueprint, request, abort
import json
import requests
from db.extension import mysql, redis

test = Blueprint('test', __name__)

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
    connection = mysql.connect()
    cursor = connection.cursor()
    cursor.execute('''select * from test''')
    columns = cursor.description
    result = []
    for value in cursor.fetchall():
        tmp = {}
        for (index, column) in enumerate(value):
            tmp[columns[index][0]] = column
        result.append(tmp)
    return str(result)

@test.route('/save', methods=['POST'])
def this_save():
    if not request.json:
        abort(400)
    connection = mysql.connect()
    cursor = connection.cursor()
    query = '''insert into test (content) values (" ''' + str(request.json["content"]) + ''' ")'''
    cursor.execute(query)
    connection.commit()

    return "OK"

@test.route('/signin', methods=['POST'])
def this_signin():
    redis.setex(str(request.json["id"]),10,True)
    return "ok"

@test.route('/login', methods=['GET'])
def this_login():
    result = redis.get(str(request.json["id"]))
    if not result:
        result = "not authorized"
    else:
        result = "authorized"
    return result

@test.route('/request', methods=['GET'])
def this_request():
    return requests.get('http://wearthistoday.monotas.com').content

@test.route('/alldata', methods=['POST'])
def this_alldata():
    token = str(request.json["token"])
    if not token:
        abort(400)
    user_id = redis.get(token)
    if not user_id:
        abort(400)
    user_id = user_id.decode("utf-8")
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
    return str(result)