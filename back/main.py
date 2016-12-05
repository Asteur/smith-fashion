from flask import Flask

app = Flask(__name__)
app.config['SECRET_KEY'] = 'secret!'

@app.route('/')
def hello_world():
    return "Wear This TodayサービスのAPIサーバーです。詳細はhttps://github.com/kdrl/Wear-This-Today より確認ください。"

if __name__ == '__main__':
    app.run(port=80,host='0.0.0.0')
