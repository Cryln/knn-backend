import numpy as np
import sys
import json

def loadFromJson(data):
#   data = json.loads(str1)
  nodeInitmacySum = {}
  ans = {}
  possibility = []
  for index, item in enumerate(data):
    userId = item["userId"]
    friendId = item["friendId"]
    intimacy = item["intimacy"]
    nodeInitmacySum[userId] = intimacy + (0 if userId not in nodeInitmacySum.keys() else nodeInitmacySum[userId])
    # str_to_int[str(userId)+"_to_"+str(friendId)] = index
  for index, item in enumerate(data):
    userId = item["userId"]
    friendId = item["friendId"]
    intimacy = item["intimacy"]
    ans[userId] = {} if userId not in ans.keys() else ans[userId]
    curNode = ans[userId]
    curNode[friendId] = []
    curNode[friendId].append(tuple([index,intimacy/nodeInitmacySum[userId]]))
    possibility.append(intimacy/nodeInitmacySum[userId])
  return ans,np.array(possibility)

def findK(src, graph, K):

  if src not in graph.keys():
    print("没有起始节点")
    return None

  ans = []  #为list，index对应距离（跳数），每个元素为dict（key为终点，value为路径）
  ans.append({src:[()]})
  visited = {src:True}

  step = 1

  next = [src] # 当前层的节点

  while K>0:
    stepAns = {}
    _next = []
    # print(next)
    if len(next)==0:
      break
    for curNode in next: #从next中的节点逐一遍历下一层
      if curNode not in graph.keys():
        continue
      for node in graph[curNode].keys(): #此节点的所有子节点
        if node in visited.keys(): #如果此节点已经访问过，1.存在更短的路径，已经提前找到了，2.存在相同长度的路径
          if node not in stepAns.keys(): #更短的路径已存在，忽略
            continue
          else: #从其他节点，也可以相同step到这个点，记录
            data = stepAns[node]
            preData = ans[step-1][curNode]
            for path in preData:
              for edgeData in graph[curNode][node]:
                data.append(path+tuple([edgeData[0]]))
            stepAns[node] = data
        else: #从未访问过此节点，记录
          data = []
          preData = ans[step-1][curNode]
          for path in preData:
            for edgeData in graph[curNode][node]:
              data.append(path+tuple([edgeData[0]]))
          stepAns[node] = data
          visited[node] = True
          _next.append(node)
          K -= 1
    next = _next
    if len(stepAns.keys())>0:
      ans.append(stepAns)
    step += 1
  return ans

#根据概率生成01向量
#
#随机生成一个0到1的浮点向量X,若X中元素比
#概率向量对应位置的概率值大，则置为0；否则置为1
def genRandomWorld(possibility):
  length = len(possibility)
  res = np.random.rand(length) - possibility
  res[res>0] = 0
  res[res<0] = 1
  res = res.astype(bool)
  return res

#通过抽样计算AB连通概率 vectors 为所有AB之间的通路list
def calVectorP(vectors, possibility,sampleSize):
  #初始化命中次数
  hit = 0
  #迭代抽样
  for i in range(sampleSize):
    #根据概率随机生成一个可能世界
    sampleWorld = genRandomWorld(possibility)
    #逐一检查最短路径列表，如果该可能世界中存在最短路径，hit加一，退出当前循环
    for vector in vectors:
      if ((sampleWorld&vector)==vector).all():
        hit += 1
        break
  return hit/sampleSize


#枚举法计算概率
#输入：vectors:包含所有最短路径向量的列表
#   possibilitiy:剪枝后的不确定图的边概率向量，每个元素代表对于边的概率
def calVectorP0(vectors, possibility):
  N = len(possibility) #获得边的数量
  p = 0 #初始化概率
  for i in range(np.power(2,N)): #遍历所有可能世界,0到2^N
    temp = np.array(list(bin(i)[2:].zfill(N))).astype(bool) #可能世界的向量表示
    for vector in vectors: #遍历所有最短路径，检查其是否在当前可能世界中存在
      if ((temp&vector)==vector).all(): #AND运算判断，存在最短路径
        x = np.copy(possibility)
        x[temp==False] = 1-x[temp==False] #在概率向量中，位于当前可能世界向量为0的位置的元素，即不存在的边，和1做差
        p += np.prod(x) #概率向量所有元素相乘并累加到p
        break
  return p

class Result:

    def __init__(this,id,inti):
        this.friendId = id
        this.intimacy = inti

def candidate(ans,K,friends):
    res = {}
    for i in range(2,len(ans)):
        layer = ans[i]
        amount = 0
        for item in layer.keys():
            if item not in friends:
                res[item] = layer[item]
                amount += 1
        if amount>=K:
            break
    print(res)
    return res

def select(ans, K, possibility,sampleSize,friends):
  edgeNum = len(possibility)
  res = []
#   size = 0
#   for d in ans:
#     size += len(d.keys())
#   overK = size-K-1
  candidates = candidate(ans,K,friends)
  for node in candidates.keys():
    vectors = []
    for path in candidates[node]:
      temp = np.sum(np.eye(edgeNum)[np.array(path).reshape(-1)],axis=0)
      temp = temp.astype(bool)
      vectors.append(temp)
    res.append((node,calVectorP(vectors,possibility,sampleSize)))
    res.sort(key=lambda x:x[1],reverse=True)
    if(len(res)>K):
        res = res[:K]
  return list(map(lambda x:Result(x[0],x[1]).__dict__,res))
if __name__=="__main__":
    result = None
    with open("src/main/resources/static/args.json") as f:
         result=json.load(f)
    print(result)
    srcNode = int(sys.argv[1])
    config = result['arg2']
    input = result['arg3']
    friends = result['arg4']
    print("1")
    g,p = loadFromJson(input)
    print("2")
    ans = select(findK(srcNode,g,config["k"]),config["k"],p,config["sampleSize"],friends)
    print("3")
    output = json.dumps(ans)
    print(output)