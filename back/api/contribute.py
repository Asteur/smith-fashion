#!/usr/bin/env python
# -*- coding: utf-8 -*-

'''
    api/contribute.py
    貢献する
'''
from flask import Blueprint, Response, request, abort
import json
import requests
from db.extension import mysql, redis
'''
    contributeというBlueprintを生成
    これをmain.pyで読み込んでapiとして登録
'''
contribute = Blueprint('contribute', __name__)
# /api/contribute にPOSTがきたら以下が動作する
@contribute.route('/contribute', methods=['POST'])
def this_contribute():
    res = {
        "message" : "Success"
    }
    # 以下のようにResponseを用いて返す
    return Response(json.dumps(res), status=200, mimetype='application/json')