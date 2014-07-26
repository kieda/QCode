for each classloader we keep track of the instances that are there. 

because of this we can do something like

SELECT * FROM org.comp.Obj WHERE
     length >= 10
