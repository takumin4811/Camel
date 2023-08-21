#!/bin/sh

# insert into Routes(routeId,routeType,dstNodeId,dstPath) values ('RT01','LOCAL',0,'/tmp/to');
# insert into Routes(routeId,routeType,dstNodeId,dstPath,isBinary,charset) values ('RT02','FTP','Node01','/to',true,'utf-8');
# insert into Routes(routeId,routeType,dstNodeId,dstPath,isBinary,charset) values ('RT03','FTP','Node01','/to',true,'utf-8');
# insert into Routes(routeId,routeType,dstNodeId,dstPath) values ('RT04','LOCAL',0,'/tmp/to');
# insert into Routes(routeId,routeType,dstNodeId,dstPath) values ('RT05','EXEC',0,'/tmp/to');

# insert into Triggers(triggerFileName,routeId) values ('/tmp/from/testfile.trg','RT01');
# insert into Triggers(triggerFileName,routeId) values ('/tmp/from/testfile2.trg','RT02');
# insert into Triggers(triggerFileName,routeId) values ('from/testfile4.trg','RT04');
# insert into Triggers(triggerFileName,routeId) values ('/tmp/from/ExecType.trg','RT05');

# -NKF Command option -
# -w : UTF8コードを出力（BOM無し）
# -e : EUCコードを出力
# -s : Shift-JISコードを出力
# -j : JISコード(ISO-2022-JP)を出力
# -Lu : unix改行形式(LF)に変換
# -Lw : windows改行形式(CRLF)に変換
# -Lm : mac改行形式(CR)に変換
# -g(--guess) : 文字コード自動判別の結果を表示
# --overwrite : 元のファイルを上書きする
# --version : バージョン情報を表示


touch dummyfile
setopt RM_STAR_SILENT
rm -rf ./from/*
rm -rf ./to/*
cp dummyfile ./from/dummyfile
cp dummyfile ./to/dummyfile

########################## 
echo "!!!Test RT01 from ./test/in to ./test/out By Trg"

echo "======Before======"
ls -l ./to
 
echo "======Execute======"
cp testfile-utf8.dat ./from/testfile.dat
touch ./from/testfile.trg

sleep 7
echo "======Result======"
ls -l ./to
nkf --guess ./to/testfile.dat

rm ./to/testfile.trg
rm ./to/testfile.dat

###############################################
echo "############################"
echo "!!!Test RT02 from /tmp/from to FTPServer By Trg"
sleep 7

echo "======Before======"
ftp -n <<END
open localhost
user foo1 bar1
cd to
binary
put dummyfile 
ls
END

echo "======Execute======"
cp testfile-utf8.dat ./from/testfile2.dat
touch ./from/testfile2.trg

sleep 7
echo "======Result======"
ftp -n <<END
open localhost
user foo1 bar1
cd to
binary
prompt
ls
del testfile2.dat
del testfile2.trg
END


###############################################
echo "############################"
echo "!!!Test RT05 from /tmp/from to ExecCmd By Trg"
sleep 7

echo "======Before======"
ftp -n <<END
open localhost
user foo1 bar1
cd to
binary
del testfile5.dat
ls
END

echo "======Execute======"
cp testfile-utf8.dat ./from/testfile5.dat
touch ./from/testfile5.trg

sleep 7
echo "======Result======"
ftp -n <<END
open localhost
user foo1 bar1
cd to
binary
prompt
ls
del testfile5.dat
END

sleep 1



###############################################
echo "############################"
echo "!!!Test RT01 from ./from to ./to By REST"
sleep 7
echo "======Before======"
ls -l ./to

echo "======Execute======"
cp testfile-utf8.dat ./from/testfile.dat
curl "http://localhost:8080/fromLocal?routeId=RT01&fileName=testfile.dat"
sleep 7

echo "======Result======"
ls -l ./to
rm ./to/testfile.dat
rm ./to/testfile.trg

sleep 1



###############################################
echo "############################"
echo "!!!Test RT03 from ./from to ./to By REST  with Convert euc2utf8 and filename"
sleep 7
echo "======Before======"
ls -l ./to
echo "======Execute======"

cp testfile-euc.dat ./from/testfile3.dat
nkf --guess ./from/*
curl "http://localhost:8080/fromLocal?routeId=RT03&fileName=testfile3.dat"
sleep 7

echo "======Result======"
ls -l ./to
nkf --guess ./to/TEST*


sleep 1
