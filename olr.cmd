@echo off
cls
echo ------------------------------------------------------------
echo            Build for Clarify CRM
echo ------------------------------------------------------------

set DEV_HOME=C:\Paul\Projects\Olr
set BEA_HOME=C:\bea8
set JAVA_HOME=C:\bea8\jdk141_03
set WLS_HOME=%BEA_HOME%\weblogic81
set ANT_HOME=%WLS_HOME%\server


set ANT_CLASSPATH=%ANT_HOME%\lib\ant\ant.jar;%ANT_HOME%\lib\xerces.jar;%ANT_HOME%\lib\jakarta-ant-1.4.1-optional.jar;

set WLS_CP=%WLS_HOME%\server\lib\weblogic.jar;%WLS_HOME%\server\lib\webservices.jar

set CLASSPATH=%WLS_CP%;%ANT_CLASSPATH%;%JAVA_HOME%\bin

echo %CLASSPATH%

IF %1a==cga goto codeGenBatch

%ANT_HOME%\bin\ant -buildfile olrbuild.xml %1 %2 %3

goto end

@rem java -classpath "%ANT_CLASSPATH%" -Dant.home=%ANT_HOME% org.apache.tools.ant.Main -buildfile build.xml %1 %2 %3
echo ------------------------------------------------------------

:codeGenBatch
cg %2

:end
