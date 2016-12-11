from flask import Blueprint, Response, request, abort
import json
import requests
from db.extension import mysql, redis

signin = Blueprint('signin', __name__)

@signin.route('/signin', methods=['POST'])
def this_signin():
    res = {
        "message" : ""
    }
    token = str(request.json["token"])
    if not token:
        res["message"] = "you need token"
        return Response(json.dumps(res), status=400, mimetype='application/json')

    params = {
        "access_token": token
    }
    url = 'https://graph.facebook.com/me'
    response_facebook = requests.get(url, params=params)
    response_facebook = json.loads(response_facebook.text)
    user_id = response_facebook["id"]
    user_name = response_facebook["name"]
    if not user_id:
        res["message"] = "your token is wrong"
        return Response(json.dumps(res), status=400, mimetype='application/json')
    redis.set(token, user_id)

    connection = mysql.connect()
    cursor = connection.cursor()
    query = 'select id from user where id='+ user_id
    cursor.execute(query)
    result = cursor.fetchall()
    if not result:
        query = 'insert into user (id, name) values ("' + user_id + '", "' + user_name + '")'
        cursor.execute(query)
        connection.commit()
        res["message"] += "your are a new user.  sign up success. "

    res["message"] += "login success."
    return Response(json.dumps(res), status=200, mimetype='application/json')