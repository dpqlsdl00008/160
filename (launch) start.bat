@echo off
@title driving
set CLASSPATH=.;dist\*;lib\*
java -Dfile.encoding="UTF8" -client -Dnet.sf.odinms.wzpath=wz server.Start
pause
java -Dnashorn.args=--no-deprecation-warning -Xms512m -Xmx30G -Dorg.whitestar.gateway_ip="127.0.0.1" -Dfile.encoding="UTF8" -server server.Start
ping 127.0.0.1 -n 5 > nul
goto gogo