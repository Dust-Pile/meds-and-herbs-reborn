execute store result score @s random run random value 1..100
execute if score @s random matches 1..14 run effect give @s meds_and_herbs:mushroom_poisoning 2400 0
execute if score @s random matches 15..100 run effect give @s minecraft:nausea 200 2