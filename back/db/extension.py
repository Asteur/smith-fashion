from flaskext.mysql import MySQL
import redis as r
mysql = MySQL()
redis = r.StrictRedis(host='localhost', port=6379, db=0)