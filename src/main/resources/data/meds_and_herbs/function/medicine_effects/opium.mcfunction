execute store result score @s random run random value 1..100
execute if predicate meds_and_herbs:has_opium_addiction run scoreboard players set @s random 1
execute if score @s random matches 1..9 run effect give @s meds_and_herbs:opium_addiction 12000 0
execute if score @s random matches 10..100 run effect give @s meds_and_herbs:painkiller 600 0