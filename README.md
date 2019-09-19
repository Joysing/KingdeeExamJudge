前台地址（考试系统）https://github.com/Joysing/KingdeeExam
考试系统依赖于本程序

**Windows**:

```
cd %JAVA_HOME%\include\win32
copy jawt_md.h  ..
copy jni_md.h  ..

cd judger
mvn package -DskipTests
```

**Linux**:

```
cd $JAVA_HOME/include/linux
cp jawt_md.h jni_md.h ..

cd SOURCE_CODE_PATH/judger
mvn package -DskipTests
```
