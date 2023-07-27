import pathlib
import glob
import os

rootdir = pathlib.Path().resolve()
for subdir, dirs, files in os.walk(rootdir):
    test = dirs
    break

for thing in test:
    test = os.path.join(pathlib.Path().resolve(),str(thing))
    all_files = glob.glob(test + "/*.p")
    for file in all_files:
        f = open(file, "r")
        f = f.read()
        i = 0
        leng = len(f)
        j = 0
        while i < leng:
            if f[i] == " ":
                if j % 2 == 0:
                    end = f[i+1:].find(" ") + i
                    temp = str(int(f[i+1:end+1]) - 64)
                    f = f[:i+1] + temp + f[end+1:]
                    leng = len(f)
                j = j + 1
            i = i + 1
        with open(file, 'w') as g:
            g.write(f)                