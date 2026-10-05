execute store result score @s random run random value 0..11

execute if score @s random matches 0 run effect give @s minecraft:speed 60 0
execute if score @s random matches 1 run effect give @s minecraft:slowness 60 0
execute if score @s random matches 2 run effect give @s minecraft:mining_fatigue 60 0
execute if score @s random matches 3 run effect give @s minecraft:haste 60 0
execute if score @s random matches 4 run effect give @s minecraft:strength 60 0
execute if score @s random matches 5 run effect give @s minecraft:weakness 60 0
execute if score @s random matches 6 run effect give @s minecraft:night_vision 60 0
execute if score @s random matches 7 run effect give @s minecraft:blindness 60 0
execute if score @s random matches 8 run effect give @s minecraft:water_breathing 60 0
execute if score @s random matches 9 run effect give @s minecraft:nausea 60 0
execute if score @s random matches 10 run effect give @s minecraft:wither 60 0
execute if score @s random matches 11 run effect give @s minecraft:regeneration 60 0