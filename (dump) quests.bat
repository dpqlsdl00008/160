@echo off
@title Dump
set CLASSPATH=.;dist\*
java -Dnet.sf.odinms.wzpath=wz tools.wztosql.DumpQuests
pause