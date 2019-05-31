#!/bin/bash

export GPG_TTY=$(tty)

# 发布中央仓库
mvn clean deploy -P release -Dgpg.passphrase=$1  -Dmaven.test.skip=true