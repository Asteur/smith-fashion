local nn    = require 'nn'
local image = require 'image'

local model       = torch.load( './api/stylenet.t7' )
local stylenet    = model.stylenet
local trainmean   = model.mean
local trainstd    = model.std

local function loadImage(path)
   local input = image.load(path)
   if input:dim() == 2 then -- 1-channel image loaded as 2D tensor
      input = input:view(1,input:size(1), input:size(2)):repeatTensor(3,1,1)
   elseif input:dim() == 3 and input:size(1) == 1 then -- 1-channel image
      input = input:repeatTensor(3,1,1)
   elseif input:dim() == 3 and input:size(1) == 3 then -- 3-channel image
   elseif input:dim() == 3 and input:size(1) == 4 then -- image with alpha
      input = input[{{1,3},{},{}}]
   else
      print(#input)
      error('not 2-channel or 3-channel image')
   end
   for i=1,3 do -- channels
      input[{{i},{},{}}]:add(-trainmean[i]):div(trainstd[i])
   end
   return input:float()
end

local function get_descriptor( path )
   local I = loadImage( path )
   I = image.scale( I, 256, 384 )
   I = I:reshape( 1, I:size(1), I:size(2), I:size(3) )
   return stylenet:forward( I )[1]:clone()
end

-- arg[1] : user_id
-- arg[2] : データ数
-- arg[3]から続く文字列はimageのpathが書かれております。

local images = { }

local index = 0
local size = arg[2]

for index = 0, size - 1, 1 do
   images[index] = arg[3 + index]
end

local descriptors = {}
for index = 0, size - 1, 1 do
   descriptors[index] = get_descriptor( images[index] )
end

local csvfilename = "./tmp/"..arg[1]..".csv"

local out = assert(io.open(csvfilename, "w")) -- open a file for serialization

splitter = ","
for i = 0, size - 1 do
    for j = 1, 128 do
        out:write(descriptors[i][j])
        if j == 128 then
            out:write("\n")
        else
            out:write(splitter)
        end
    end
end

out:close()

print ( "./tmp/"..arg[1]..".csv" )