# -*- coding: utf-8 -*-
'''
    db/extension.py
    DBのインスタンスをここで持つ
    このファイルをimportすることで他のapiファイルなどで同じDBインスタンスを用いることが可能
'''
from flaskext.mysql import MySQL
import redis as r
mysql = MySQL()
redis = r.StrictRedis(host='localhost', port=6379, db=0)