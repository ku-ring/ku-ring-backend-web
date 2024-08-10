#!/bin/sh

gpg --batch --yes --decrypt --passphrase "$GPG_PASSPHRASE" --output src/main/resources/third/ku-stack-firebase-adminsdk-87nwq-ae6a2df931.json src/main/resources/third/ku-stack-firebase-adminsdk-87nwq-ae6a2df931.json.gpg

if [ -f src/main/resources/third/ku-stack-firebase-adminsdk-87nwq-ae6a2df931.json ]; then
  echo "Decryption Success!"
fi
