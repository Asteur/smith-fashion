local nn    = require 'nn'
local image = require 'image'

local model       = torch.load( 'stylenet.t7' )
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

-- trainstdTRAIN

local images = { }

-- initial_picture_id : 始める番号、 last_data : 終わる番号、　ここではテストデータは10個としている
local initial_picture_id = 478
local last_data = 839
local test_data = 10
local size = last_data - initial_picture_id + 1 - test_data

for id = initial_picture_id, initial_picture_id + size - 1, 1 do
   images[id-initial_picture_id] = "kawaii"..string.format("%05d",id)..".jpeg"
end

print( images )

local descriptors = {}
for index = 1, size, 1 do
   print( "convert image"..string.format("%05d",initial_picture_id + index - 1)..".jpeg now..." )
   descriptors[index] = get_descriptor( images[index-1] )
end

local out = assert(io.open("./xtrain.csv", "w")) -- open a file for serialization

splitter = ","
for i = 1, size do
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

-- TEST

local images = { }

local initial_picture_id = initial_picture_id + size
local size = test_data

for id = initial_picture_id, initial_picture_id + size - 1, 1 do
   images[id-initial_picture_id] = "kawaii"..string.format("%05d",id)..".jpeg"
end

print( images )

local descriptors = {}
for index = 1, size, 1 do
   print( "convert image"..string.format("%05d",initial_picture_id + index - 1)..".jpeg now..." )
   descriptors[index] = get_descriptor( images[index-1] )
end

local out = assert(io.open("./xtest.csv", "w")) -- open a file for serialization

splitter = ","
for i = 1, size do
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