#!/bin/bash

set -ev

# This script will be used by Travis CI, thus it's able to decrypt the cert and sign the artifacts appropriately

echo $TRAVIS_BRANCH;
echo $TRAVIS_COMMIT_DESCRIPTION;
echo $TRAVIS_EVENT_TYPE;

if [[ "$TRAVIS_COMMIT_DESCRIPTION" != *"maven-release-plugin"* ]];then

    if [ "$TRAVIS_BRANCH" == "master" ];then
        openssl aes-256-cbc -K $encrypted_75d76ac7d458_key -iv $encrypted_75d76ac7d458_iv -in travis-ci/keys/codesigning.asc.enc -out travis-ci/keys/signingkey.asc -d
        gpg --yes --batch --fast-import travis-ci/keys/signingkey.asc  || { echo $0: mvn failed; exit 1; }
    fi

else
  echo "before-deploy: Not running gpg commands due to maven-release-plugin auto commit - this is just for preparation for dev/releases";
fi

