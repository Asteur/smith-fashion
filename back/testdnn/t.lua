local images = { }

for id=1,50,1 do
   print (string.format("%02d",id)..".jpg")
   images[id-1] = string.format("%02d",id)..".jpg"
end

print( images )