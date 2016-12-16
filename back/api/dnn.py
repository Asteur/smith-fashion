import numpy as np 
import chainer 
from chainer import cuda, Function, gradient_check, report, training, utils, Variable 
from chainer import datasets, iterators, optimizers, serializers 
from chainer import Link, Chain, ChainList 
import chainer.functions as F 
import chainer.links as L 
import pickle

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
  #損失関数(交差エントロピー誤差関数)の定義 
  def __call__(self,x,y): 
      print ( self.fwd(x).data, y.data )
      # return F.softmax_cross_entropy(self.fwd(x), y) , F.accuracy(self.fwd(x), y)
      return F.mean_squared_error(self.fwd(x), y) 
  #順伝播計算を定義 主に活性化関数について 
  def fwd(self,x): 
      h1 = F.relu(self.l1(x)) 
      h2 = F.relu(self.l2(h1)) 
      h3 = F.relu(self.l3(h2)) 
      # h4 = F.softmax(self.l4(h3)) 
      h4 = self.l4(h3)
      return h4[:,0]

with open("dnn.pkl", "rb") as f: 
 	model = pickle.load(f) 

user_data = np.array(np.genfromtxt("../tmp/1185849424834131.csv",delimiter=",")).astype(np.float32)
user_data = Variable(user_data, volatile='on') 
user_data_result = model.fwd(user_data) 
user_data_result = user_data_result.data 
print( user_data_result )