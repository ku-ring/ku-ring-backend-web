#!/bin/sh

gpg --batch --yes --decrypt --passphrase "$GPG_PASSPHRASE" --output src/main/resources/third/ku-stack-firebase-adminsdk-87nwq-5ba04dfc12.json src/main/resources/third/ku-stack-firebase-adminsdk-87nwq-5ba04dfc12.json.gpg

if [ -f src/main/resources/third/ku-stack-firebase-adminsdk-87nwq-5ba04dfc12.json ]; then
  echo "Decryption Success!"
fi