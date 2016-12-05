# coding=utf-8
from flask import Flask, abort, request
import json

app = Flask(__name__)

# "/"に入ってくる時
@app.route('/')
def basic_response():
    return "Wear This TodayサービスのAPIサーバーです。詳細はhttps://github.com/kdrl/Wear-This-Today より確認ください。\n"

# "/api/echo"に入ってくる時
# methodsにPOSTを指定すると、POSTリクエストを受けられる
@app.route('/api/echo', methods=['POST'])
def echo():
    # もしももらったデータがjsonでなかったら、400を返す。(400 Bad Request)
    if not request.json:
        abort(400)
    # もらったJSONをサーバで表示(debug用)
    print(request.json)
    # JSONを返す。
    return json.dumps(request.json)

if __name__ == '__main__':
    # run application with debug mode
    app.run(port=3000, host='0.0.0.0', debug=True)
