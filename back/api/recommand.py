# -*- coding: utf-8 -*-
'''
    api/recommand.py
    ここが服のレコメンドapi
'''
from flask import Blueprint, Response, request, abort
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
    画像処理
'''
import chainer 
from chainer import cuda, Function, gradient_check, report, training, utils, Variable 
from chainer import datasets, iterators, optimizers, serializers 
from chainer import Link, Chain, ChainList 
import chainer.functions as F 
import chainer.links as L 

"""Definition model""" 
class MNIST_Chain(Chain): 
  #パラメータを含む関数の宣言 主に結合について 
  def __init__(self): 
      super(MNIST_Chain, self).__init__( 
          l1=L.Linear(128,1000), 
          l2=L.Linear(1000,1000), 
          l3=L.Linear(1000,1000), 
          l4=L.Linear(1000,1), 
      ) 
  #損失関数(交差エントロピー誤差関数はクラスタリング、最小二乗誤差関数は回帰)の定義 
  def __call__(self,x,y): 
      print ( self.fwd(x).data, y.data )
      return F.mean_squared_error(self.fwd(x), y) 
  #順伝播計算を定義 主に活性化関数について
  def fwd(self,x): 
      h1 = F.relu(self.l1(x)) 
      h2 = F.relu(self.l2(h1)) 
      h3 = F.relu(self.l3(h2)) 
      # h4 = F.softmax(self.l4(h3)) これはクラスタリングに時
      h4 = self.l4(h3)
      return h4[:,0]
      
model = MNIST_Chain() 
serializers.load_npz('./api/my.model', model)

'''
    recommandというBlueprintを生成
    これをmain.pyで読み込んでapiとして登録
'''
recommand = Blueprint('recommand', __name__)

@recommand.record
def record_params(setup_state):
  app = setup_state.app

# /api/recommand にPOSTがきたら以下が動作する
@recommand.route('/recommand', methods=['POST'])
def this_recommand():
    res = {
        "error" : "",
        "result" : []
    }

    # まず、tokenをとる
    token = str(request.json["token"])
    if not token:
        # tokenがないっということはClient側で正しくtokenを渡していないこと
        res["error"] = "you need token. you need login"
        return Response(json.dumps(res), status=400, mimetype='application/json')

    # redisにこのtokenをキーとして持つデータがあるかどうかを確認する
    user_id = redis.get(token)
    if not user_id:
        # tokenがないっということはClient側で正しくtokenを渡していないこと
        res["error"] = "unknown token. you need login"
        return Response(json.dumps(res), status=400, mimetype='application/json')
    
    user_id = user_id.decode("utf-8")

    images = request.json["images"]
    image_num = len(images)
    print ("get ", image_num, " image") 
    image_num = len(images)
    for index in range(image_num):
        print (index)
        images[index] = re.sub('^data:image/.+;base64,', '', images[index])
        image_path = "./tmp/" + user_id +  "_" + str(index) + ".jpeg"
        im = Image.open(BytesIO(base64.b64decode(images[index])))
        im.save(image_path, "JPEG")

    # extract fashion features
    lua_input = ["th" ,"./api/main.lua", user_id, str(image_num)]

    for index in range(image_num):
        lua_input.append("./tmp/" + user_id +  "_" + str(index) + ".jpeg")

    print ("lua command : ", str(lua_input))

    csv_file_path = subprocess.check_output(lua_input).decode("utf-8")
    csv_file_path = re.sub('\t\n', '', csv_file_path)
    print ("csv file name : ", csv_file_path)

    user_data = np.array(np.genfromtxt("./tmp/1185849424834131.csv",delimiter=",")).astype(np.float32)
    user_data = Variable(user_data, volatile='on') 
    user_data_result = model.fwd(user_data) 
    user_data_result = user_data_result.data 
    print( user_data_result )

    res = {
        "error" : "",
        "result" : [{
        "index" : 0,
        "formal" : 0,
        "casual" : 0,
        "kakkoii" : 0,
        "kawaii" : 0
        }]
    }
    return Response(json.dumps(res), status=200, mimetype='application/json')