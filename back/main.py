# coding=utf-8
from flask import Flask, abort, request
import json
# 自作api.pyをロード
import api

app = Flask(__name__)

# "/"に入ってくる時
@app.route('/')
def basic_response():
    return "Wear This TodayサービスのAPIサーバーです。詳細はhttps://github.com/kdrl/Wear-This-Today より確認ください。\n"


# "/api/echo"にPOSTでrequestが来たら以下が発動
@app.route('/api/echo', methods=['POST'])
def echo():
    # もしももらったデータがjsonでなかったら、400を返す。(400 Bad Request)
    if not request.json:
        abort(400)
    # もらったJSONをサーバで表示(debug用)
    print(request.json)
    # JSONを返す。
    return json.dumps(request.json)

@app.route('/api/helloworld', methods=['POST'])
def helloworld():
    # もしももらったデータがjsonでなかったり、numをキーとする値を持ってなかったら、400を返す。(400 Bad Request)
    if not request.json["num"]:
        abort(400)
    # もらったJSONをサーバで表示(debug用)
    print(request.json)
    # api.helloworldを用いて処理した値を返す
    result = api.helloworld(request.json["num"])
    return result

if __name__ == '__main__':
    # run application with debug mode
    app.run(port=3000, host='0.0.0.0', debug=True)
