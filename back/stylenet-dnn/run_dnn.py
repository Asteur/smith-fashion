import numpy as np 
import chainer 
from chainer import cuda, Function, gradient_check, report, training, utils, Variable 
from chainer import datasets, iterators, optimizers, serializers 
from chainer import Link, Chain, ChainList 
import chainer.functions as F 
import chainer.links as L 

"""Set data"""#変える 
xtrain=np.array(np.genfromtxt("./xtrain.csv",delimiter=",")).astype(np.float32) 
ytrain=np.array(np.genfromtxt("./ytrain.csv",delimiter=",")).astype(np.int32) 
xtest=np.array(np.genfromtxt("./xtest.csv",delimiter=",")).astype(np.float32) 
ytest=np.array(np.genfromtxt("./ytest.csv",delimiter=",")).astype(np.int32) 

"""Definition model""" 
class MNIST_Chain(Chain): 
    #パラメータを含む関数の宣言 主に結合について 
    def __init__(self): 
        super(MNIST_Chain, self).__init__( 
            l1=L.Linear(128,2000), 
            l2=L.Linear(2000,2000), 
            l3=L.Linear(2000,2000), 
            l4=L.Linear(2000,2), 
        ) 
    #損失関数(交差エントロピー誤差関数)の定義 
    def __call__(self,x,y): 
        return F.softmax_cross_entropy(self.fwd(x), y) , F.accuracy(self.fwd(x), y)
    #順伝播計算を定義 主に活性化関数について 
    def fwd(self,x): 
        h1 = F.relu(self.l1(x)) 
        h2 = F.relu(self.l2(h1)) 
        h3 = F.relu(self.l3(h2)) 
        h4 = F.softmax(self.l4(h3)) 
        return h4 

"""Initialize model""" 
#モデルのセット 
model = MNIST_Chain() 
#最適化にはAdam 
optimizer = optimizers.Adam() 
#モデルに最適化をセット 
optimizer.setup(model) 

"""Learn and Test""" 
#ミニバッチ法を用いる 
max_epoch = 10 #回繰り返す 
#データサイズ 
n = 450
#バッチサイズ 
bs = 50 
training_error=np.zeros(max_epoch) 
test_error=np.zeros(max_epoch) 
for epoch in range(max_epoch): 
    #何epochかを表示 
    print("epoch: %d" %epoch) 

    #epochごとにテスト誤差を評価 
    #テストデータのセット 
    x = Variable(xtest) 
    y = Variable(ytest) 
    #勾配初期化 
    model.zerograds() 

    #誤差計算 
    # print("$$$",x.data.shape,y.data.shape) 10x128 10x4
    loss, acc = model(x,y) 

    # print("loss: %f" %loss.data, "acc:%f" %acc.data) 

    #プロット用の誤差 
    test_error[epoch]=loss.data 

    #訓練データをミニバッチ法のためシャッフルする 
    sffindx = np.random.permutation(n) 
    for i in range(0, n, bs): 
        #ミニバッチ法用のデータをセット 
        x = Variable(xtrain[i:(i+bs)]) 
        y = Variable(ytrain[i:(i+bs)]) 
        #勾配初期化 
        model.zerograds() 
        #誤差計算 
        loss, acc = model(x,y) 
        #勾配計算 
        loss.backward() 
        #パラメータ更新 
        optimizer.update() 
    #プロット用の誤差 
    training_error[epoch]=loss.data 

 
"""Result""" 
xt = Variable(xtest, volatile='on') 
yy = model.fwd(xt) 
ans = yy.data 
nrow, ncol = ans.shape 
ok = 0 
for i in range(nrow): 
    cls = np.argmax(ans[i,:]) 
    if cls == ytest[i]: 
        ok += 1 

print("accuracy:", ok, "/", nrow, " = ", (ok * 1.0)/nrow) 

"""Plot""" 
import matplotlib.pyplot as plt 
plt.plot(range(max_epoch),training_error,linewidth=3) 
plt.plot(range(max_epoch),test_error,linewidth=3,color="red") 
plt.title("Fashion") 
plt.legend(["training error","test error"],loc="upper right") 
plt.xlim(0,max_epoch) 
plt.xlabel("epoch") 
plt.ylabel("loss") 
plt.savefig("result(acc" + str((ok * 1.0)/nrow) + ").png") 
plt.show()