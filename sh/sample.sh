#!/bin/sh

echo "======ExecStart======"

echo $1

ftp -n <<END
open localhost
user foo1 bar1
cd to

binary
!pwd
!ls ./test/from
put $1 aaa
END
echo "======ExecFTPEnd======"

sleep 5

echo "======ExecEnd======"
