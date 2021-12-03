import re

f = open("./test.log")
content = f.readlines()
wanted = []
pattern = "/.* Profit: [\d]+\.[\d]+/gm"
for i in content:
    if re.search(pattern, i):
        wanted.append(i)
for j in wanted:
    print(j)

print("Over")