If IntelliJ IDEA console produces malformed output, add the following fix (https://stackoverflow.com/a/58886877):

Go to Help > Edit Custom VM options... then add the following option:
```
-Dconsole.encoding=UTF-8
-Dfile.encoding=UTF-8
```
